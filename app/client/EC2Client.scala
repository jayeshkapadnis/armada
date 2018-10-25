package client

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.ec2.model._
import com.amazonaws.services.ec2.{AmazonEC2Async, AmazonEC2AsyncClient}
import models.{SecurityGroupIngressRequest, SpotInstanceRequest}

import scala.concurrent.Future

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

  def createSpotInstance(request: SpotInstanceRequest): Future[DescribeSpotInstanceRequestsResult] ={
    asFuture(asyncClient.requestSpotInstancesAsync)(SpotInstanceRequest(request)).flatMap{ r =>
      val describeRequest = new DescribeSpotInstanceRequestsRequest()
        .withSpotInstanceRequestIds(r.getSdkResponseMetadata.getRequestId)
      asFuture(asyncClient.describeSpotInstanceRequestsAsync)(describeRequest)
    }
  }
}
