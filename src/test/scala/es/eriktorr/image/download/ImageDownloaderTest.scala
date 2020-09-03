package es.eriktorr.image.download

import better.files._
import com.github.tomakehurst.wiremock.client.WireMock._
import es.eriktorr.image.specs.{HttpServerSpec, ResourceSpec}
import eu.timepit.refined.api.Refined.unsafeApply
import org.apache.commons.io.FilenameUtils.concat

final class ImageDownloaderTest extends HttpServerSpec with ResourceSpec {
  private[this] val pathToImage = "/assets/img/image1.png"
  private[this] val resourceImage = "images/image1.png"

  "Image downloader should" - {
    "download images from source URL" in {
      stubFor(
        get(pathToImage)
          .willReturn(
            aResponse()
              .withStatus(200)
              .withStatusMessage("OK")
              .withHeader("Content-Type", "image/png")
              .withBody(byteArrayFrom(resourceImage))
          )
      )

      File.usingTemporaryDirectory() { tempDir =>
        val outputFilename = concat(tempDir.pathAsString, "image1.png")

        ImageDownloader().download(
          ImageSource(url = unsafeApply(s"http://localhost:${port()}$pathToImage")),
          outputFilename
        )

        assert(File(pathTo(resourceImage)).isSameContentAs(File(outputFilename)))
      }
    }
  }
}
