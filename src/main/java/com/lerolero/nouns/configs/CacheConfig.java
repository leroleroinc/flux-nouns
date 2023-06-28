package com.lerolero.nouns.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cache.CacheManager;
//import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.redisson.config.Config;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.Redisson;

@Configuration
public class CacheConfig {

	@Bean
	@Primary
	public RedissonReactiveClient redissonClient(@Value("${spring.data.redis.url}") String url) {
		Config config = new Config();
		config.useSingleServer().setAddress(url);
		return Redisson.create(config).reactive();
	}

}
