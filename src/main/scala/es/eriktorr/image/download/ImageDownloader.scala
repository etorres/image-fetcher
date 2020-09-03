package es.eriktorr.image.download

import better.files._
import sttp.client._

final class ImageDownloader {
  implicit private[this] val backend: SttpBackend[Identity, Nothing, NothingT] =
    HttpURLConnectionBackend()

  def download(imageSource: ImageSource, outputFilename: String): Unit =
    basicRequest.get(uri"${imageSource.url}").response(asFile(File(outputFilename).toJava)).send()
}

object ImageDownloader {
  def apply(): ImageDownloader = new ImageDownloader()
}
