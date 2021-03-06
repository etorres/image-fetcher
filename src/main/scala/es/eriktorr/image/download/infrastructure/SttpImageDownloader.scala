package es.eriktorr.image.download.infrastructure

import java.net.URL

import better.files._
import es.eriktorr.image.download.ImageDownloader
import sttp.client._

final class SttpImageDownloader extends ImageDownloader {
  implicit private[this] val backend: SttpBackend[Identity, Nothing, NothingT] =
    HttpURLConnectionBackend()

  /**
   * As the handling of response is specified upfront, there's no need to "consume" the response body.
   * It can be safely discarded if not needed.
   * More info: https://sttp.softwaremill.com/en/latest/responses/body.html
   */
  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements", "org.wartremover.warts.Any"))
  override def download(url: URL, outputFilename: String): Unit =
    basicRequest
      .get(uri"$url")
      .response(asFile(File(outputFilename).toJava))
      .send()
}

object SttpImageDownloader {
  def apply(): SttpImageDownloader = new SttpImageDownloader()
}
