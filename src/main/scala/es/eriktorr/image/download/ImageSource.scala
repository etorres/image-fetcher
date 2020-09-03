package es.eriktorr.image.download

import eu.timepit.refined.api.Refined
import eu.timepit.refined.string._

final case class ImageSource(url: String Refined Url)
