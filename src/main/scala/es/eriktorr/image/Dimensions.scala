package es.eriktorr.image

import eu.timepit.refined.api.Refined

final case class Dimensions(width: MaxWidth8K, height: MaxHeight8K)

object Dimensions {
  def apply(size: Int): Dimensions =
    Dimensions(Refined.unsafeApply(size), Refined.unsafeApply(size))
}
