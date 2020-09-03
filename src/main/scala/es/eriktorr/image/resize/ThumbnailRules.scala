package es.eriktorr.image.resize

final case class ThumbnailRule(thumbnail: Thumbnail, mandatory: Boolean)

object ThumbnailRules {
  val Rules: List[ThumbnailRule] = List(
    ThumbnailRule(thumbnail = Thumbnail160x160, mandatory = true),
    ThumbnailRule(thumbnail = Thumbnail320x320, mandatory = false),
    ThumbnailRule(thumbnail = Thumbnail640x640, mandatory = false),
    ThumbnailRule(thumbnail = Thumbnail960x960, mandatory = false)
  )
}
