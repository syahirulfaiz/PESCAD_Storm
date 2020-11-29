package com.faiz.storm;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.distribution.PoissonDistribution;

import org.deeplearning4j.clustering.algorithm.Distance;
import org.deeplearning4j.clustering.cluster.Cluster;
import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.clustering.kmeans.KMeansClustering;
import org.deeplearning4j.models.word2vec.Word2Vec;

import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dimensionalityreduction.PCA;
import org.nd4j.linalg.factory.Nd4j;

import com.mysql.fabric.xmlrpc.base.Array;

import backtype.storm.tuple.Values;
import twitter4j.HashtagEntity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <br> - Class for WordEmbedding Word2Vec with KMeans Clustering for Visualisation 2D Plot
 * @author Syahirul Faiz
 * @version 2020.08
 * @since 2020-08-08
 *
 */
public class Word2VecRawKMeans {

	List<Point> pointsLst = new CopyOnWriteArrayList<Point>();

	public static void main(String[] args) throws Exception {


		//    	List<String> tempTopic = new ArrayList<String>();
		//    	tempTopic.add("covid");
		//    	tempTopic.add("sell");
		//    	tempTopic.add("mask;hit;covid;death;corona;");
		//    	tempTopic.add("doctor");
		//    	tempTopic.add("kick");
		//    	tempTopic.add("the");
		//    	for (String temp : tempTopic) {
		//    		test(temp);
		//    		//System.out.println(temp);
		//    	}

		//start timing
		long start = System.currentTimeMillis();


		//TODO DUMMY DELETE
		PoissonDistribution p = new PoissonDistribution(1.0, PoissonDistribution.DEFAULT_EPSILON);

		System.out.println("pmf="+p.probability(3));
		System.out.println("lower="+  p.cumulativeProbability(3));

		//end timing
		long end = System.currentTimeMillis();
		double elapsedTime = (double)(end - start)/1000;


		System.err.println("elapsedTime="+(end - start)+" time="+elapsedTime);


	}






	/**
	 * <br> - A method for executing/testing the word2vec
	 * @param status text sentence tweet
	 * @param sentences a collection of keywords
	 */
	public void test(String status, Collection<String> sentences) {
		//start timing in milliseconds
		long start = System.currentTimeMillis();

		//convert the list into an iterable object
		SentenceIterator iter = new CollectionSentenceIterator(sentences);

		//create a tokenizer
		TokenizerFactory t = new DefaultTokenizerFactory();          

		/*
		 * StemmingPreprocessor = CommonPreprocessor + Porter  stemmer
            CommonPreprocessor apply regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
            strip all numbers, punctuation symbols and some special symbols are stripped off.
            forces lower case.
		 */
		t.setTokenPreProcessor(new CommonPreprocessor());

		Helper helper = new Helper();
		List<String> stopList = helper.getStopwordList();   

		//build Word2Vec model
		Word2Vec vec = new Word2Vec.Builder()
				//.minWordFrequency(5)
				.iterations(1)
				.layerSize(100)
				.seed(42)
				.windowSize(5)
				.stopWords(stopList)
				.iterate(iter)
				.tokenizerFactory(t)
				.learningRate(0.025)
				.build();

		//training the model
		vec.fit();		
		
		//=============================================KMEANS======================================
		
		//1. create a kmeanscluster instance
		int maxIterationCount = 3;
		int clusterCount = 5;
		Distance distanceFunction = org.deeplearning4j.clustering.algorithm.Distance.EUCLIDEAN;
		KMeansClustering kmc = KMeansClustering.setup(clusterCount, maxIterationCount, distanceFunction, true);


		//2. iterate over rows in the paragraphvector and create a List of paragraph vectors
		int i = 0;
		for (String word : vec.vocab().words()) {
			Point point = new Point(String.valueOf(i),word,vec.getWordVector(word));
			pointsLst.add(point);
			i=i+1;	
		}


		//[!!!] PERLU DI TRIGGER CENTER: tambahkan keyword : FINANCE, HEALTH, TECHNOLOGY, SCIENCE, FOOD; lalu set manual centernya dan getWordVector 

		ClusterSet cs =null;
		if(pointsLst.size()>1) {
			cs = kmc.applyTo(pointsLst);
		}

		int numOfPoints = pointsLst.size();
		
		//SEMENTARA SUKSES 
		pointsLst =  new CopyOnWriteArrayList<Point>();       

		List<Cluster> clsterLst = new ArrayList<Cluster>();
		if(cs!=null) {clsterLst = cs.getClusters();}



		//to get the class name of the bolts : from_class_name
		String from_class_name = "ClusteringBolt"; 

		
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


		elapsedTime = elapsedTime / numOfPoints; //TODO each amount of elapsed time
		
		//get all points from every cluster
		for(Cluster c: clsterLst) {
			Point center = c.getCenter();
			for(Point p: c.getPoints()) {       		

				String point_keyword = PCA.pca(vec.getWordVectorMatrix(p.getLabel()), 2, false).toString(2, true, 2).replaceAll("(\\[|\\]|\\s)","");
				String point_cluster = PCA.pca(c.getCenter().getArray().reshape(1,c.getCenter().getArray().size(0)), 2, false).toString(2, true, 2).replaceAll("(\\[|\\]|\\s)","");;

				//String word_count = String.valueOf(vec.getVocab().wordFrequency(p.getLabel()));
				writeToTable(p.getId(),p.getLabel(),point_keyword,c.getId(),point_cluster, String.valueOf(elapsedTime), from_class_name, from_machine_name);

			}
		}

	}



