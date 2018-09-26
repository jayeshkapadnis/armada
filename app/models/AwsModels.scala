package models

import com.amazonaws.services.ec2.model.{AuthorizeSecurityGroupIngressRequest, GroupIdentifier, IamInstanceProfileSpecification, IpPermission, IpRange, LaunchSpecification, RequestSpotFleetRequest, RequestSpotInstancesRequest, SpotFleetLaunchSpecification, SpotFleetRequestConfigData}

object IpPermission{
  def apply(ipRanges: List[String]): IpPermission = {
    new IpPermission()
      .withIpProtocol("tcp")
      .withFromPort(new Integer(22)).withToPort(new Integer(22))
      .withIpv4Ranges(ipRanges.map(new IpRange().withCidrIp): _*)
  }
}

object SecurityGroupIngressRequest{
  def apply(ipV4Address: String, securityGroupId: String): AuthorizeSecurityGroupIngressRequest = {
    new AuthorizeSecurityGroupIngressRequest()
      .withIpPermissions(IpPermission(List(ipV4Address)))
      .withGroupId(securityGroupId)
  }
}

case class SpotInstanceRequest(spotPrice: Double,
                               instanceCount: Int,
                               instanceType: String,
                               imageId: String,
                               securityGroupNames: List[String])


object SpotInstanceRequest{
  def apply(request: SpotInstanceRequest): RequestSpotInstancesRequest = {
    new RequestSpotInstancesRequest()
      .withSpotPrice(request.spotPrice.toString)
      .withInstanceCount(request.instanceCount)
      .withLaunchSpecification(
        new LaunchSpecification()
          .withImageId(request.imageId)
          .withSecurityGroups(request.securityGroupNames: _*)
          .withInstanceType(request.instanceType)
      )
  }
}


case class SpotFleetRequest(allocationStrategy: String,
                            instanceRequest: SpotInstanceRequest,
                            iamFleetRole: String,
                            iamInstanceProfileArn: String)


object SpotFleetRequest{
  def apply(request: SpotFleetRequest): RequestSpotFleetRequest = new RequestSpotFleetRequest()
    .withSpotFleetRequestConfig(
      new SpotFleetRequestConfigData()
        .withIamFleetRole(request.iamFleetRole)
        .withTargetCapacity(request.instanceRequest.instanceCount)
        .withSpotPrice(request.instanceRequest.spotPrice.toString)
        .withLaunchSpecifications(
          new SpotFleetLaunchSpecification()
            .withImageId(request.instanceRequest.imageId)
            .withSecurityGroups(request.instanceRequest.securityGroupNames.map(new GroupIdentifier().withGroupName): _*)
            .withInstanceType(request.instanceRequest.instanceType)
            .withIamInstanceProfile(
              new IamInstanceProfileSpecification()
                .withArn(request.iamInstanceProfileArn)
            )
        ))
}