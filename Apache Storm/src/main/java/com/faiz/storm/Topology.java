package com.faiz.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

/**
 * <br> - Topology class is the main (driver) class of the Project
 * <br> - We design the Topology which consists of a Spout class and four other Bolt classes.
 * <br> - We use three objects of Spouts to collect a large number of tweets, 2 PreprocessingBolt for balancing the pre-processing; PESCADBolt, ClusteringBolt and CounterBolt for other main tasks (Task-Parallelism)
 * @author Syahirul Faiz
 * @version 2020.06
 * @since 2020-06-08
 */
public class Topology {

	public static void main(String[] args) throws Exception {
		Config config = new Config();
		config.setMessageTimeoutSecs(120);

		TopologyBuilder b = new TopologyBuilder();
		b.setSpout("Spout", new Spout(), 3);
		b.setBolt("PreprocessingBolt", new PreprocessingBolt(3), 2).shuffleGrouping("Spout");

		//fieldGrouping by id_status and is_event element from previous bolt
		b.setBolt("PESCADBolt", new PESCADBolt(60)).fieldsGrouping("PreprocessingBolt", new Fields("id_status","is_event"));
		
		//for graphical word representation and word embedding
		b.setBolt("ClusteringBolt", new ClusteringBolt(60)).fieldsGrouping("PreprocessingBolt", new Fields("id_status","word"));

		//for tf-idf calculation 
		b.setBolt("TFIDFBolt", new TFIDFBolt()).fieldsGrouping("PreprocessingBolt", new Fields("topic","word"));
		
///////============DISTRIBUTED VERSION==================		
		//submit the storm topology to cluster.
		config.setNumWorkers(20);
		config.setMaxSpoutPending(5000);
		StormSubmitter.submitTopology("Topology", config, b.createTopology());
		
////==========STANDALONE VERSION======================
		
		// final LocalCluster cluster = new LocalCluster();
		// cluster.submitTopology("Topology", config, b.createTopology());
		
		// Runtime.getRuntime().addShutdownHook(new Thread() {
			// @Override
			// public void run() {
				// cluster.killTopology("Topology");
				// cluster.shutdown();
			// }
		// });
		
		
		
	}

}
