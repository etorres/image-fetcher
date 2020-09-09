package es.eriktorr.image.app

import es.eriktorr.image.shared.infrastructure.FakeImageFetcherContext.withImageFetcherContext
import es.eriktorr.image.shared.infrastructure.ImageFetcherState.{
  initialImageFetcherState,
  sqsEvent
}
import es.eriktorr.image.shared.infrastructure.LambdaRuntimeStubs
import es.eriktorr.image.specs.BaseAnySpec

final class FetchImageEventHandlerTest extends BaseAnySpec with LambdaRuntimeStubs {
  "Fetch image event handler" - {
    "should create and publish an image with its thumbnails" in {
      val (finalState, testResult) = withImageFetcherContext(initialImageFetcherState)(
        _.handleRequest(sqsEvent, AwsLambdaContextStub)
      )
      assert(testResult.contains(()) && finalState === initialImageFetcherState) // TODO
    }
  }
}
