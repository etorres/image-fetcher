package es.eriktorr.image.fetch

import es.eriktorr.image.core.{GifImage, ImageFormat, JpegImage, PngImage}

object ImageFormats {
  private[this] val allSupported: List[ImageFormat] = List(GifImage, PngImage, JpegImage)

  def supportedFormat(extension: String): Option[ImageFormat] = {
    val extensionInLowerCase = extension.toLowerCase
    allSupported.find(_.extensions.contains(extensionInLowerCase))
  }
}
