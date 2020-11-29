package com.faiz.storm;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <br> - Pull the tweet feed using the twitter4j library and maintain the tweet stream feeding.
 * <br> - In our Spout, we use API keys from the main class and query the twitter by using the keywords defined.
 * <br> - we filter only English language tweets.
 * @author Syahirul Faiz
 * @version 2020.06
 * @since 2020-06-08
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class Spout extends BaseRichSpout {

	private SpoutOutputCollector collector;
	private LinkedBlockingQueue<Status> queue;
	private TwitterStream twitterStream;

	/**
	 *<br> - opening the spout for producing the tuple
	 */
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		queue = new LinkedBlockingQueue<Status>(1);
		
		this.collector = collector;
		
		//create the listener object
		StatusListener listener = new StatusListener() {
			@Override
			public void onStatus(Status status) {
				queue.offer(status);
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice sdn) {
			}

			@Override
			public void onTrackLimitationNotice(int i) {
			}

			@Override
			public void onScrubGeo(long l, long l1) {
			}

			@Override
			public void onStallWarning(StallWarning stallWarning) {
			}

			@Override
			public void onException(Exception e) {
			}
		};

		
		String consumerKey = "PUT YOUR consumerKey HERE";
		String consumerSecret = "PUT YOUR consumerSecret HERE";
		String accessToken = "PUT YOUR accessToken HERE";
		String accessTokenSecret = "PUT YOUR accessTokenSecret HERE";
				
		//TODO Choose Tracking Keyword
		String[] keyWords = {"#coronavirusuk","death","covid","corona"};
		
		//using configuration builder class for manage the authentication setting
				ConfigurationBuilder cb = new ConfigurationBuilder();
				cb.setDebugEnabled(false).setOAuthConsumerKey(consumerKey)
						.setOAuthConsumerSecret(consumerSecret)
						.setOAuthAccessToken(accessToken)
						.setOAuthAccessTokenSecret(accessTokenSecret);
				
		TwitterStreamFactory factory = new TwitterStreamFactory(cb.build());
		twitterStream = factory.getInstance();
		twitterStream.addListener(listener);
		
		if (keyWords.length == 0) {
			twitterStream.sample();
		} else {
			//filter the tweets using the keywords provided
			FilterQuery query = new FilterQuery().track(keyWords);
					
			double lat = 53.186288;
			double longitude = -8.043709;
			double lat1 = lat - 4;
			double longitude1 = longitude - 8;
			double lat2 = lat + 4;
			double longitude2 = longitude + 8;
			
			//track keyword, primarily in UK, but can also track tweets from around the world
			query.locations(new double[][]{new double[]{longitude1, lat1},new double[]{longitude2, lat2}}).language(new String[]{"en"}).track(keyWords);  
			
			twitterStream.filter(query);
		}
		System.out.println("INFO from "+this.getClass().getSimpleName());	
		
	}

	/**
	 *<br> - pass the tuple to next bolt
	 */
	@Override
	public void nextTuple() {
		Status collected_tweets = queue.poll();
		if (collected_tweets == null) {
			Utils.sleep(1000);
		} else {
			collector.emit(new Values(collected_tweets));
		}
	}

	/**
	 *<br> - shutdown the spout according to the given interval in topology
	 */
	@Override
	public void close() {
		twitterStream.shutdown();
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		Config ret = new Config();
		//More than '1', will be blocked		
		ret.setMaxTaskParallelism(1);
		return ret;
	}

	@Override
	public void ack(Object id) {
	}

	@Override
	public void fail(Object id) {
	}

	/**
	 *<br> - declare the field grouping sent to next PreprocessingBolt
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		//Send the tuple which contains a fielf which called 'collected_tweets'
		declarer.declare(new Fields("collected_tweets"));
	}

}
