package es.eriktorr.image.resize

import es.eriktorr.image._
import es.eriktorr.image.core.Dimensions
import eu.timepit.refined.auto._

sealed trait Thumbnail
abstract private[resize] class ThumbnailFormat(
  val dimensions: Dimensions,
  val quality: Quality,
  val format: String
) extends Thumbnail

case object Thumbnail160x160
    extends ThumbnailFormat(dimensions = Dimensions(160), quality = 0.85, format = "JPEG")
case object Thumbnail320x320
    extends ThumbnailFormat(dimensions = Dimensions(320), quality = 0.75, format = "JPEG")
case object Thumbnail640x640
    extends ThumbnailFormat(dimensions = Dimensions(640), quality = 0.75, format = "JPEG")
case object Thumbnail960x960
    extends ThumbnailFormat(dimensions = Dimensions(960), quality = 0.75, format = "JPEG")
