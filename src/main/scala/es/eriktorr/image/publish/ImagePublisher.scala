package es.eriktorr.image.publish

import es.eriktorr.image.AwsConfig

trait ImagePublisher {
  def publish(inputFilename: String, imageDestination: ImageDestination)(
    implicit awsConfig: Option[AwsConfig]
  ): Unit
}
