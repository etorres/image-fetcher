package es.eriktorr.image.fetch

import better.files._
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.lambda.runtime.{Context, RequestHandler}
import es.eriktorr.image.{ApplicationContext, AwsConfig}
import es.eriktorr.image.fetch.FetchImageJsonProtocol._
import es.eriktorr.image.fetch.ImageFormats.supportedFormat
import es.eriktorr.image.publish.{ImageDestination, ImageMetadata, ImagePublisher}
import es.eriktorr.image.resize.ThumbnailRules.mandatoryThumbnails
import es.eriktorr.image.resize.ThumbnailsMaker
import org.apache.commons.io.FilenameUtils.{concat, getExtension}
import spray.json._

import scala.jdk.CollectionConverters._

final class FetchImageEventHandler(
  applicationContext: ApplicationContext,
  imageDownloader: ImageDownloader,
  thumbnailsMaker: ThumbnailsMaker,
  imagePublisher: ImagePublisher
) extends RequestHandler[SQSEvent, Unit] {
  def this() = {
    this(
      applicationContext = ApplicationContext(),
      imageDownloader = ImageDownloader(),
      thumbnailsMaker = ThumbnailsMaker(),
      imagePublisher = ImagePublisher()
    )
  }

  override def handleRequest(sqsEvent: SQSEvent, context: Context): Unit =
    File.usingTemporaryDirectory() { tempDir =>
      sqsEvent.getRecords.asScala.foreach { sqsEvent =>
        val imageSource = sqsEvent.getBody.parseJson.convertTo[ImageSource]
        val (filename, extension) = tempFilenameFrom(imageSource)
        supportedFormat(extension) match {
          case Some(imageFormat) =>
            imageDownloader.download(imageSource.url, filename)
            thumbnailsMaker.thumbnailsFor(
              filename,
              tempDir.pathAsString,
              mandatoryThumbnails
            )
            implicit val awsConfig: Option[AwsConfig] = applicationContext.awsConfig
            imagePublisher.publish(
              filename,
              ImageDestination(
                applicationContext.destinationBucket,
                s3KeyFrom(imageSource, extension),
                ImageMetadata(imageSource.url, imageFormat.mimeType)
              )
            )
          case None =>
            context.getLogger.log(s"Unsupported image ignored: ${imageSource.toString}")
        }
      }

      def tempFilenameFrom(imageSource: ImageSource): (String, String) = {
        val extension = getExtension(imageSource.url.getPath)
        (concat(tempDir.pathAsString, s"${imageSource.imageId.value}.$extension"), extension)
      }

      def s3KeyFrom(imageSource: ImageSource, extension: String): String =
        s"${imageSource.site.country
          .name()}/${imageSource.site.verticalMarket.value}/${imageSource.imageId.value}.$extension"
    }
}
