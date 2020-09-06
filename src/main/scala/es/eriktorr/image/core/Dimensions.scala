package es.eriktorr.image.core

import es.eriktorr.image._

final case class Dimensions(width: Width, height: Height)

object Dimensions {
  def apply(size: Int): Dimensions = Dimensions(size, size)
}
