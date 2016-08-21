package Part1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class identify_faces {
	
	static int[] class_counter=new int[2];
	static int current_class;
	static int[][][] pixel_counter=new int[2][70][60];
	static double[][][] color_likelyhood = new double[2][70][60];
	static double[][][] white_likelyhood = new double[2][70][60];
	static double[][][] posterior_temp = new double[2][70][60];	
	static double[] prior = new double[2];
	static double[] posterior = new double[2];
	static int training_counter=0;

	static ArrayList<String> stored_training_data= new ArrayList<String>();
	static ArrayList<Integer> stored_training_label= new ArrayList<Integer>();
	static ArrayList<String> stored_test_data= new ArrayList<String>();
	static ArrayList<Integer> stored_test_label= new ArrayList<Integer>();
	static ArrayList<Integer> test_result= new ArrayList<Integer>();
	
	
	public static void read_files1() { //read training and testing images
		ArrayList<String>list1=new ArrayList<String>();
		ArrayList<String>list2=new ArrayList<String>();
		   String s;
		   FileInputStream file;
		try {//read training images
			   file = new FileInputStream("facedatatrain");
			   InputStreamReader isr=new InputStreamReader(file);
			   BufferedReader br=new BufferedReader(isr);
			   while((s=br.readLine())!=null){
				   stored_training_data.add(s);
				}br.close();
		} catch (IOException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}
		try {//read training labels
			   file = new FileInputStream("facedatatrainlabels");
			   InputStreamReader isr=new InputStreamReader(file);
			   BufferedReader br=new BufferedReader(isr);
			   while((s=br.readLine())!=null){
				   list1.add(s);
				}br.close();
		} catch (IOException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}
		try {//read testing images
			   file = new FileInputStream("facedatatest");
			   InputStreamReader isr=new InputStreamReader(file);
			   BufferedReader br=new BufferedReader(isr);
			   while((s=br.readLine())!=null){
				   stored_test_data.add(s);
				}br.close();
		} catch (IOException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}
		try {//read testing labels
			   file = new FileInputStream("facedatatestlabels");
			   InputStreamReader isr=new InputStreamReader(file);
			   BufferedReader br=new BufferedReader(isr);
			   while((s=br.readLine())!=null){
				   list2.add(s);
				}br.close();
		} catch (IOException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}

		for (int i=0;i<list1.size();i++){
			stored_training_label.add(Integer.parseInt(list1.get(i)));
		}
		for (int i=0;i<list2.size();i++){
			stored_test_label.add(Integer.parseInt(list2.get(i)));
		}
	}
	
	public static char[][] get_next_image1(String which){
		
		char[][]c=new char[70][60];
		if(which=="training"){  
			for(int i=0;i<70;i++){
				   c[i]= stored_training_data.get(0).toString().toCharArray();
				   stored_training_data.remove(0);
			   }
		}else {
			for(int i=0;i<70;i++){
				   c[i]= stored_test_data.get(0).toString().toCharArray();
				   stored_test_data.remove(0);
			   }
		}
		return c;
	}
	
	public static void main(String args[]){
		
		char[][] training=new char[70][60];
		char[][] testing=new char[70][60];
		
		read_files1();
		
		/*
		 * 
		 * Training Process
		 * 
		 * */
		while(training_counter<=200/*# of training samples*/){
		
		current_class=stored_training_label.get(training_counter);//find the given class
		training=get_next_image1("training");//read the corresponding training image
		
		for(int i=0;i<70;i++){//counting the frequency of colored pixels 
			for (int j=0;j<60;j++){
				if(training[i][j]!=' ')pixel_counter[current_class][i][j]++;
			}
		}
		
		for (int i=0;i<2;i++){//counting the number of each class appeared in training sample
			if (current_class==i)class_counter[i]++;
		}
		int smooth=1;//smooth factor from 1 to 50
		for(int i=0;i<2;i++){//calculate the likelihood for white or colored pixels for given class
			for (int j=0;j<70;j++){
				for (int k=0;k<60;k++){
					color_likelyhood[i][j][k]=(((double)(pixel_counter[i][j][k]+smooth)/(double)(class_counter[i]+smooth*2)));
					white_likelyhood[i][j][k]=(((double)(class_counter[i]-pixel_counter[i][j][k]+smooth)/(double)(class_counter[i]+smooth*2)));
				}
			}
		}			
		
		training_counter++;
		for (int i=0;i<2;i++){
		prior[i]=((double)class_counter[i])/((double)training_counter);
			}
		}
//			for (int j=0;j<70;j++){
//				for (int k=0;k<60;k++){
//					double z=color_likelyhood[0][j][k];
//					System.out.printf("%.3f",z);
//					System.out.print(" | ");
//					}
//				System.out.println("");
//				}
		/*
		 * 
		 * 
		 * TESTING
		 * 
		 * */
		while(!stored_test_data.isEmpty()){
			int result_temp = 0;
			double max=Integer.MIN_VALUE;
			
			testing=get_next_image1("testing");
			
			for(int i=0;i<2;i++){//calculate posterior for each class
				posterior[i]=Math.log(prior[i]);
				for (int j=0;j<70;j++){
					for(int k=0;k<60;k++){
						if(testing[j][k]==' '){
							posterior_temp[i][j][k]=white_likelyhood[i][j][k];
						}else posterior_temp[i][j][k]=color_likelyhood[i][j][k];
						
						posterior[i]+=Math.log(posterior_temp[i][j][k]);
//						System.out.println(posterior[i]);
					}
				}
				if(posterior[i]>=max){
					max=posterior[i];
					result_temp=i;
				}
			}
			test_result.add(result_temp);
		}
		
		int n=test_result.size();
		int[] number_counter=new int[2];
		int correct_total=0;
		int[] correct_each=new int[2];
		/*
		 * 
		 * GET DATAS
		 * 
		 * */
		for (int i=0;i<test_result.size();i++){
			number_counter[stored_test_label.get(i)]++;
			if(test_result.get(i)==stored_test_label.get(i)) {
				correct_total++;
				correct_each[test_result.get(i)]++;
			}
			System.out.print(test_result.get(i)+" | "+stored_test_label.get(i));
			System.out.println("");
		}
		double percentage= (double)correct_total/(double)n;
		
		for (int i=0;i<2;i++){
			double each_percentage=(double)correct_each[i]/(double)number_counter[i];
			System.out.println("");
			System.out.println("The Classification Ratio for "+ i +" is "+each_percentage);

		}
		System.out.println("Total Percentage = "+percentage);
		System.out.println("");
		System.out.println("");
	}
	
}
