package es.eriktorr.image.resize

import better.files._
import org.apache.commons.io.FilenameUtils.getName
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.prop.TableDrivenPropertyChecks

final class ThumbnailsMakerTest
    extends AnyFreeSpec
    with TypeCheckedTripleEquals
    with TableDrivenPropertyChecks {

  private[this] val pathTo: String => String = {
    getClass.getClassLoader.getResource(_).getPath
  }

  private[this] val mandatoryOnly: ThumbnailRule => Boolean = _.mandatory
  private[this] val all: ThumbnailRule => Boolean = _ => true

  private[this] val images =
    Table(
      ("image", "filter", "thumbnails"),
      (
        "image1.png",
        all,
        List("image1-160x160.jpg", "image1-320x320.jpg", "image1-640x640.jpg", "image1-960x960.jpg")
      ),
      ("image2.gif", mandatoryOnly, List("image2-160x160.jpg")),
      ("image3.jpg", mandatoryOnly, List("image3-160x160.jpg"))
    )

  "Thumbnails maker should" - {
    "create thumbnail images" in {
      File.usingTemporaryDirectory() { tempDir =>
        val thumbnailsMaker = new ThumbnailsMaker
        forAll(images) { (image, filter, thumbnails) =>
          thumbnailsMaker.thumbnailsFor(pathTo(image), tempDir.path.toString, filter)
          assert(
            tempDir
              .list(file => thumbnails.contains(getName(file.path.toString)))
              .toList
              .size === thumbnails.size
          )
        }
      }
    }
  }
}