package es.eriktorr.image.download.infrastructure

import java.net.URL
import java.util.concurrent.atomic.AtomicReference

import es.eriktorr.image.download.ImageDownloader

final case class ImageDownloaderState(images: List[(URL, String)])

final class FakeImageDownloader(stateRef: AtomicReference[ImageDownloaderState])
    extends ImageDownloader {
  override def download(url: URL, outputFilename: String): Unit =
    stateRef.updateAndGet(s => s.copy(images = (url, outputFilename) :: s.images))
}
