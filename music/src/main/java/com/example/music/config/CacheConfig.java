package com.example.music.config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	public Caffeine<Object, Object> caffeineSpec() {
		return Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES) // ★ 30분 캐싱
				.maximumSize(1000).recordStats();
	}

	@Bean
	public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
		CaffeineCacheManager mgr = new CaffeineCacheManager();
		mgr.setCaffeine(caffeine);
		mgr.setCacheNames(Arrays.asList("home:popularVideos", "home:latestVideos", "home:popularChannels"));
		return mgr;
	}
}