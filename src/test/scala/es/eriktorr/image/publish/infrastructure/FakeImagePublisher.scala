package es.eriktorr.image.publish.infrastructure

import java.util.concurrent.atomic.AtomicReference

import es.eriktorr.image.publish.{ImageDestination, ImagePublisher}

final case class ImagePublisherState(objects: List[ImageDestination])

final class FakeImagePublisher(stateRef: AtomicReference[ImagePublisherState])
    extends ImagePublisher {
  override def publish(inputFilename: String, imageDestination: ImageDestination): Unit =
    stateRef.updateAndGet(s => s.copy(objects = imageDestination :: s.objects))
}
