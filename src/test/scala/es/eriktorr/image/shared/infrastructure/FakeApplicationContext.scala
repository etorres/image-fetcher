package es.eriktorr.image.shared.infrastructure

import es.eriktorr.image.{ApplicationContext, AwsConfig}

trait FakeApplicationContext {
  val applicationContext: ApplicationContext = ApplicationContext(
    destinationBucket = "images",
    awsConfig =
      Some(AwsConfig(serviceEndpoint = "http://localhost:4566", signingRegion = "us-east-1"))
  )
}
