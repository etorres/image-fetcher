package es.eriktorr.image

final case class AwsConfig(serviceEndpoint: String, signingRegion: String)

final case class ApplicationContext(
  destinationBucket: String,
  awsConfig: Option[AwsConfig]
)

object ApplicationContext {
  def apply(): ApplicationContext = ???
  // TODO: IMAGE_FETCHER_DESTINATION_BUCKET

  def apply(
    destinationBucket: String,
    awsConfig: Option[AwsConfig]
  ): ApplicationContext = new ApplicationContext(destinationBucket, awsConfig)
}
