package es.eriktorr.image.core

object ImageFormats {
  private[this] val allSupported: List[ImageFormat] = List(GifImage, PngImage, JpegImage)

  def imageFormatFrom(extension: String): Option[ImageFormat] = {
    val extensionInLowerCase = extension.toLowerCase
    allSupported.find(_.extensions.contains(extensionInLowerCase))
  }

  def mimeTypeFrom(extension: Option[String]): String =
    (`extension` match {
      case Some(x) => imageFormatFrom(x).getOrElse(UnknownFormat)
      case None => UnknownFormat
    }).mimeType
}
