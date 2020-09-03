package es.eriktorr.image.resize

import better.files._
import eu.timepit.refined.auto._
import javax.imageio.ImageIO
import net.coobird.thumbnailator.Thumbnails
import org.apache.commons.io.FilenameUtils.{concat, getBaseName}

// Needed to filter thumbnails based on default rule
@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
final class ThumbnailsMaker {
  def thumbnailsFor(
    inputFilename: String,
    outputDirectory: String,
    filter: ThumbnailRule => Boolean = _.mandatory
  ): Unit =
    ThumbnailRules.Rules.filter(filter).foreach { rule =>
      rule.thumbnail match {
        case format: ThumbnailFormat =>
          for {
            inputStream <- inputFilename.toFile.newInputStream.autoClosed
            outputStream <- outputPathnameFrom(inputFilename, outputDirectory, format).toFile.newOutputStream.autoClosed
          } yield {
            Thumbnails
              .of(ImageIO.read(inputStream))
              .size(format.dimensions.width, format.dimensions.height)
              .outputFormat(format.format)
              .outputQuality(format.quality)
              .toOutputStream(outputStream)
            outputStream.flush()
          }
      }
    }

  private[resize] def outputPathnameFrom(
    inputPathname: String,
    outputDirectory: String,
    thumbnail: Thumbnail
  ): String =
    thumbnail match {
      case format: ThumbnailFormat =>
        concat(
          outputDirectory,
          s"${getBaseName(inputPathname)}-${format.dimensions.width.toString}x${format.dimensions.height.toString}.jpg"
        )
    }
}
