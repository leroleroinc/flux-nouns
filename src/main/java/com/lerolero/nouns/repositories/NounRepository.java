package com.lerolero.nouns.repositories;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.DefaultResourceLoader;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class NounRepository {

	private ResourceLoader loader = new DefaultResourceLoader();

	private String fileName = "nouns.dat";

	public Mono<String> pullRandom() {
		return Mono.fromSupplier(() -> loader.getResource(fileName))
			.handle((r, sink) -> {
				try {
					String s = r.getContentAsString(Charset.defaultCharset());
					sink.next(s);
				} catch (IOException e) {
					sink.error(new RuntimeException("Couldn't stringify resource."));
				}
			})
			.cast(String.class)
			.map(s -> s.split("\n"))
			.map(arr -> arr[(int)(Math.random() * arr.length)])
			.subscribeOn(Schedulers.boundedElastic());
	}
	
}

