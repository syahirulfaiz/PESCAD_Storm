package com.faiz.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import twitter4j.UserMentionEntity;
import twitter4j.HashtagEntity;
import twitter4j.Status;



/**
 * <br> - Preprocess the tuples from Spouts
 * <br> - Extract the hashtag and user-mentions and keywords
 * <br> - We breakdown the tweets into smaller objects, such as screen name (username), location, tweets, and other entities. 
 * @author Syahirul Faiz
 * @version 2020.06
 * @since 2020-06-08
 */
@SuppressWarnings({ "rawtypes"})
public class PreprocessingBolt extends BaseRichBolt {

	private static final long serialVersionUID = 6369939573153711515L;

	private final int minWordLength;
	/**
	 * <br> - Pre-defined non-abstract data type in Storm (to collect tuple)
	 */
	private OutputCollector collector;

	public PreprocessingBolt(int minWordLength) {
		this.minWordLength = minWordLength;
	}

	/**
	 * <br> - collect tuples from Spout
	 */
	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
		this.collector = collector;
	}

	/**
	 * <br> - execute after prepare method.
	 * <br> - we breakdown the tweets into smaller objects, such as screen name (username), location,  tweets, and other entities. 
	 */
	@Override
	public void execute(Tuple input) {

		//start timing in milliseconds
		long start = System.currentTimeMillis();


		//instantiate the Helper object
		Helper helper = new Helper();

		//patterns
		String pattern = "health|economy|education|finance|technology|"+
				"nhs|who|corona|covid|outbreak|vaccine|sanitizer|mask|hospital|doctor|medic|swab|pcr|rapid|virus|infection|symptom|pandemi"+
				"safe|recover|quarantine|death|lockdown|distancing|football|"+
				"wuhan|usa|china|boris|uk|trump";

		//extract the field grouping from Spout
		Status tweet = (Status) input.getValueByField("collected_tweets");

		//the keywords from a tweet
		String topic="";

		//extract the hashtag entity
		for (HashtagEntity hashtage : tweet.getHashtagEntities()) {	
			String match = helper.patternParser(hashtage.getText().toLowerCase(), pattern);
			if (match!=null && !match.isEmpty()){
				topic = topic + match + ";";
			}
		}

		//extract the user mention entity
		for (UserMentionEntity userMention : tweet.getUserMentionEntities()) {	
			String match = helper.patternParser(userMention.getText().toLowerCase(), pattern);
			if (match!=null && !match.isEmpty()){
				topic = topic + match + ";";
			}
		}

		//remove whitespaces, punctuation, and new lines
		String statusText = tweet.getText().replaceAll("[; , \\n \\r \" \']"," ");

		//extract keywords from the status text
		topic = topic + helper.getKeywordFromStatus(statusText);

		//for marking the timestamp
		Timestamp startDate = new Timestamp(System.currentTimeMillis());

		//define the index of the columns in Mysql
		String[] toBeWritten= {"0","1","2","3","4","5","6","7","8","9","10","11"};
		String query = " insert into collected_tweets (id_status, screen_name, status, topic, place, latitude, longitude, created_at, is_event, elapsed_time, from_class_name, from_machine_name)"
				+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		//get id_status
		toBeWritten[0]= String.valueOf(tweet.getId());

		//to get the screen name of the tweet
		toBeWritten[1]= String.valueOf(tweet.getUser().getScreenName()).substring(0, Math.min(String.valueOf(tweet.getUser().getScreenName()).length(), 15));
		//the contents of the tweet
		toBeWritten[2]= statusText; 
		toBeWritten[3]= topic; //topic

		if (tweet.getPlace() !=null) {
			//to get place of the tweet
			toBeWritten[4]=  String.valueOf(tweet.getPlace().getFullName()).substring(0, Math.min(String.valueOf(tweet.getPlace().getFullName()).length(), 30));
		}else {
			//to get location of the user, if the place is null
			toBeWritten[4]=  String.valueOf(tweet.getUser().getLocation()).substring(0, Math.min(String.valueOf(tweet.getUser().getLocation()).length(), 30));
		}

		//latitude and longitude
		if (tweet.getGeoLocation()!=null) { 		
			toBeWritten[5]= String.valueOf(tweet.getGeoLocation().getLatitude()).substring(0, Math.min(String.valueOf(tweet.getGeoLocation().getLatitude()).length(), 10));//latitude
			toBeWritten[6]= String.valueOf(tweet.getGeoLocation().getLongitude()).substring(0, Math.min(String.valueOf(tweet.getGeoLocation().getLongitude()).length(), 10)); //longitude	 
		}else {
			toBeWritten[5]= "";
			toBeWritten[6]= "";
		}

		//created_at
		toBeWritten[7]= startDate.toString().substring(0, 19); 
		//is_event: marking a 'death' word 
		String cek =statusText.toLowerCase();
		toBeWritten[8]= (cek.contains("death") 
				&& (cek.contains("corona") || cek.contains("covid"))
				? "1" : "0");	


		//to get the class name of the bolts : from_class_name
		toBeWritten[10]= String.valueOf(this.getClass().getSimpleName()).substring(0, Math.min(String.valueOf(this.getClass().getSimpleName()).length(), 25)); 

		String hostname = "";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//to get the host name of the machine : from_machine_name
		toBeWritten[11]=String.valueOf(hostname).substring(0, Math.min(String.valueOf(hostname).length(), 15));

		//end timing
		long end = System.currentTimeMillis();
		double elapsedTime = (double) (end - start)/1000;
		toBeWritten[9] = String.valueOf(elapsedTime);


		//emit the pre-processed fields to next Bolt
		if (topic!="" && !topic.isBlank()) {
			String[] words = topic.split(";");
			for (String word : words) {
				if (word.length() >= minWordLength) {	
					//tuple emited word by word
					collector.emit(new Values(toBeWritten[0], toBeWritten[1], toBeWritten[2],word,
							toBeWritten[4], toBeWritten[5],toBeWritten[6],
							toBeWritten[7], toBeWritten[8],
							toBeWritten[10], toBeWritten[11],
							//TODO !!! topic is sent here
							toBeWritten[3],toBeWritten[2]));

				}
			}	
			//save to table
			helper.writeToTable(toBeWritten, query);
		}

	}

	/**
	 *<br> - declare the field grouping sent to next TFIDFBolt, ClusteringBolt, and PESCADBolt
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

		//send a tuple which consists of many elements
		declarer.declare(new Fields("id_status", "screen_name", "status", "word", 
				"place", "latitude", "longitude", 
				"created_at", "is_event", 
				"from_class_name", "from_machine_name",
				//TODO topic is sent here
				"topic", "lang"));

	}
}
