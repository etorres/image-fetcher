package es.eriktorr.image.publish.infrastructure

import java.io.File

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.model.{ObjectMetadata, PutObjectRequest}
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import es.eriktorr.image.AwsConfig
import es.eriktorr.image.publish.{ImageDestination, ImageMetadata, ImagePublisher}

final class AmazonS3ImagePublisher(awsConfig: Option[AwsConfig]) extends ImagePublisher {
  override def publish(inputFilename: String, imageDestination: ImageDestination): Unit = {
    val transferManager = TransferManagerBuilder
      .standard()
      .withS3Client(s3ClientFrom(awsConfig))
      .withMultipartUploadThreshold(5L * 1024L * 1024L)
      .build()
    val putObjectRequest = new PutObjectRequest(
      imageDestination.bucket,
      imageDestination.key,
      new File(inputFilename)
    ).withMetadata(metadataFrom(imageDestination.metadata))
    transferManager.upload(putObjectRequest).waitForCompletion()
  }

  private[infrastructure] def s3ClientFrom(awsConfig: Option[AwsConfig]): AmazonS3 =
    awsConfig match {
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

object AmazonS3ImagePublisher {
  def apply(awsConfig: Option[AwsConfig]): AmazonS3ImagePublisher =
    new AmazonS3ImagePublisher(awsConfig)
}
