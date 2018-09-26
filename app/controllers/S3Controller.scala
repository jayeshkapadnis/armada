package controllers

import akka.stream.alpakka.s3.scaladsl.MultipartUploadResult
import client.AwsClient
import javax.inject._
import play.api.libs.streams.Accumulator
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc._
import play.core.parsers.Multipart
import play.core.parsers.Multipart.FileInfo

import scala.concurrent.ExecutionContext

@Singleton
class S3Controller @Inject()(cc: ControllerComponents, awsClient: AwsClient)
                            (implicit ec: ExecutionContext) extends AbstractController(cc){

  def upload: Action[MultipartFormData[MultipartUploadResult]] =
    Action(storeInS3UsingBucket) {
      request =>
        val maybeUploadResult =
          request.body.file("images").map {
            case FilePart(key, filename, contentType, multipartUploadResult) =>
              multipartUploadResult
          }
        //Fold will apply first function when response empty and second when success
        maybeUploadResult.fold(
          InternalServerError("Something went wrong, while uploading file!")
        )(uploadResult =>
          Ok(s"File ${uploadResult.key} upload to bucket ${uploadResult.bucket}")
      )
    }


  private def handleFilePartAwsUploadResult(bucketName: String): Multipart.FilePartHandler[MultipartUploadResult] = {
    case FileInfo(partName, filename, contentType) =>
      val accumulator = Accumulator(awsClient.s3Sink(bucketName, filename))
      accumulator map {
        multipartUploadResult =>
          FilePart(partName, filename, contentType, multipartUploadResult)
      }
  }

  val storeInS3UsingBucket: BodyParser[MultipartFormData[MultipartUploadResult]] = parse.using{ request =>
    request.queryString.get("bucketName").flatMap{ bucketName =>
      bucketName.headOption.map{ b =>
        parse.multipartFormData(handleFilePartAwsUploadResult(b))
      }
    }.getOrElse{
      sys.error("Bucket name is not provided!")
    }
  }

}
