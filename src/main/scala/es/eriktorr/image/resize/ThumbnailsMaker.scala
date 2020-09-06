package es.eriktorr.image.resize

import better.files._
import javax.imageio.ImageIO
import net.coobird.thumbnailator.Thumbnails
import org.apache.commons.io.FilenameUtils.{concat, getBaseName}

final class ThumbnailsMaker {
  def thumbnailsFor(
    inputFilename: String,
    outputDirectory: String,
    filter: ThumbnailRule => Boolean
  ): Unit =
    ThumbnailRules.rules.filter(filter).foreach { rule =>
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

object ThumbnailsMaker {
  def apply(): ThumbnailsMaker = new ThumbnailsMaker()
}
