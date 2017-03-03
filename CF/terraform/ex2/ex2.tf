provider "aws" {
}

resource "aws_instance" "mySecondTerraformInstance" {
  ami           	= "ami-c51e3eb6"	# Amazon Linux 2016.09
  instance_type 	= "t2.small"
  key_name		= "admin"
  security_groups 	= [ "PingSG" ]		# ICMP and SSH allowed
  tags {
	Name = "Terraform2"
    }
}

resource "aws_eip" "myElasticIp" {
    instance = "${aws_instance.mySecondTerraformInstance.id}"
}
