package es.eriktorr.image.resize.infrastructure

import es.eriktorr.image.resize.ThumbnailRule

trait ThumbnailsMaker {
  def thumbnailsFor(
    inputFilename: String,
    outputDirectory: String,
    filter: ThumbnailRule => Boolean
  ): Unit
}
