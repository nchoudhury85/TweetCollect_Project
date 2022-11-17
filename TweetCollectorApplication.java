package com.tweet.tweetCollector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

@SpringBootApplication
public class TweetCollectorApplication {

	@Value("${spring.social.twitter.appId}")
	private String consumerKey;
	@Value("${spring.social.twitter.appSecret}")
	private String consumerSecret;
	
	@Bean TwitterTemplate getTwtTemplate(){ return new
	TwitterTemplate(consumerKey, consumerSecret); }
	
	public static void main(String[] args) {
		SpringApplication.run(TweetCollectorApplication.class, args);
	}

}
