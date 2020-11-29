package com.faiz.storm;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.math3.distribution.PoissonDistribution;

/**
 * <br> - PESCAD : Poisson Event Stream Collective Anomaly Detection 
 * <br> - A Bolt where we implement the PESCAD algorithm, to detect anomaly based a rate of the current interval
 * @author Syahirul Faiz
 * @version 2020.08
 * @since 2020-08-01
 */
public class PESCADBolt extends BaseRichBolt {

	private static final long serialVersionUID = 7832788607967406072L;

	/** Number of seconds before the top list will be cleared. */
	private final long clearIntervalSec;

	/** Marking the clearing time */
	private long lastClearTime;

	/** Container for all incoming tweets. */
	private Map<String, String> allTweetList = new HashMap<String, String>();

	/** variable for calculating actual event occurrences. */
	private double sumOfActualOccurences =0;

	/** List for record event occurrences in each intervals. */
	private List<Integer> eventCountFromAllIntervalList = new CopyOnWriteArrayList<Integer>();
	private Map<String, String> idStatusFromCurrentIntervalList = new HashMap<String, String>(); 
	private List<String> tempAntiRepeat=new CopyOnWriteArrayList<String>();

	/**
	 * <br> - Constructor for PESCADBolt
	 * @param clearIntervalSec interval for clearing the list in seconds
	 */
	public PESCADBolt(long clearIntervalSec) {
		this.clearIntervalSec = clearIntervalSec;
	}

