package org.aminesidki.resiaiac.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

/**
 * In-memory cache setup used ONLY in tests, so caching behavior (@Cacheable / @CacheEvict) can be
 * verified without a real Redis connection. Uses ConcurrentMapCacheManager: same Spring cache
 * abstraction / AOP proxy behavior as the real RedisCacheManager, just backed by a plain HashMap
 * instead of Redis — fast, no Docker needed in CI.
 */
@TestConfiguration
@EnableCaching
public class TestCacheConfiguration {

  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager();
  }
}
