package es.eriktorr.image.resize.infrastructure

import java.util.concurrent.atomic.AtomicReference

import es.eriktorr.image.resize.{ThumbnailRule, ThumbnailRules, Thumbnailator}

final case class ThumbnailsMakerState(thumbnails: List[(String, String)])

final class FakeThumbnailsMaker(stateRef: AtomicReference[ThumbnailsMakerState])
    extends ThumbnailsMaker {
  override def thumbnailsFor(
    inputFilename: String,
    outputDirectory: String,
    filter: ThumbnailRule => Boolean
  ): Unit = ThumbnailRules.rules.filter(filter).foreach { rule =>
    stateRef.updateAndGet(s =>
      s.copy(thumbnails =
        (
          inputFilename,
          Thumbnailator.outputPathnameFrom(inputFilename, outputDirectory, rule.thumbnail)
        ) :: s.thumbnails
      )
    )
  }
}