	/**
	 * <br> - A Method for record the field to table
	 * @param id_status id_status from the Twitter
	 * @param keyword keyword from a tweet
	 * @param point_keyword vector of a keyword
	 * @param id_cluster a centroid (id_cluster)
	 * @param point_cluster vector coordinate of the centroid
	 * @param elapsedTime time spent for word2vec
	 * @param from_class_name class name for this class
	 * @param from_machine_name machine name of the server
	 */
	public void writeToTable(String id_status, String keyword, String point_keyword, String id_cluster, String point_cluster, String elapsedTime, String from_class_name, String from_machine_name) {
		//define the index of the columns in Mysql
		String[] toBeWritten= {"0","1","2","3","4","5","6","7"}; 

		toBeWritten[0]= String.valueOf(id_status.substring(0, Math.min(String.valueOf(id_status).length(), 30)));
		toBeWritten[1]= String.valueOf(keyword.substring(0, Math.min(String.valueOf(keyword).length(), 20)));
		toBeWritten[2]= String.valueOf(point_keyword.substring(0, Math.min(String.valueOf(point_keyword).length(), 20)));
		toBeWritten[3]= String.valueOf(id_cluster.substring(0, Math.min(String.valueOf(id_cluster).length(), 40)));  
		toBeWritten[4]= String.valueOf(point_cluster.substring(0, Math.min(String.valueOf(point_cluster).length(), 20)));

		toBeWritten[5]= String.valueOf(elapsedTime.substring(0, Math.min(String.valueOf(elapsedTime).length(), 5)));
		toBeWritten[6]= String.valueOf(from_class_name.substring(0, Math.min(String.valueOf(from_class_name).length(), 25)));
		toBeWritten[7]= String.valueOf(from_machine_name.substring(0, Math.min(String.valueOf(from_machine_name).length(), 15)));

		
		
		String query = " INSERT INTO keyword_cluster (id_status, keyword, point_keyword, id_cluster, point_cluster, elapsed_time, from_class_name, from_machine_name) " + 
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?) " + 
				"ON DUPLICATE KEY UPDATE " + 
				"  id_status='"+toBeWritten[0]+"', "+
				"  point_keyword='"+toBeWritten[2]+"', "+
				"  id_cluster='"+toBeWritten[3]+"', "+
				"  point_cluster='"+toBeWritten[4]+"', "+
				"  elapsed_time='"+toBeWritten[5]+"', "+
				"  from_class_name='"+toBeWritten[6]+"', "+
				"  from_machine_name='"+toBeWritten[7]+"' "
				;

		Helper helper = new Helper();
		helper.writeToTable(toBeWritten, query);
	}
}
