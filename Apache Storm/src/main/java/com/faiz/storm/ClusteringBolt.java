package com.faiz.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <br> - Bolt filters out a predefined set of words.
 * @author Syahirul Faiz
 * @version 2020.08
 * @since 2020-08-01
 */
public class ClusteringBolt extends BaseRichBolt {
	int sum=0;
	
	//create global collection of string
	Collection<String> sentences = new CopyOnWriteArrayList<String>();
	
	private static final long serialVersionUID = 14171564556419606L;

	private OutputCollector collector;

	//set the default period for word embedding and clustering
	private long logIntervalSec = 60;
	private long lastLogTime =System.currentTimeMillis();
	
	
	/**
	 * <br> - Constructor for ClusteringBolt.
	 * @param logIntervalSec interval time for clustering
	 */
	public ClusteringBolt(long logIntervalSec) {
		this.logIntervalSec = logIntervalSec;
	}
	
	/**
	 * <br> - collect tuples from PreprocessingBolt
	 */
	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
		this.collector = collector;
		lastLogTime =System.currentTimeMillis();
	}

	/**
	 * <br> - Perform Word Embedding and Clustering 
	 */
	@Override
	public void execute(Tuple input) {

		//extract word and id_status field from previous tuple
		String word = (String) input.getValueByField("word");
		String id_status = (String) input.getValueByField("id_status");
		
		if(word.contains(";")) {
    		sentences.addAll(Arrays.asList(word.split(";")));
    	}else {
    		sentences.add(word);
    	}

		
		long now = System.currentTimeMillis();
		long logPeriodSec = (now - lastLogTime) / 1000;
		//create the word2vec instance and perform word embedding
		if (logPeriodSec > logIntervalSec) {
			Word2VecRawKMeans w2v = new Word2VecRawKMeans();
			w2v.test(word,sentences);
			lastLogTime = now;
		}
		
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		//no tuple to send in the last Bolt
	}
}
