package es.eriktorr.image.download

import java.net.URL

trait ImageDownloader {
  def download(url: URL, outputFilename: String): Unit
}
