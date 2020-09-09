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
    sqsEvent.setRecords(List(sqsMessageFrom(1), sqsMessageFrom(2)).asJava)
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
        List(
          (new URL("http://example.org/image2.png"), "/tmp/es/cars/2/2.png"),
          (new URL("http://example.org/image1.png"), "/tmp/es/cars/1/1.png")
        )
      ),
      ThumbnailsMakerState(
        List(
          ("/tmp/es/cars/2/2.png", "/tmp/es/cars/2/2-160x160.jpg"),
          ("/tmp/es/cars/1/1.png", "/tmp/es/cars/1/1-160x160.jpg")
        )
      ),
      ImagePublisherState(
        List(
          ImageDestination(
            "images",
            "es/cars/2-160x160.jpg",
            ImageMetadata(new URL("http://example.org/image2.png"), "image/jpeg")
          ),
          ImageDestination(
            "images",
            "es/cars/2.png",
            ImageMetadata(new URL("http://example.org/image2.png"), "image/png")
          ),
          ImageDestination(
            "images",
            "es/cars/1-160x160.jpg",
            ImageMetadata(new URL("http://example.org/image1.png"), "image/jpeg")
          ),
          ImageDestination(
            "images",
            "es/cars/1.png",
            ImageMetadata(new URL("http://example.org/image1.png"), "image/png")
          )
        )
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
