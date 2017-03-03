provider "aws" {
}

resource "aws_instance" "myFirstTerraformInstance" {
  ami           	= "ami-c51e3eb6"	# Amazon Linux 2016.09
  instance_type 	= "t2.micro"
  key_name		= "admin"
  security_groups 	= [ "PingSG" ]		# ICMP and SSH allowed
  tags {
	Name = "Terraform1"
    }
}
