package es.eriktorr.image.fetch

import java.net.URL

import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import es.eriktorr.image.ApplicationContext
import es.eriktorr.image.fetch.FetchImageJsonProtocol._
import es.eriktorr.image.resize.ThumbnailRules.mandatoryThumbnails
import es.eriktorr.image.resize.ThumbnailsMaker
import org.apache.commons.io.FilenameUtils.{concat, getExtension}
import spray.json._

import scala.jdk.CollectionConverters._

final class FetchImageEventHandler(
  applicationContext: ApplicationContext,
  imageDownloader: ImageDownloader,
  thumbnailsMaker: ThumbnailsMaker
) extends RequestHandler[SQSEvent, Unit] {
  def this() = {
    this(
      applicationContext = ApplicationContext(),
      imageDownloader = ImageDownloader(),
      thumbnailsMaker = ThumbnailsMaker()
    )
  }

  override def handleRequest(sqsEvent: SQSEvent, context: Context): Unit =
    sqsEvent.getRecords.asScala.foreach { sqsEvent =>
      val imageSource = sqsEvent.getBody.parseJson.convertTo[ImageSource]
      val imageLocalPath = concat(
        applicationContext.localFileSystem.baseOutputDirectory,
        filenameFrom(imageSource)
      )
      imageDownloader.download(imageSource.sourceUrl, imageLocalPath)
      thumbnailsMaker.thumbnailsFor(
        imageLocalPath,
        applicationContext.localFileSystem.baseOutputDirectory,
        mandatoryThumbnails
      )
    }

  private[this] def filenameFrom(imageSource: ImageSource): String =
    s"${imageSource.imageId.value}.${getExtension(new URL(imageSource.sourceUrl.value).getPath)}"
}
