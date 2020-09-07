package es.eriktorr.image.publish

trait ImagePublisher {
  def publish(inputFilename: String, imageDestination: ImageDestination): Unit
}
