// Author: Julien Simon <julien@julien.org>

package org.julien.datastuff;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.machinelearning.AmazonMachineLearningClient;
import com.amazonaws.services.machinelearning.model.DescribeMLModelsResult;
import com.amazonaws.services.machinelearning.model.MLModel;
import com.amazonaws.services.machinelearning.model.PredictRequest;
import com.amazonaws.services.machinelearning.model.PredictResult;
import com.amazonaws.services.machinelearning.model.RealtimeEndpointInfo;

public class MLSample {

	public static void main(String[] args) {

		if (args.length != 6) {
			System.out.println("Usage : predict file|role MODEL_ID AGE GENDER STATE DAY");
			return;
		}

		// Create Amazon ML client, using either file-based credentials ("file")
		// or role-based credentials ("role")
		AmazonMachineLearningClient client = null;
		switch (args[0]) {
		case "file":
			try {
				AWSCredentials credentials = new ProfileCredentialsProvider("default").getCredentials();
				client = new AmazonMachineLearningClient(credentials);
			} catch (Exception e) {
				throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
						+ "Please make sure that your credentials file is at the correct "
						+ "location, and is in valid format.", e);
			}
			break;
		case "role":
			InstanceProfileCredentialsProvider credentials = new InstanceProfileCredentialsProvider();
			client = new AmazonMachineLearningClient(credentials);
			break;
		default:
			System.out.println("Usage : predict file|role MODEL_ID AGE GENDER STATE DAY");
			return;
		}
		client.setRegion(Region.getRegion(Regions.EU_WEST_1));

		// Get list of prediction models
		DescribeMLModelsResult models = client.describeMLModels();
		MLModel model = null;

		// Show basic information about each model
		for (MLModel m : models.getResults()) {
			if (m.getMLModelId().equals(args[1])) {
				model = m;
				System.out.println("Name: " + m.getName());
				System.out.println("Id: " + m.getMLModelId());
				System.out.println("Type: " + m.getMLModelType());
				System.out.println("Algorithm: " + m.getAlgorithm());
				System.out.println("Input data: " + m.getInputDataLocationS3());
				System.out.println("Status: " + m.getStatus());

				RealtimeEndpointInfo endpoint = m.getEndpointInfo();
				System.out.println("URL: " + endpoint.getEndpointUrl());
				System.out.println("Status: " + endpoint.getEndpointStatus());
				System.out.println("RPS: " + endpoint.getPeakRequestsPerSecond());
			}
		}

		System.out.println();

		// Build a prediction request
		PredictRequest request = new PredictRequest();
		// Select prediction model
		request.setMLModelId(model.getMLModelId());
		// Select realtime endpoint
		request.setPredictEndpoint(model.getEndpointInfo().getEndpointUrl());

		// Build data to be predicted
		request.addRecordEntry("age", args[2]).addRecordEntry("gender", args[3]).addRecordEntry("state", args[4])
				.addRecordEntry("day", args[5]);

		// Send prediction request
		PredictResult result;
		try {
			long start = System.currentTimeMillis();
			result = client.predict(request);
			long end = System.currentTimeMillis();
			System.out.println("Request time: " + (end - start) + " ms");
		} catch (Exception e) {
			throw new AmazonClientException("Prediction failed", e);
		}

		// Display predicted value
		System.out.println("Predicted value:" + result.getPrediction().getPredictedValue());
	}
}
