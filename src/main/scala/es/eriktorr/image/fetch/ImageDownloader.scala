package es.eriktorr.image.fetch

import better.files._
import sttp.client._
import es.eriktorr.image._

final class ImageDownloader {
  implicit private[this] val backend: SttpBackend[Identity, Nothing, NothingT] =
    HttpURLConnectionBackend()

  /**
   * As the handling of response is specified upfront, there's no need to "consume" the response body.
   * It can be safely discarded if not needed.
   * More info: https://sttp.softwaremill.com/en/latest/responses/body.html
   */
  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements", "org.wartremover.warts.Any"))
  def download(url: Url, outputFilename: String): Unit =
    basicRequest
      .get(uri"$url")
      .response(asFile(File(outputFilename).toJava))
      .send()
}

object ImageDownloader {
  def apply(): ImageDownloader = new ImageDownloader()
}
