package es.eriktorr.image.core.infrastructure

final case class LocalFile(pathAsString: String, name: String, extension: Option[String])

trait LocalFileSystem {
  def usingTemporaryDirectory[U]()(f: String => U): Unit

  def listFilesIn(dir: String): Iterator[LocalFile]
}

final class BetterFilesLocalFileSystem extends LocalFileSystem {
  import better.files._

  override def usingTemporaryDirectory[U]()(f: String => U): Unit =
    File.usingTemporaryDirectory()(tempDir => f(tempDir.pathAsString))

  override def listFilesIn(dir: String): Iterator[LocalFile] =
    File(dir).list(_.isRegularFile).map(f => LocalFile(f.pathAsString, f.name, f.`extension`))
}

object BetterFilesLocalFileSystem {
  def apply(): BetterFilesLocalFileSystem = new BetterFilesLocalFileSystem()
}
