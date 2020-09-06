package es.eriktorr.image.fetch

import java.net.URL
import java.util.Locale.IsoCountryCode

import spray.json._

final case class ImageId(value: String)

final case class VerticalMarket(value: String)

final case class Site(country: IsoCountryCode, verticalMarket: VerticalMarket)

final case class ImageSource(imageId: ImageId, site: Site, url: URL)

object FetchImageJsonProtocol extends DefaultJsonProtocol {
  implicit def imageIdFormat: RootJsonFormat[ImageId] = jsonFormat1(ImageId)

  implicit def verticalMarketFormat: RootJsonFormat[VerticalMarket] = jsonFormat1(VerticalMarket)

  implicit object CountryJsonFormat extends RootJsonFormat[IsoCountryCode] {
    def write(countryCode: IsoCountryCode): JsString = JsString(countryCode.toString)

    def read(value: JsValue): IsoCountryCode = value match {
      case JsString(countryCode) => IsoCountryCode.valueOf(countryCode)
      case _ => deserializationError("Country code expected")
    }
  }

  implicit object UrlFormat extends RootJsonFormat[URL] {
    override def write(url: URL): JsString = JsString(url.toString)

    override def read(value: JsValue): URL = value match {
      case JsString(url) => new URL(url)
      case _ => deserializationError("URL expected")
    }
  }

  implicit def siteFormat: RootJsonFormat[Site] = jsonFormat2(Site)

  implicit def sourceImageFormat: RootJsonFormat[ImageSource] = jsonFormat3(ImageSource)
}
