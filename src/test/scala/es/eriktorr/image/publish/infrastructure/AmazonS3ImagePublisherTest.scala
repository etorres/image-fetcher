package es.eriktorr.image.publish.infrastructure

import java.net.URL

import com.amazonaws.services.s3.model.GetObjectMetadataRequest
import es.eriktorr.image.publish.{ImageDestination, ImageMetadata}
import es.eriktorr.image.shared.infrastructure.FakeApplicationContext
import es.eriktorr.image.specs.{BaseAnySpec, ResourceSpec}

import scala.util.Random

final class AmazonS3ImagePublisherTest
    extends BaseAnySpec
    with ResourceSpec
    with FakeApplicationContext {
  "Image publisher should" - {
    val resourceImage = "images/image1.png"
    val s3ObjectKey = s"es/cars/image_${Random.alphanumeric.take(24).mkString}.png"
    val imagePublisher = AmazonS3ImagePublisher(applicationContext.awsConfig)

    "upload image to bucket in S3" in {
      imagePublisher.publish(
        pathTo(resourceImage),
        ImageDestination(
          applicationContext.destinationBucket,
          s3ObjectKey,
          ImageMetadata(new URL("http://example.org/assets/img/image1.png"), "image/png")
        )
      )

      val objectMetadata = imagePublisher
        .s3ClientFrom(applicationContext.awsConfig)
        .getObjectMetadata(
          new GetObjectMetadataRequest(applicationContext.destinationBucket, s3ObjectKey)
        )

      assert(
        objectMetadata.getContentLength === 1036366L
          && objectMetadata.getContentMD5 === "fvWmWrNzql5zRKHphn6o8A=="
          && objectMetadata.getContentType === "image/png"
          && objectMetadata
            .getUserMetaDataOf("sourceurl") === "http://example.org/assets/img/image1.png"
      )
    }
  }
}
