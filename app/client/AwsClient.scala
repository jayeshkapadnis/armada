package client

import java.net.InetAddress

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.s3.S3Settings
import akka.stream.alpakka.s3.scaladsl.{MultipartUploadResult, S3Client}
import akka.stream.scaladsl.Sink
import akka.util.ByteString
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressResult
import javax.inject._
import models.SpotInstanceRequest

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class AwsClient @Inject()(system: ActorSystem, materializer: Materializer){

  private val awsSettings = S3Settings()(system)
  private val s3Client = new S3Client(awsSettings)(system, materializer)
  private val ec2Client = EC2Client(awsSettings.credentialsProvider, awsSettings.s3RegionProvider.getRegion)

  def s3Sink(bucketName: String, bucketKey: String): Sink[ByteString, Future[MultipartUploadResult]] =
    s3Client.multipartUpload(bucketName, bucketKey)

  def createSecurityGroup(groupName: String, description: String): Future[AuthorizeSecurityGroupIngressResult] = {
    ec2Client.createSecurityGroup(groupName, description).flatMap{ r =>
      Try(InetAddress.getLocalHost) match {
        case Success(address) =>
          ec2Client.authorizeSecurityGroupIngress(s"${address.getHostAddress}/10", r.getGroupId)

        case Failure(e) => sys.error(s"Error while getting local host: [${e.getMessage}]")
      }
    }
  }

  def createSpotInstance(request: SpotInstanceRequest): Unit ={
    ec2Client.createSpotInstance(request)
  }

}
