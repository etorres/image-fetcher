package es.eriktorr.image.shared.infrastructure

import java.util

import com.amazonaws.services.lambda.runtime._
import org.slf4j.LoggerFactory

trait LambdaRuntimeStubs {
  object AwsLambdaContextStub extends Context {
    override def getAwsRequestId: String = "awsRequestId"

    override def getLogGroupName: String = "logGroupName"

    override def getLogStreamName: String = "logStreamName"

    override def getFunctionName: String = "functionName"

    override def getFunctionVersion: String = "functionVersion"

    override def getInvokedFunctionArn: String = "invokedFunctionArn"

    override def getIdentity: CognitoIdentity = CognitoIdentityStub

    override def getClientContext: ClientContext = ClientContextStub

    override def getRemainingTimeInMillis: Int = 15000

    override def getMemoryLimitInMB: Int = 10

    override def getLogger: LambdaLogger = LambdaLoggerFake
  }

  object CognitoIdentityStub extends CognitoIdentity {
    override def getIdentityId: String = "identityId"

    override def getIdentityPoolId: String = "identityPoolId"
  }

  object ClientContextStub extends ClientContext {
    override def getClient: Client = ClientStub

    override def getCustom: util.Map[String, String] = new java.util.HashMap[String, String]()

    override def getEnvironment: util.Map[String, String] = new java.util.HashMap[String, String]()
  }

  object ClientStub extends Client {
    override def getInstallationId: String = "installationId"

    override def getAppTitle: String = "appTitle"

    override def getAppVersionName: String = "appVersionName"

    override def getAppVersionCode: String = "appVersionCode"

    override def getAppPackageName: String = "appPackageName"
  }

  object LambdaLoggerFake extends LambdaLogger {
    private[this] val logger = LoggerFactory.getLogger(classOf[LambdaLogger])

    override def log(message: String): Unit = logger.info(message)

    override def log(message: Array[Byte]): Unit = logger.info(new String(message))
  }
}
