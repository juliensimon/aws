package com.amazonaws.samples;

import java.util.List;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AgeRange;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.Attribute;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
public class RekoDetectFaces {

	   public static void main(String[] args) throws Exception {

	      String photo = "oktoberfest.jpg";
	      String bucket = "jsimon-public";

	      AWSCredentials credentials;
	      try {
	         credentials = new ProfileCredentialsProvider("default").getCredentials();
	      } catch (Exception e) {
	         throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
	            + "Please make sure that your credentials file is at the correct "
	            + "location (/Users/userid.aws/credentials), and is in a valid format.", e);
	      }

	      AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder
	         .standard()
	         .withRegion(Regions.EU_WEST_1)
	         .withCredentials(new AWSStaticCredentialsProvider(credentials))
	         .build();

	      DetectFacesRequest request = new DetectFacesRequest()
	         .withImage(new Image()
	            .withS3Object(new S3Object()
	               .withName(photo)
	               .withBucket(bucket)))
	         .withAttributes(Attribute.ALL);

	      try {
	         DetectFacesResult result = rekognitionClient.detectFaces(request);
	         List < FaceDetail > faceDetails = result.getFaceDetails();
	         
	         System.out.println(faceDetails.size() + " faces have been detected.\n");

	         for (FaceDetail face: faceDetails) {
	            if (request.getAttributes().contains("ALL")) {
	               AgeRange ageRange = face.getAgeRange();
	               System.out.println("The detected face is estimated to be between "
	                  + ageRange.getLow().toString() + " and " + ageRange.getHigh().toString()
	                  + " years old.");
	               System.out.println("Here's the complete set of attributes:");
	            } else { // non-default attributes have null values.
	               System.out.println("Here's the default set of attributes:");
	            }

	            ObjectMapper objectMapper = new ObjectMapper();
	            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(face));
	         }

	      } catch (AmazonRekognitionException e) {
	         e.printStackTrace();
	      }

	   }

	}
