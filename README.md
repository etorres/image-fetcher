# Image Fetcher

This starts up [LocalStack](https://github.com/localstack/localstack):

```shell script
TMPDIR=/private$TMPDIR docker-compose up
```

(The part `TMPDIR=/private$TMPDIR` is required only in MacOS.)

You can check the status of each service with the following:

```shell script
curl "http://localhost:4566/health?reload"
```

```shell script
aws --endpoint-url=http://localhost:4566 s3 ls
```

## Acknowledges

Thanks to [Unsplash](https://unsplash.com/) for the sample images.

## References

* [How Can I Resize an Image Using Java?](https://www.baeldung.com/java-resize-image)
* [Working with Images in Java](https://www.baeldung.com/java-images)

import java.io.ByteArrayInputStream
import java.net.URL

import better.files._
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import es.eriktorr.image.ApplicationContext
import es.eriktorr.image.core.MimeType
import es.eriktorr.image.fetch.ImageFormats.supportedMimeTypeFrom
import sttp.client._

final class ImageDownloader(applicationContext: ApplicationContext) {
  implicit private[this] val backend: SttpBackend[Identity, Nothing, NothingT] =
    HttpURLConnectionBackend()

  private[this] val s3Client = anS3Client()

  def downloadTo(url: URL, imageDestination: ImageDestination): Either[String, Unit] = {
    val result = basicRequest
      .get(uri"$url")
      .response(asByteArrayAlways)
      .send()
    supportedMimeTypeFrom(result.contentType) match {
      case None => Left(s"Unsupported image ignored: ${url.toString}")
      case Some(mimeType) =>
        Right(
          for {
            inputStream <- new ByteArrayInputStream(result.body).autoClosed
          } s3Client.putObject(
            imageDestination.bucket,
            imageDestination.key,
            inputStream,
            metadataFrom(url, mimeType)
          )
        )
    }
  }

  private[this] def anS3Client(): AmazonS3 = applicationContext.awsConfig match {
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

  private[this] def metadataFrom(url: URL, mimeType: MimeType): ObjectMetadata = {
    val metadata = new ObjectMetadata()
    metadata.setContentType(mimeType.value)
    metadata.addUserMetadata("sourceUrl", s"${url.toString}")
    metadata
  }
}

object ImageDownloader {
  def apply(applicationContext: ApplicationContext): ImageDownloader =
    new ImageDownloader(applicationContext)
}