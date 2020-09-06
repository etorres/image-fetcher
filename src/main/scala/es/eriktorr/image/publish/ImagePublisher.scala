package es.eriktorr.image.publish

import better.files._
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import es.eriktorr.image.AwsConfig

final class ImagePublisher {
  def publish(inputFilename: String, imageDestination: ImageDestination)(
    implicit awsConfig: Option[AwsConfig]
  ): Unit =
    for {
      inputStream <- File(inputFilename).newInputStream.autoClosed
    } s3ClientFrom(awsConfig).putObject(
      imageDestination.bucket,
      imageDestination.key,
      inputStream,
      metadataFrom(imageDestination.metadata)
    )

  private[this] def s3ClientFrom(awsConfig: Option[AwsConfig]): AmazonS3 = awsConfig match {
    case Some(awsConfig) =>
      AmazonS3ClientBuilder
        .standard()
        .withEndpointConfiguration(
          new EndpointConfiguration(awsConfig.serviceEndpoint, awsConfig.signingRegion)
        )
        .withPathStyleAccessEnabled(true)
        .build()
    case None => AmazonS3ClientBuilder.defaultClient()
  }

  private[this] def metadataFrom(imageMetadata: ImageMetadata): ObjectMetadata = {
    val metadata = new ObjectMetadata()
    metadata.setContentType(imageMetadata.mimeType)
    metadata.addUserMetadata("sourceUrl", s"${imageMetadata.sourceUrl.toString}")
    metadata
  }
}

object ImagePublisher {
  def apply(): ImagePublisher = new ImagePublisher()
}
