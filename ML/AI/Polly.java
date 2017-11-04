package com.amazonaws.samples;

import java.io.InputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class Polly {

	private static void play(InputStream stream) {
		try {
			AdvancedPlayer player = new AdvancedPlayer(stream,
					javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());
			player.play();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		AWSCredentials credentials;
		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (/Users/userid/.aws/credentials), and is in valid format.", e);
		}

		AmazonPolly pollyClient = AmazonPollyClientBuilder.standard().withRegion(Regions.EU_WEST_1)
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();

		SynthesizeSpeechRequest req = new SynthesizeSpeechRequest().withVoiceId("Brian")
				.withText("Hello, My name is Brian. I'm in the kitchen.").withOutputFormat("mp3");

		SynthesizeSpeechResult result = pollyClient.synthesizeSpeech(req);
		play(result.getAudioStream());

		String ssmlMessage = "<speak>Your reservation for <say-as interpret-as=\"cardinal\"> 2 </say-as> rooms on the "
				+ "<say-as interpret-as=\"ordinal\">4th</say-as> floor of the hotel on"
				+ "<say-as interpret-as=\"date\" format=\"mdy\">3/21/2012</say-as>, with early"
				+ "arrival at <say-as interpret-as=\"time\" format=\"hms12\">12:35pm</say-as> has been confirmed. "
				+ "Please call <say-as interpret-as=\"telephone\" format=\"1\">(888) 555-1212</say-as> with any questions.</speak>";

		req = new SynthesizeSpeechRequest().withVoiceId("Amy").withTextType("ssml").withText(ssmlMessage)
				.withOutputFormat("mp3");

		result = pollyClient.synthesizeSpeech(req);
		play(result.getAudioStream());
	}
}
