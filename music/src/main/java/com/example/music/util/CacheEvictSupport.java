package com.example.music.util;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Component;

@Component
public class CacheEvictSupport {

	private final CacheManager cacheManager;

	public CacheEvictSupport(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	/** 특정 캐시에서 key prefix로 일괄 삭제 */
	public void evictByPrefix(String cacheName, String prefix) {
		Cache cache = cacheManager.getCache(cacheName);
		if (!(cache instanceof CaffeineCache)) {
			return;
		}
		CaffeineCache caffeineCache = (CaffeineCache) cache;

		caffeineCache.getNativeCache().asMap().keySet().removeIf(k -> String.valueOf(k).startsWith(prefix));
	}

	/** 정확한 키 하나 삭제 */
	public void evictOne(String cacheName, Object key) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			cache.evictIfPresent(key);
		}
	}

	/** 캐시 네임스페이스 전체 삭제 */
	public void clearAll(String cacheName) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			cache.clear();
		}
	}
}
