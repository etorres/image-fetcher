package es.eriktorr.image.app

import es.eriktorr.image.shared.infrastructure.FakeImageFetcherContext.withImageFetcherContext
import es.eriktorr.image.shared.infrastructure.ImageFetcherState.{
  finalImageFetcherState,
  initialImageFetcherState,
  sqsEvent
}
import es.eriktorr.image.shared.infrastructure.LambdaRuntimeStubs
import es.eriktorr.image.specs.BaseAnySpec

final class FetchImageEventHandlerTest extends BaseAnySpec with LambdaRuntimeStubs {
  "Fetch image event handler" - {
    "should publish images with their thumbnails" in {
      val (finalState, testResult) = withImageFetcherContext(initialImageFetcherState)(
        _.handleRequest(sqsEvent, AwsLambdaContextStub)
      )
      assert(testResult.contains(()) && finalState === finalImageFetcherState)
    }
  }
}
