package com.faiz.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.deeplearning4j.clustering.cluster.Point;

/**
 * <br> - Bolt for calculating the TF-IDF (Frequency) and word count of each word
 * @author Syahirul Faiz
 * @version 2020.06
 * @since 2020-06-08
 */
public class TFIDFBolt extends BaseRichBolt {

	private static final long serialVersionUID = 14171564556419607L;

	private OutputCollector collector;

	//TFIDF
	List<String> tempDoc = new CopyOnWriteArrayList<String>();
	List<List<String>> Docs = new CopyOnWriteArrayList<List<String>>();

	//WORDCOUNT
	private Map<String, Long> counter = new HashMap<String, Long>();

	/**
	 * <br> - collect tuples from PreprocessingBolt
	 */
	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
		this.collector = collector;
	}

	/**
	 * <br> - execute after prepare method.
	 * <br> - Calculate the TF-IDF and wordcount. 
	 */
	@Override
	public void execute(Tuple input) {
		//start timing in milliseconds
		long start = System.currentTimeMillis();

		//extract topic, word, and id_status field from previous tuple
		String topic = (String) input.getValueByField("topic");
		String word = (String) input.getValueByField("word");
		String id_status = (String) input.getValueByField("id_status");

		//TFIDF
		tempDoc = Arrays.asList(topic.split(";"));
		Docs.add(tempDoc);
		TFIDFCompute calculator = new TFIDFCompute();
		double tfidf = calculator.tfIdf(tempDoc, Docs, word);
		//System.err.println("INFO TF-IDF ("+word+") = " + tfidf);

		//WORDCOUNT
		Long count = counter.get(word);
		count = count == null ? 1L : count + 1;
		counter.put(word, count);


		//to get the class name of the bolts : from_class_name
		String from_class_name = String.valueOf(this.getClass().getSimpleName()).substring(0, Math.min(String.valueOf(this.getClass().getSimpleName()).length(), 25)); 

		String hostname = "";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//to get the host name of the machine : from_machine_name
		String from_machine_name=String.valueOf(hostname).substring(0, Math.min(String.valueOf(hostname).length(), 15));
	
		//end timing
		long end = System.currentTimeMillis();
		double elapsedTime = (double) (end - start)/1000;

		writeToTable(id_status,word,String.valueOf(count),String.valueOf(tfidf),String.valueOf(elapsedTime), from_class_name, from_machine_name);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

	/**
	 * <br> - a method for record to the database. 
	 * @param id_status id_status from the twitter
	 * @param word important word from a tuple
	 * @param count number of keyword
	 * @param tfidf TF-IDF score of a keyword
	 * @param elapsedTime time spent for TF-IDF Bolt
	 * @param from_class_name class name of TF-IDF Bolt
	 * @param from_machine_name the server name for clustering
	 */
	public void writeToTable(String id_status, String word, String count, String tfidf, String elapsedTime, String from_class_name, String from_machine_name) {
		//define the index of the columns in Mysql
		String[] toBeWritten= {"0","1","2","3","4","5","6"}; 

		toBeWritten[0]= String.valueOf(id_status.substring(0, Math.min(String.valueOf(id_status).length(), 30)));
		toBeWritten[1]= String.valueOf(word.substring(0, Math.min(String.valueOf(word).length(), 20)));
		toBeWritten[2]= String.valueOf(count.substring(0, Math.min(String.valueOf(count).length(), 5)));
		toBeWritten[3]= String.valueOf(tfidf.substring(0, Math.min(String.valueOf(tfidf).length(), 10)));
		toBeWritten[4]= String.valueOf(elapsedTime.substring(0, Math.min(String.valueOf(elapsedTime).length(), 5)));
		toBeWritten[5]= from_class_name;
		toBeWritten[6]= from_machine_name;

		//TODO !!! Check for insert result		
		String query = " INSERT INTO keyword_count (id_status, keyword, word_count, tf_idf, elapsed_time, from_class_name, from_machine_name) " + 
				"VALUES (?, ?, ?, ?, ?, ?, ?) " + 
				"ON DUPLICATE KEY UPDATE " + 
				"  id_status='"+toBeWritten[0]+"', "+
				"  word_count='"+toBeWritten[2]+"', "+
				"  tf_idf='"+toBeWritten[3]+"', "+
				"  elapsed_time='"+toBeWritten[4]+"', "+
				"  from_class_name='"+toBeWritten[5]+"', "+
				"  from_machine_name='"+toBeWritten[6]+"' "
				;


		Helper helper = new Helper();
		helper.writeToTable(toBeWritten, query);

	}
}
