package com.example.music.config;

import org.springframework.beans.factory.FactoryBean;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;

public class YouTubeFactoryBean implements FactoryBean<YouTube> {

	@Override
	public YouTube getObject() throws Exception {
		return new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
				request -> {
				}).setApplicationName("music").build();
	}

	@Override
	public Class<?> getObjectType() {
		return YouTube.class;
	}
}
