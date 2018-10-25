import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler

import scala.concurrent.{Future, Promise}

package object client {

  class AwsAsyncPromiseHandler[R <: AmazonWebServiceRequest, T](promise: Promise[T]) extends AsyncHandler[R, T] {
    def onError(e: Exception): Unit = promise failure e
    def onSuccess(r: R, t: T): Unit = promise success t
  }

  def asFuture[R <: AmazonWebServiceRequest, T](underlyingSdkMethod: (R, AsyncHandler[R, T]) => java.util.concurrent.Future[T]): R => Future[T] = { awsRequest =>
    val p = Promise[T]()
    underlyingSdkMethod(awsRequest, new AwsAsyncPromiseHandler(p))
    p.future
  }
}
