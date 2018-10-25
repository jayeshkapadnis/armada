package client

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.services.ecs.{AmazonECSAsync, AmazonECSAsyncClient}

object ESCClient{
  def apply(credentials: AWSCredentialsProvider, region: String): ESCClient = {
    val client = AmazonECSAsyncClient.asyncBuilder()
      .withCredentials(credentials)
      .withRegion(region).build()
    new ESCClient(client)
  }
}

class ESCClient(underlying: AmazonECSAsync) {
  def link{

  }
}
