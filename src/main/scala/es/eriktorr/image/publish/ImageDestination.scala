package es.eriktorr.image.publish

import java.net.URL

final case class ImageMetadata(sourceUrl: URL, mimeType: String)

final case class ImageDestination(bucket: String, key: String, metadata: ImageMetadata)
