provider "aws" {
}

resource "aws_instance" "myFirstWebServer" {
  ami           	= "ami-c51e3eb6"	# Amazon Linux 2016.09
  instance_type 	= "t2.micro"
  key_name		= "admin"
  security_groups 	= [ "${aws_security_group.myFirstWebServerSG.name}" ]	
  tags {
    Name = "TerraformWebServer"
  }
  provisioner "local-exec" {
    command = "echo ${aws_instance.myFirstWebServer.public_ip} > ip_address.txt"
  }

  provisioner "remote-exec" {
    connection {
      type = "ssh"
      user = "ec2-user"
      private_key = "${file("/home/julien/.ssh/admin.pem")}"
      timeout = "2m"
      agent = false
    }
    inline = [
      "sudo yum update -y",
      "sudo yum install httpd -y",
      "sudo service httpd start",
      "sudo service httpd start"	# When once it not enough, do it twice. Hmmm.
    ]
  }
}

resource "aws_security_group" "myFirstWebServerSG" {
  name = "allow_http_ssh"
  description = "Allow SSH and HTTP from anywhere"
  ingress {
    from_port = 22 
    to_port = 22
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port = 80 
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port = 0 
    to_port = 0
    protocol = -1
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags {
    Name = "TerraformWebServerSG"
  }
}

