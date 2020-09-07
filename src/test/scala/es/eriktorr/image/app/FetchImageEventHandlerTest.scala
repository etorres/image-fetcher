package es.eriktorr.image.app

import es.eriktorr.image.core.infrastructure.BetterFilesLocalFileSystem
import es.eriktorr.image.specs.BaseAnySpec

final class FetchImageEventHandlerTest extends BaseAnySpec {
  "this" - {
    "this" in {
      val localFileSystem = new BetterFilesLocalFileSystem
      localFileSystem.usingTemporaryDirectory()(s => println(s))
    }
  }

  // TODO
}
