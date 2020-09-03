package es.eriktorr.image.specs

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import org.scalatest.BeforeAndAfterEach

trait HttpServerSpec extends BaseAnySpec with BeforeAndAfterEach {
  private[this] val wireMockServer = new WireMockServer(
    options().dynamicPort()
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
    wireMockServer.start()
  }

  override def afterEach(): Unit = {
    wireMockServer.stop()
    super.afterEach()
  }

  def port(): Int = wireMockServer.port()

  // Needed to support calls to the fluent interface in the Java API: WireMockServer
  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  def stubFor(mappingBuilder: MappingBuilder): Unit =
    wireMockServer.stubFor(mappingBuilder)

  def verifyGetRequestTo(path: String): Unit =
    wireMockServer.verify(getRequestedFor(urlEqualTo(path)))
}
