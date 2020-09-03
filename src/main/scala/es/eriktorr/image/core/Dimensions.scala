package es.eriktorr.image.core

import es.eriktorr.image.{core, MaxHeight8K, MaxWidth8K}
import eu.timepit.refined.api.Refined

final case class Dimensions(width: MaxWidth8K, height: MaxHeight8K)

object Dimensions {
  def apply(size: Int): Dimensions =
    core.Dimensions(Refined.unsafeApply(size), Refined.unsafeApply(size))
}
