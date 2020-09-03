package es.eriktorr.image.specs

import better.files._

trait ResourceSpec {
  def pathTo(resource: String): String = getClass.getClassLoader.getResource(resource).getPath

  def byteArrayFrom(resource: String): Array[Byte] = File(pathTo(resource)).loadBytes
}
