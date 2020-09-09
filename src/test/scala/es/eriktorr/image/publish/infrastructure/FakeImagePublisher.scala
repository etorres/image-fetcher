package es.eriktorr.image.publish.infrastructure

import java.util.concurrent.atomic.AtomicReference

import es.eriktorr.image.ignoreResult
import es.eriktorr.image.publish.{ImageDestination, ImagePublisher}

final case class ImagePublisherState(objects: List[(String, ImageDestination)])

final class FakeImagePublisher(stateRef: AtomicReference[ImagePublisherState])
    extends ImagePublisher {
  override def publish(inputFilename: String, imageDestination: ImageDestination): Unit =
    ignoreResult(
      (args: (String, ImageDestination)) =>
        args match {
          case (inputFilename, imageDestination) =>
            stateRef.updateAndGet(s =>
              s.copy(objects = (inputFilename, imageDestination) :: s.objects)
            )
        },
      (inputFilename, imageDestination)
    )
}
