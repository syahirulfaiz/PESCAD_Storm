package com.faiz.storm;


import java.util.Arrays;
import java.util.List;

/**
 * <br> - A helper class for TF-IDF score
 * @version 2020.08
 * @since 2020-08-01
 */
public class TFIDFCompute {
	/**
	 * @param doc  list of document
	 * @param term String represents a word
	 * @return term frequency of word in document
	 */
	public double tf(List<String> doc, String term) {
		double result = 0;
		for (String word : doc) {
			if (term.equalsIgnoreCase(word))
				result++;
			//System.out.println("tf_result:"+result);
		}
		return result / doc.size();
	}

	/**
	 * @param docs list of list of document represents the all document/dataset
	 * @param term String represents a word
	 * @return the inverse term frequency of word in all documents
	 */
	public double idf(List<List<String>> docs, String term) {
		double n = 0;
		for (List<String> doc : docs) {
			for (String word : doc) {
				if (term.equalsIgnoreCase(word)) {
					n++;
					break;
				}
			}
		}
		return Math.log(docs.size() / n);
	}

	/**
	 * @param doc  a text document
	 * @param docs all documents
	 * @param term a word
	 * @return the TF-IDF of word
	 */
	public double tfIdf(List<String> doc, List<List<String>> docs, String term) {
		return tf(doc, term) * idf(docs, term);

	}

	public static void main(String[] args) {


	}


}