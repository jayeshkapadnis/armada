package client

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.ec2.model.{AuthorizeSecurityGroupIngressResult, CreateSecurityGroupRequest, CreateSecurityGroupResult}
import com.amazonaws.services.ec2.{AmazonEC2Async, AmazonEC2AsyncClient}
import models.SecurityGroupIngressRequest

import scala.concurrent.{Future, Promise}

object EC2Client {

  def apply(credentials: AWSCredentialsProvider, region: String): EC2Client = {
    val builder = AmazonEC2AsyncClient.asyncBuilder()
      .withCredentials(credentials)
        .withRegion(region)
    new EC2Client(builder.build())
  }
}



class EC2Client(asyncClient: AmazonEC2Async){

  def createSecurityGroup(groupName: String, description: String): Future[CreateSecurityGroupResult] ={
    val request = new CreateSecurityGroupRequest(groupName, description)
    asFuture(asyncClient.createSecurityGroupAsync)(request)
  }

  def authorizeSecurityGroupIngress(ipAddress: String, securityGroupId: String): Future[AuthorizeSecurityGroupIngressResult] ={
    val request = SecurityGroupIngressRequest(ipAddress, securityGroupId)
    asFuture(asyncClient.authorizeSecurityGroupIngressAsync)(request)
  }

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
