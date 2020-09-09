package es.eriktorr.image.core.infrastructure

import java.util.concurrent.atomic.AtomicReference

import es.eriktorr.image.download.infrastructure.ImageDownloaderState
import es.eriktorr.image.resize.infrastructure.ThumbnailsMakerState
import org.apache.commons.io.FilenameUtils.getExtension

final class FakeLocalFileSystem(
  imagesRef: AtomicReference[ImageDownloaderState],
  thumbnailsRef: AtomicReference[ThumbnailsMakerState]
) extends LocalFileSystem {
  override def usingTemporaryDirectory[U]()(f: String => U): Unit = f("/tmp")

  override def listFilesIn(dir: String): Iterator[LocalFile] = {
    val images = imagesRef
      .get()
      .images
      .filter {
        case (_, filename) => filename.startsWith(dir)
      }
      .map(_._2)
    val thumbnails = thumbnailsRef
      .get()
      .thumbnails
      .filter {
        case (_, filename) => filename.startsWith(dir)
      }
      .map(_._2)
    (images ++ thumbnails)
      .map(filename => LocalFile(filename, Some(getExtension(filename))))
      .iterator
  }
}
