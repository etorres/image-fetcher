package es.eriktorr.image.core

abstract class MimeType(val mimeType: String, val extensions: List[String])

sealed trait ImageFormat extends MimeType

case object GifImage
    extends MimeType(mimeType = "image/gif", extensions = List("gif"))
    with ImageFormat
case object JpegImage
    extends MimeType(mimeType = "image/jpeg", extensions = List("jpeg", "jpg"))
    with ImageFormat
case object PngImage
    extends MimeType(mimeType = "image/png", extensions = List("png"))
    with ImageFormat
case object UnknownFormat
    extends MimeType(mimeType = "application/octet-stream", extensions = List.empty)
    with ImageFormat
