package com.tweet.tweetCollector.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tweet.tweetCollector.data.HashLabel;
import com.tweet.tweetCollector.data.HashTagRepository;

@RestController
@RequestMapping(value=TweetController.TWITTER_URL)
public class TweetController {

	public final static String TWITTER_URL = "twitter";
	
	//Bearer token : AAAAAAAAAAAAAAAAAAAAAD19jQEAAAAAjKxiDXlXtwSqPyPT7mPfJV9OVJc%3DHD0DGM0m0Bo6IbQ1M1pvx0iZtZ5rCBBPf2Ogbk5srN0q1C4072
	@Autowired
    private TwitterTemplate twitter;
	
	@Autowired
	private HashTagRepository hashRepos;
	
	@RequestMapping(value="/hashtags/{hashtag}", produces = MediaType.APPLICATION_JSON_VALUE, method=RequestMethod.GET)
	public List<Tweet> getTweets(@PathVariable final String hashtag) {
		return twitter.searchOperations().search(hashtag, 20).getTweets();
	}
	
	@PostMapping(value="/hashtags")
	public HashLabel saveTweets(@RequestBody final HashLabel hashLabel) {
		return hashRepos.save(hashLabel);
	}
	
	@RequestMapping(value="/labels/{hashLabel}", method=RequestMethod.GET)
	public List<String> fetchTweetsByLabel(@PathVariable final  String hashLabel) {
		//List<HashLabel> hashLs= StreamSupport.stream(hashRepos.findAll().spliterator(), false).filter(a -> a.getLabel().equals(hashLabel)).collect(Collectors.toList());
		List<HashLabel> hashLs = hashRepos.findByLabel(hashLabel);
		List<String> tts = new ArrayList<>();
		hashLs.forEach(hash -> tts.addAll(hash.getTweets()));
		return tts;
	}
	
	@PostMapping(value="/hashtags/bulk")
	public List<HashLabel> saveMultipleTweets(@RequestBody final String filePath) {
		List<HashLabel> hashLabels = new ArrayList<>();
		Path path = Paths.get(filePath);

	    try {
			BufferedReader reader = Files.newBufferedReader(path);
			while (reader.ready()) {
				String line = reader.readLine();
				if(line.contains(":")) {
					String[] lineParts = line.split(":");
					List<Tweet> tweets = twitter.searchOperations().search(lineParts[0], 20).getTweets();
					List<String> tweetTxts = tweets.stream().map(t -> t.getText()).collect(Collectors.toList());
					HashLabel hLabel = new HashLabel();
					hLabel.setHashTag(lineParts[0]);
					hLabel.setLabel(lineParts[1]);
					hLabel.setTweets(tweetTxts);
					hashLabels.add(hashRepos.save(hLabel));
				}
			}
		} catch (IOException e) {
			System.out.println("Failed to read file!!!");
		}
		return hashLabels;
	}
	
	@GetMapping(value="/test")
	public String getMsg() {
		System.out.println("Reached test method.... !!!!!!!!!!!!!!!!!!!");
		return "Hello Stranger!!!";
	}
}
