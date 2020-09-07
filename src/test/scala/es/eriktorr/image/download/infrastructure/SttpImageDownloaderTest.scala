package es.eriktorr.image.download.infrastructure

import java.net.URL

import better.files._
import com.github.tomakehurst.wiremock.client.WireMock._
import es.eriktorr.image.specs.{HttpServerSpec, ResourceSpec}
import org.apache.commons.io.FilenameUtils.concat

final class SttpImageDownloaderTest extends HttpServerSpec with ResourceSpec {
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
        val outputFilename = concat(tempDir.pathAsString, "es_cars/image1/image1.png")

        SttpImageDownloader().download(
          url = new URL(s"http://localhost:${port().toString}$pathToImage"),
          outputFilename = outputFilename
        )

        verifyGetRequestTo(pathToImage)

        assert(File(pathTo(resourceImage)).isSameContentAs(File(outputFilename)))
      }
    }
  }
}
