package com.lerolero.nouns.services;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import com.lerolero.nouns.repositories.MongoNounRepository;
import com.lerolero.nouns.models.Noun;

@Service
public class NounService {

	@Autowired
	private MongoNounRepository repo;

	private Mono<String> next() {
		return repo.pullRandom()
			.map(n -> n.getPlural());
	}

	public Mono<String> randomNoun() {
		return next()
			.subscribeOn(Schedulers.boundedElastic());
	}

	public Flux<String> randomNounList(Integer size) {
		return Flux.range(1, size)
			.flatMap(i -> next())
			.subscribeOn(Schedulers.boundedElastic());
	}

	public Flux<String> randomNounProducer(Integer interval) {
		return Flux.interval(Duration.ofMillis(interval))
			.flatMap(i -> next())
			.subscribeOn(Schedulers.boundedElastic());
	}

}
