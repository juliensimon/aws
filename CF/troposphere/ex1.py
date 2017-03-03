
from troposphere import *
import troposphere.ec2 as ec2

template = Template()

keyname_param = template.add_parameter(Parameter(
    "KeyName",
    Description="Name of an existing EC2 KeyPair to enable SSH "
                "access to the instance",
    Type="String",
    Default="admin"
))

securitygroup_param = template.add_parameter(Parameter(
    "SecurityGroup",
    Description="Name of an existing Security Group",
    Type="String",
    Default="default"
))

template.add_mapping('RegionMap', {
    "us-east-1":      {"AMI": "ami-0b33d91d"},
    "us-west-1":      {"AMI": "ami-165a0876"},
    "us-west-2":      {"AMI": "ami-f173cc91"},
    "eu-west-1":      {"AMI": "ami-70edb016"},
    "sa-east-1":      {"AMI": "ami-80086dec"},
    "ap-southeast-1": {"AMI": "ami-dc9339bf"},
    "ap-southeast-2": {"AMI": "ami-1c47407f"},
    "ap-northeast-1": {"AMI": "ami-56d4ad31"}
})

ec2_instance = template.add_resource(ec2.Instance(
    "Ec2Instance",
    ImageId=FindInMap("RegionMap", Ref("AWS::Region"), "AMI"),
    InstanceType="t2.micro",
    KeyName=Ref(keyname_param),
    SecurityGroups=[Ref(securitygroup_param)],
    Tags=Tags(Name="Tropo1")
))

template.add_output([
    Output(
        "PublicIP",
        Description="Public IP address of the newly created EC2 instance",
        Value=GetAtt(ec2_instance, "PublicIp"),
    ),
    Output(
        "PublicDNS",
        Description="Public DNSName of the newly created EC2 instance",
        Value=GetAtt(ec2_instance, "PublicDnsName"),
    )
])

print(template.to_json())
