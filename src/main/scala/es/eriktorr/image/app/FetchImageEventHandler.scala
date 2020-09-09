package es.eriktorr.image.app

import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import es.eriktorr.image.ApplicationContext
import es.eriktorr.image.core.ImageFormats.{imageFormatFrom, mimeTypeFrom}
import es.eriktorr.image.core.infrastructure.{BetterFilesLocalFileSystem, LocalFileSystem}
import es.eriktorr.image.download.FetchImageJsonProtocol._
import es.eriktorr.image.download.infrastructure.SttpImageDownloader
import es.eriktorr.image.download.{ImageDownloader, ImageSource}
import es.eriktorr.image.publish.infrastructure.AmazonS3ImagePublisher
import es.eriktorr.image.publish.{ImageDestination, ImageMetadata, ImagePublisher}
import es.eriktorr.image.resize.ThumbnailRules.mandatoryThumbnails
import es.eriktorr.image.resize.Thumbnailator
import es.eriktorr.image.resize.infrastructure.ThumbnailsMaker
import org.apache.commons.io.FilenameUtils.{concat, getExtension}
import spray.json._

import scala.jdk.CollectionConverters._

final class FetchImageEventHandler(
  applicationContext: ApplicationContext,
  localFileSystem: LocalFileSystem,
  imageDownloader: ImageDownloader,
  thumbnailsMaker: ThumbnailsMaker,
  imagePublisher: ImagePublisher
) extends RequestHandler[SQSEvent, Unit] {
  private[this] def this(applicationContext: ApplicationContext) = {
    this(
      applicationContext,
      BetterFilesLocalFileSystem(),
      SttpImageDownloader(),
      Thumbnailator(),
      AmazonS3ImagePublisher(applicationContext.awsConfig)
    )
  }

  def this() = {
    this(ApplicationContext())
  }

  override def handleRequest(sqsEvent: SQSEvent, awsLambdaContext: Context): Unit =
    localFileSystem.usingTemporaryDirectory() { tempDir =>
      sqsEvent.getRecords.asScala.foreach { sqsEvent =>
        val imageSource = sqsEvent.getBody.parseJson.convertTo[ImageSource]
        val (site, id, extension) = deconstruct(imageSource)
        imageFormatFrom(extension) match {
          case Some(_) =>
            val workingDir = concat(tempDir, s"$site/$id")
            val filename = concat(workingDir, s"$id.$extension")
            imageDownloader.download(imageSource.url, filename)
            thumbnailsMaker.thumbnailsFor(filename, workingDir, mandatoryThumbnails)
            localFileSystem.listFilesIn(workingDir).foreach { file =>
              imagePublisher.publish(
                file.pathAsString,
                ImageDestination(
                  bucket = applicationContext.destinationBucket,
                  key = s"$site/${file.name}",
                  metadata = ImageMetadata(imageSource.url, mimeTypeFrom(file.`extension`))
                )
              )
            }
          case None =>
            awsLambdaContext.getLogger.log(s"Unsupported image ignored: ${imageSource.toString}")
        }
      }

      def deconstruct(imageSource: ImageSource): (String, String, String) = {
        val imageId = imageSource.imageId.value
        val country = imageSource.site.country.value
        val verticalMarket = imageSource.site.verticalMarket.value
        val extension = getExtension(imageSource.url.getPath)
        (s"$country/$verticalMarket", imageId, extension)
      }
    }
}