	/**
	 * <br> - collect tuples from PreprocessingBolt
	 */
	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
		lastClearTime = System.currentTimeMillis();
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
	}

	/**
	 * <br> - Calculating the event rate in current interval
	 * <br> - Predict the occurrences based on the event rate
	 * <br> - The fundamental principle is to predict the occurrence by using the Poisson distribution and compare it with real events occurrence
	 * <br> - if we compare the Actual_Occurrences larger than Predicted_Occurrences, we mark all of the id_status in that current interval as the group/collective anomaly
	 */
	@Override
	public void execute(Tuple input) {        

		//start timing in milliseconds
		long start = System.currentTimeMillis();

		//extract id_status and is_event field from previous tuple
		String id_status = (String) input.getValueByField("id_status");
		String is_event = (String) input.getValueByField("is_event");

		//collect all id_status from all tweets, and push that into ALLTWEETS ARRAY (A)
		if(allTweetList.get(id_status)==null) {
			allTweetList.put(id_status, "");
			System.err.println("allTweetList size="+allTweetList.size());
		}


		//TODO if it is an event, then collect that, and push that into an EVENT ARRAY (B)
		if( (is_event.equalsIgnoreCase("1"))
				&& idStatusFromCurrentIntervalList.get(id_status)==null) {
			idStatusFromCurrentIntervalList.put(id_status, "");
		}

		//mark the current interval for poisson calculation
		long now = System.currentTimeMillis();

		//restart calculation per every clearIntervalSec & initialise the eventCountFromAllIntervalList 
		if (now - lastClearTime > clearIntervalSec * 1000) {
			restartEventCalculationForCurrentInterval();

			lastClearTime = now; 
		}



		//=============PESCAD Algorithm start here:=============
		//calculate average occurences
		double lambda = getEventCountAverage();
		System.err.println("lambda="+lambda);
		System.err.println("sumOfActualOccurences="+sumOfActualOccurences);
		//lambda can't be zero org.apache.commons.math3.exception.NotStrictlyPositiveException: mean (0)
		if(lambda>0) {

			//calculate count of event for current interval (from EVENT ARRAY (B) size)
			int eventCountCurrentInterval = idStatusFromCurrentIntervalList.size();	
			System.err.println("idStatusFromCurrentIntervalList size="+eventCountCurrentInterval+" keys="+idStatusFromCurrentIntervalList.keySet());

			//calculate the probability of occurrences, based on the average occurrences and current interval occurrences
			PoissonDistribution p = new PoissonDistribution(lambda, PoissonDistribution.DEFAULT_EPSILON);
			double currentIntervalProbability = p.probability(eventCountCurrentInterval);
			System.err.println("currentIntervalProbability ("+eventCountCurrentInterval+")="+currentIntervalProbability);

			//Therefore, based on probability above and ALLTWEETS ARRAY (A) size, we can have an "Estimated Occurrences (C)"
			double sumOfPredictedOccurences = currentIntervalProbability * allTweetList.size() ;
			System.err.println("sumOfPredictedOccurences="+sumOfPredictedOccurences+"::<=currentIntervalProbability ["+currentIntervalProbability+"] * allTweetList.size() ["+allTweetList.size()+"]");

			//if "Estimated Occurrences (C)" < "Actual Occurrences (D)", then it is an anomaly (How to get (D)? see below)
			if(sumOfPredictedOccurences < sumOfActualOccurences) {
				System.err.println("ANOMALY="+idStatusFromCurrentIntervalList.keySet());

				//end timing
				long end = System.currentTimeMillis();
				double elapsedTime = (double) (end - start)/1000;

				//TODO mark all current id_status as anomaly
				markCurrentIntervalAsAnomaly(idStatusFromCurrentIntervalList, String.valueOf(elapsedTime));

			}		

		}

	}

	/**
	 * <br> - to restart the timing and refresh the list every pre-defined interval
	 */
	public void restartEventCalculationForCurrentInterval() {
		//add current count to eventCountFromAllIntervalList
		eventCountFromAllIntervalList.add(idStatusFromCurrentIntervalList.size());            	
		System.err.println("eventCountFromAllIntervalList ="+eventCountFromAllIntervalList);

		//clear and restart the event counter
		idStatusFromCurrentIntervalList.clear();
		System.err.println("idStatusFromCurrentIntervalList ="+idStatusFromCurrentIntervalList);
	}

	/**
	 * <br> - Method for calculating the average of event rate
	 * @return lambda/average for the event count
	 */
	public double getEventCountAverage() {
		Integer sum = 0;
		double lambda = 0.0;
		if(!eventCountFromAllIntervalList.isEmpty()) {
			for (Integer count : eventCountFromAllIntervalList) {
				sum += count;
			}
			//TODO : this is how we get the "Actual Occurrences (D)"
			sumOfActualOccurences = sum; 
			lambda = (double) sumOfActualOccurences / allTweetList.size();
		}

		return lambda;


	}


	/**
	 * <br> - method for marking the anomaly based on the id_status list
	 * @param idStatusFromCurrentIntervalList list of id_status to be marked as anomaly
	 * @param elapsedTime record the elapsed time
	 */
	public void markCurrentIntervalAsAnomaly(Map<String, String> idStatusFromCurrentIntervalList, String elapsedTime) {
		//TODO Think about BULK INSERT, rather than multiple Helper instance. DBCONN PROBLEM !!!

		for (String id_status : idStatusFromCurrentIntervalList.keySet()) {
			if (!tempAntiRepeat.contains(id_status)) {
				String[] toBeWritten= {"0","1","2","3"};
				toBeWritten[0]= String.valueOf(id_status.substring(0, Math.min(String.valueOf(id_status).length(), 30)));

				toBeWritten[1]= String.valueOf(elapsedTime.substring(0, Math.min(String.valueOf(elapsedTime).length(), 5)));

				//to get the class name of the bolts : from_class_name
				toBeWritten[2] = String.valueOf(this.getClass().getSimpleName()).substring(0, Math.min(String.valueOf(this.getClass().getSimpleName()).length(), 25)); 

				String hostname = "";
				try {
					hostname = InetAddress.getLocalHost().getHostName();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//to get the host name of the machine : from_machine_name
				toBeWritten[3]=String.valueOf(hostname).substring(0, Math.min(String.valueOf(hostname).length(), 15));

				String query = " INSERT INTO anomaly (id_status, elapsed_time, from_class_name, from_machine_name) VALUES (?,?,?,?) ";
				Helper helper = new Helper();
				helper.writeToTable(toBeWritten, query);	
			}
			tempAntiRepeat.add(id_status);
		}

	}


}
