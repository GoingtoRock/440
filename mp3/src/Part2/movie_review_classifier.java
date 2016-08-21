package Part2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class movie_review_classifier {

	
	private static ArrayList<Integer> test_labels= new ArrayList<Integer>();
	private static Queue<String> test_readings= new LinkedList<String>();
	private static ArrayList<Integer> filter_result= new ArrayList<Integer>();
	private static HashMap<String, Double> spam_train=new  HashMap<String, Double>();
	private static HashMap<String, Double> non_spam_train=new  HashMap<String, Double>();
	private static HashMap<String, Double> test_read=new  HashMap<String, Double>();
	private static double spam_word_counter_train=0; 
	private static double non_spam_word_counter_train=0;
	private static double unique_word_counter_spam=0;
	private static double unique_word_counter_nonspam=0;
	
	
	
	private static void read_files(){
		String s0;
		FileInputStream file;
	try {//read training images
		   file = new FileInputStream("rt-train.txt");
		   InputStreamReader isr=new InputStreamReader(file);
		   BufferedReader br=new BufferedReader(isr);
		   while((s0=br.readLine())!=null){
		   		String[] s1=s0.split(" ");//split " " [1][word1:1][word2:2].....
		   	if (s1[0].equals("1")){
		   		for (String s:s1){
		   			String[] s2=s.split(":");//split " " [1][word1][1][word2][2].....	
		   			if (s2.length>1){
		   			for(int i=0;i<s2.length;i+=2){
		   				spam_word_counter_train+=(double)Integer.parseInt(s2[1]);
		   				if(!spam_train.containsKey(s2[0])){
		   					spam_train.put(s2[0], (double)(Integer.parseInt(s2[1])));
		   				}else spam_train.put(s2[0], ((double)(Integer.parseInt(s2[1]))+spam_train.get(s2[0])));		   				
		   				}
		   			}
		   		}	   			
		   	}else{
		   		for (String s:s1){
		   			String[] s2=s.split(":");//split " " [-][1][word1][1][word2][2].....	
		   			if (s2.length>1){
		   			for(int i=0;i<s2.length;i+=2){
		   				non_spam_word_counter_train+=(double)Integer.parseInt(s2[1]);
		   				if(!non_spam_train.containsKey(s2[0])){
		   					non_spam_train.put(s2[0], (double)(Integer.parseInt(s2[1])));
		   				}else non_spam_train.put(s2[0], ((double)(Integer.parseInt(s2[1]))+non_spam_train.get(s2[0])));		   	
				   			}
				   		}
				  }	   
				   	
			}
		}
			   br.close();
	} catch (IOException e) {
		System.out.println("File not found");
		e.printStackTrace();
		}	
	
	try {//read test files
		   file = new FileInputStream("rt-test.txt");
		   InputStreamReader isr=new InputStreamReader(file);
		   BufferedReader br=new BufferedReader(isr);
		   		while((s0=br.readLine())!=null){
		   			test_readings.add(s0);
		   		}br.close();			
	} catch (IOException e) {
		System.out.println("File not found");
		e.printStackTrace();
	}
	
	}
	
	private static void test_assign_next(){
		
		test_read.clear();
		String[] s1=test_readings.poll().split(" ");//split " " [-][1][word1:1][word2:2].....
		if (s1[0].equals("1")){
		test_labels.add(1);
		}else test_labels.add(-1);
   		for (String s:s1){
   			String[] s2=s.split(":");//split " " [-1][word1][1][word2][2].....	
   			if (s2.length>1){
   			for(int i=0;i<s2.length;i+=2){
   				if(!test_read.containsKey(s2[0])){
   					test_read.put(s2[0], (double)Integer.parseInt(s2[1]));
   				}else test_read.put(s2[0], ((double)Integer.parseInt(s2[1])+test_read.get(s2[0])));		   				
   				}
   			}
   		}
	}
	
	private static int testing(){//compare 2 odds to determine if the mail is spam or not.
	
		test_assign_next();
		double spam_odd=posterior_spam();
		double nonspam_odd=posterior_nonspam();
		if (spam_odd>=nonspam_odd){
			return 1;
		}else return -1;
		
		
	}
	
private static int testing_bernuli(){//compare 2 odds to determine if the mail is spam or not.
		
		test_assign_next();
		double spam_odd=posterior_spam_bernuli();
		double nonspam_odd=posterior_nonspam_bernuli();
		if (spam_odd>=nonspam_odd){
			return 1;
		}else return -1;
		
		
	}
	
	private static double posterior_spam(){
		double odds=Math.log(spam_word_counter_train/(non_spam_word_counter_train+spam_word_counter_train));
		
		for (String s:test_read.keySet()){
			if(spam_train.containsKey(s)){
			double likelyhood=(spam_train.get(s)+1)/(spam_word_counter_train+unique_word_counter_spam);
			odds+=(Math.log(likelyhood)*test_read.get(s));
			}else {
				double likelyhood=(1)/(spam_word_counter_train+unique_word_counter_spam);
				odds+=(Math.log(likelyhood)*test_read.get(s));
			}
		}
		return odds;
	}
	
	private static double posterior_nonspam(){//calculate the posterior for bad review
		double odds=Math.log(non_spam_word_counter_train/(non_spam_word_counter_train+spam_word_counter_train));
		
		for (String s:test_read.keySet()){
			if(non_spam_train.containsKey(s)){
			double likelyhood=(non_spam_train.get(s)+1)/(non_spam_word_counter_train+unique_word_counter_nonspam);
			odds+=(Math.log(likelyhood)*test_read.get(s));
			}else {
				double likelyhood=(1)/(non_spam_word_counter_train+unique_word_counter_nonspam);
				odds+=(Math.log(likelyhood)*test_read.get(s));
			}
		}
		return odds;
	}
	
	private static double posterior_spam_bernuli(){//calculate the posterior for good review with bernoulli model
		double odds=Math.log(spam_word_counter_train/(non_spam_word_counter_train+spam_word_counter_train));	
		for (String s:test_read.keySet()){
			if(spam_train.containsKey(s)){
			double likelyhood=(1+1)/(spam_word_counter_train+unique_word_counter_spam);
			odds+=(Math.log(likelyhood)*test_read.get(s));
			}else {
				double likelyhood=(1)/(spam_word_counter_train+unique_word_counter_spam);
				odds+=(Math.log(likelyhood)*test_read.get(s));
			}
		}
		return odds;
		
	}
	
	private static double posterior_nonspam_bernuli(){//calculate the posterior for good review with bernoulli model
		double odds=Math.log(non_spam_word_counter_train/(non_spam_word_counter_train+spam_word_counter_train));
		
		for (String s:test_read.keySet()){
			if(non_spam_train.containsKey(s)){
			double likelyhood=(1+1)/(non_spam_word_counter_train+unique_word_counter_nonspam);
			odds+=(Math.log(likelyhood)*test_read.get(s));
			}else {
				double likelyhood=(1)/(non_spam_word_counter_train+unique_word_counter_nonspam);
				odds+=(Math.log(likelyhood)*test_read.get(s));
			}
		}
		return odds;		
		
	}
	
	private static void getuniquewords_number(){//get V: number of unique words
		
		for(double n:spam_train.values()) {
			double k=1;
			if (n==k)unique_word_counter_spam++;
		}
		
		for(double n:non_spam_train.values()) {
			double k=1;
			if (n==k)unique_word_counter_nonspam++;
		}
		
	}
	
	public static void show_result(String model){//performing testing and evaluation
		int total_counter1=0;
		int right_counter1=0;
		int total_counter0=0;
		int right_counter0=0;

		
		while(!test_readings.isEmpty()){
			
			if (!model.equals("bernoulli")){
			int r=testing();//change model here
			filter_result.add(r);
			}else {
				int r=testing_bernuli();//change model here
				filter_result.add(r);
			}
		}
		
		for (int i=0;i<test_labels.size();i++){

			if (filter_result.get(i)==1){
				total_counter1++;
				if (filter_result.get(i)==test_labels.get(i))right_counter1++;
			}else if (filter_result.get(i)==-1){
				total_counter0++;
				if (filter_result.get(i)==test_labels.get(i))right_counter0++;
			}
			
			System.out.println(filter_result.get(i)+" | "+test_labels.get(i));
		}
		
		double p1=(double)right_counter1/(double)total_counter1;
		double p0=(double)right_counter0/(double)total_counter0;
		
		System.out.println("");
		System.out.println("The Classification Rate for Good Review is "+p1);
		System.out.println("The Classification Rate for Bad Review is "+p0);
		
		double p01=(double)(total_counter0-right_counter0)/(double)total_counter0;
		double p10=(double)(total_counter1-right_counter1)/(double)total_counter1;
		System.out.println("");
		System.out.println("The Confusion Matrix");
		System.out.printf("%.3f",p0);
		System.out.print(" | ");
		System.out.printf("%.3f",p01);
		System.out.println("");
		System.out.printf("%.3f",p10);
		System.out.print(" | ");
		System.out.printf("%.3f",p1);
    	System.out.println(" ");
	
	}
	
	private static void get_top_20(){//find top 20 words withe highest likelyhood in each class
		
		
		int size1=spam_train.size();
		int size0=non_spam_train.size();
		ArrayList<Map.Entry<String, Double>> sorted_spam=new ArrayList<Map.Entry<String, Double>>(size1);
		ArrayList<Map.Entry<String, Double>> sorted_nonspam=new ArrayList<Map.Entry<String, Double>>(size0);
		
		sorted_spam.addAll(spam_train.entrySet());
		sorted_nonspam.addAll(non_spam_train.entrySet());
		
        ValueComparator vc = new ValueComparator();  
        Collections.sort(sorted_spam, vc);
        Collections.sort(sorted_nonspam, vc);

    	System.out.println("");
    	System.out.println("The top 20 likelyhood words in Good Review are (with frequence)");
        for (int i=0;i<20;i++){
        	String word=sorted_spam.get(i).getKey();
        	double frequence=sorted_spam.get(i).getValue();
        	System.out.print(word+" | "+frequence);
        	System.out.println("");
        	        	
        }
    	System.out.println("");
     	System.out.println("The top 20 likelyhood words in Bad Review are (with frequence)");
        for (int i=0;i<20;i++){
        	String word=sorted_nonspam.get(i).getKey();
        	double frequence=sorted_nonspam.get(i).getValue();
        	System.out.print(word+" | "+frequence);
        	System.out.println("");
        	        	
        }
        

	}
	
	
	 public static class ValueComparator implements Comparator<Map.Entry<String, Double>>{
	      
	        public int compare(Map.Entry<String, Double> mp1, Map.Entry<String, Double> mp2)   
	        {  
	            return (int) (-mp1.getValue() + mp2.getValue());
	       
	        } 
	       
	    }  
	
	public static void main(String args []){
		
		read_files();
		getuniquewords_number();		
		show_result("multinomial");//multinomial or bernoulli
		get_top_20();

	}
}
