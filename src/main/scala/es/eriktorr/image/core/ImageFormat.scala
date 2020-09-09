package es.eriktorr.image.core

sealed case class ImageFormat(mimeType: String, extensions: List[String])

object ImageFormats {
  private[this] val allSupported: List[ImageFormat] = List(
    ImageFormat(mimeType = "image/gif", extensions = List("gif")),
    ImageFormat(mimeType = "image/jpeg", extensions = List("jpeg", "jpg")),
    ImageFormat(mimeType = "image/png", extensions = List("png"))
  )

  private[this] val unknownFormat =
    ImageFormat(mimeType = "application/octet-stream", extensions = List.empty)

  def imageFormatFrom(extension: String): Option[ImageFormat] = {
    val extensionInLowerCase = extension.toLowerCase
    allSupported.find(_.extensions.contains(extensionInLowerCase))
  }

  def mimeTypeFrom(extension: Option[String]): String =
    (`extension` match {
      case Some(x) => imageFormatFrom(x).getOrElse(unknownFormat)
      case None => unknownFormat
    }).mimeType
}
