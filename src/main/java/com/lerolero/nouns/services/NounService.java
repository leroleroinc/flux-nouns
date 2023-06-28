package com.lerolero.nouns.services;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import com.lerolero.nouns.repositories.MongoNounRepository;
import com.lerolero.nouns.repositories.NounCache;
import com.lerolero.nouns.models.Noun;

@Service
public class NounService {

	@Autowired
	private MongoNounRepository repo;

	@Autowired
	private NounCache cache;

	private Mono<String> next() {
		return cache.next()
			.flatMap(n -> {
				if (n.getPlural() == null) return repo.findById(n.getId());
				else return Mono.just(n);
			})
			.cast(Noun.class)
			.doOnNext(n -> cache.add(n))
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
