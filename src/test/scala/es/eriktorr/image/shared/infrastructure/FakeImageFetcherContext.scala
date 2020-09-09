package es.eriktorr.image.shared.infrastructure

import java.net.URL
import java.util.concurrent.atomic.AtomicReference

import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage
import es.eriktorr.image.app.FetchImageEventHandler
import es.eriktorr.image.core.infrastructure.FakeLocalFileSystem
import es.eriktorr.image.download.infrastructure.{FakeImageDownloader, ImageDownloaderState}
import es.eriktorr.image.publish.{ImageDestination, ImageMetadata}
import es.eriktorr.image.publish.infrastructure.{FakeImagePublisher, ImagePublisherState}
import es.eriktorr.image.resize.infrastructure.{FakeThumbnailsMaker, ThumbnailsMakerState}

import scala.util.{Failure, Success, Try}

final case class ImageFetcherState(
  imageDownloaderState: ImageDownloaderState,
  thumbnailsMakerState: ThumbnailsMakerState,
  imagePublisherState: ImagePublisherState
)

object ImageFetcherState {
  private[this] val imageIds = List(1, 2)

  private[this] def sqsMessageFrom(imageId: Int) = {
    val body = s"""{"imageId":{"value":"$imageId"},
                  |"site":{"country":{"value":"es"},"verticalMarket":{"value":"cars"}},
                  |"url": "http://example.org/image$imageId.png"}""".stripMargin
    val sqsMessage = new SQSMessage
    sqsMessage.setBody(body)
    sqsMessage
  }

  val sqsEvent: SQSEvent = {
    import scala.jdk.CollectionConverters._
    val sqsEvent = new SQSEvent
    sqsEvent.setRecords(imageIds.map(id => sqsMessageFrom(id)).asJava)
    sqsEvent
  }

  def initialImageFetcherState: ImageFetcherState =
    ImageFetcherState(
      ImageDownloaderState(List.empty),
      ThumbnailsMakerState(List.empty),
      ImagePublisherState(List.empty)
    )

  def finalImageFetcherState: ImageFetcherState =
    ImageFetcherState(
      ImageDownloaderState(
        imageIds.reverse.map { id =>
          (new URL(s"http://example.org/image$id.png"), s"/tmp/es/cars/$id/$id.png")
        }
      ),
      ThumbnailsMakerState(
        imageIds.reverse.map { id =>
          (s"/tmp/es/cars/$id/$id.png", s"/tmp/es/cars/$id/$id-160x160.jpg")
        }
      ),
      ImagePublisherState(
        imageIds.reverse.flatMap { id =>
          List(
            ImageDestination(
              "images",
              s"es/cars/$id-160x160.jpg",
              ImageMetadata(new URL(s"http://example.org/image$id.png"), "image/jpeg")
            ),
            ImageDestination(
              "images",
              s"es/cars/$id.png",
              ImageMetadata(new URL(s"http://example.org/image$id.png"), "image/png")
            )
          )
        }
      )
    )
}

object FakeImageFetcherContext extends FakeApplicationContext {
  def withImageFetcherContext[A](initialState: ImageFetcherState)(
    runTest: FetchImageEventHandler => A
  ): (ImageFetcherState, Either[Throwable, A]) = {
    val imageDownloaderStateRef = new AtomicReference(initialState.imageDownloaderState)
    val thumbnailsMakerStateRef = new AtomicReference(initialState.thumbnailsMakerState)
    val imagePublisherStateRef = new AtomicReference(initialState.imagePublisherState)
    val fetchImageEventHandler = new FetchImageEventHandler(
      applicationContext,
      new FakeLocalFileSystem(imageDownloaderStateRef, thumbnailsMakerStateRef),
      new FakeImageDownloader(imageDownloaderStateRef),
      new FakeThumbnailsMaker(thumbnailsMakerStateRef),
      new FakeImagePublisher(imagePublisherStateRef)
    )
    val testResult = Try(runTest(fetchImageEventHandler)) match {
      case Failure(exception) => Left(exception)
      case Success(result) => Right(result)
    }
    val finalState = initialState.copy(
      imageDownloaderState = imageDownloaderStateRef.get(),
      thumbnailsMakerState = thumbnailsMakerStateRef.get(),
      imagePublisherState = imagePublisherStateRef.get()
    )
    (finalState, testResult)
  }
}
