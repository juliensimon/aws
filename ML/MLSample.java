package org.julien.datastuff;

import com.amazonaws.AmazonClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.machinelearning.AmazonMachineLearning;
import com.amazonaws.services.machinelearning.AmazonMachineLearningClientBuilder;
import com.amazonaws.services.machinelearning.model.DescribeMLModelsResult;
import com.amazonaws.services.machinelearning.model.MLModel;
import com.amazonaws.services.machinelearning.model.PredictRequest;
import com.amazonaws.services.machinelearning.model.PredictResult;
import com.amazonaws.services.machinelearning.model.RealtimeEndpointInfo;

public class MLSample {

	private static void usage() {
		System.out.println("Usage : predict MODEL_ID");
	}
	
	public static void main(String[] args) {

		if (args.length != 1) {
			usage();
			return;
		}

		AmazonMachineLearning client = AmazonMachineLearningClientBuilder.standard()
                .withRegion(Regions.EU_WEST_1)
                .build();
		
		// Get list of prediction models
		DescribeMLModelsResult models = client.describeMLModels();
		MLModel model = null;

		// Look for our model and show basic information
		for (MLModel m : models.getResults()) {
			System.out.println(m.getMLModelId());
			if (m.getMLModelId().equals(args[0])) {
				model = m;
				
				// Model information
				System.out.println("Name: " 		+ m.getName());
				System.out.println("Id: " 			+ m.getMLModelId());
				System.out.println("Type: " 		+ m.getMLModelType());
				System.out.println("Algorithm: " 	+ m.getAlgorithm());
				System.out.println("Input data: " 	+ m.getInputDataLocationS3());
				System.out.println("Status: " 		+ m.getStatus());
				// Endpoint information
				RealtimeEndpointInfo endpoint = m.getEndpointInfo();
				System.out.println("URL: " 		+ endpoint.getEndpointUrl());
				System.out.println("Status: " 	+ endpoint.getEndpointStatus());
				System.out.println("RPS: " 		+ endpoint.getPeakRequestsPerSecond());
				break;
			}
		}
		
		if (model == null) {
			System.out.println("Model has not been found, exiting.");
			System.exit(-1);
		}
		else {
			
		}

		System.out.println();

		// Build a prediction request
		PredictRequest request = new PredictRequest();
		// Select prediction model
		request.setMLModelId(model.getMLModelId());
		// Select realtime endpoint
		request.setPredictEndpoint(model.getEndpointInfo().getEndpointUrl());

		// Build data to be predicted
		request.addRecordEntry("age", "32").addRecordEntry("job", "services").addRecordEntry("marital", "divorced")
		.addRecordEntry("education","basic.9y").addRecordEntry("default", "no").addRecordEntry("housing", "unknown")
		.addRecordEntry("loan", "yes").addRecordEntry("contact", "cellular").addRecordEntry("month", "dec")
		.addRecordEntry("day_of_week", "mon").addRecordEntry("duration", "110").addRecordEntry("campaign","1")
		.addRecordEntry("pdays", "11").addRecordEntry("previous", "0").addRecordEntry("poutcome", "nonexistent")
		.addRecordEntry("emp_var_rate", "-1.8").addRecordEntry("cons_price_idx", "94.465").addRecordEntry("cons_conf_idx", "-36.1")
		.addRecordEntry("euribor3m", "0.883").addRecordEntry("nr_employed", "5228.1");
					
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
		System.out.println("Prediction: " + result.getPrediction());
	}
}
