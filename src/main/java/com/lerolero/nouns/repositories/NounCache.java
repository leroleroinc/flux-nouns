package com.lerolero.nouns.repositories;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RBucketReactive;

import reactor.core.publisher.Mono;

import com.lerolero.nouns.repositories.MongoNounRepository;
import com.lerolero.nouns.models.Noun;

@Repository
public class NounCache {

	@Autowired
	private RedissonReactiveClient redis;

	@Autowired
	private MongoNounRepository repo;

	private List<String> ids;

	public Mono<Noun> next() {
		if (ids == null || ids.size() == 0) {
			ids = new ArrayList<>();
			repo.findAll().map(Noun::getId).doOnNext(i -> ids.add(i)).subscribe();
		}
		Noun defaultNoun = new Noun();
		return Mono.fromSupplier(() -> ids.get((int)(Math.random() * ids.size())))
			.doOnNext(id -> defaultNoun.setId(id))
			.flatMap(id -> redis.getBucket("/noun/" + id).get())
			.cast(Noun.class)
			.defaultIfEmpty(defaultNoun);
	}

	public void add(Noun noun) {
		RBucketReactive<Noun> bucket = redis.getBucket("/noun/" + noun.getId());
		bucket.set(noun).subscribe();
		ids.add(noun.getId());
	}

}
