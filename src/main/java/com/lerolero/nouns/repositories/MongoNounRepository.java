package com.lerolero.nouns.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;

import reactor.core.publisher.Mono;

import com.lerolero.nouns.models.Noun;

public interface MongoNounRepository extends ReactiveMongoRepository<Noun,String> {

	@Aggregation("{ $sample: { size: 1 } }")
	public Mono<Noun> pullRandom();

}
