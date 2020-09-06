package es.eriktorr.image.shared.infrastructure

import com.amazonaws.services.lambda.runtime.LambdaLogger
import es.eriktorr.image.{ApplicationContext, AwsConfig}
import org.slf4j.LoggerFactory

trait FakeApplicationContext {
  val applicationContext: ApplicationContext =
    ApplicationContext(
      destinationBucket = "images",
      awsConfig =
        Some(AwsConfig(serviceEndpoint = "http://localhost:4566", signingRegion = "us-east-1"))
    )

  val logger: LambdaLogger = new LambdaLogger() {
    private[this] val _logger = LoggerFactory.getLogger(classOf[LambdaLogger])

    override def log(message: String): Unit = _logger.info(message)

    override def log(message: Array[Byte]): Unit = _logger.info(new String(message))
  }
}
