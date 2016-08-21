package Part1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class identify_number {

	static int[] class_counter=new int[10];
	static int current_class;
	static int[][][] pixel_counter=new int[10][28][28];
	static double[][][] color_likelyhood = new double[10][28][28];
	static double[][][] white_likelyhood = new double[10][28][28];
	static double[][][] posterior_temp = new double[10][28][28];	
	static double[] prior = new double[10];
	static double[] posterior = new double[10];
	static int training_counter=0;
	static char [][][] best_sample=new char[10][28][28];
	static double []best_sample_counter=new double[10];
	static char [][][] worst_sample=new char[10][28][28];
	static double []worst_sample_counter=new double[10];
	

	static ArrayList<String> stored_training_data= new ArrayList<String>();
	static ArrayList<Integer> stored_training_label= new ArrayList<Integer>();
	static ArrayList<String> stored_test_data= new ArrayList<String>();
	static ArrayList<Integer> stored_test_label= new ArrayList<Integer>();
	static ArrayList<Integer> test_result= new ArrayList<Integer>();
	
	
	
	public static void read_files() { //read training and testing images
		ArrayList<String>list1=new ArrayList<String>();
		ArrayList<String>list2=new ArrayList<String>();
		   String s;
		   FileInputStream file;
		try {//read training images
			   file = new FileInputStream("trainingimages");
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
			   file = new FileInputStream("traininglabels");
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
			   file = new FileInputStream("testimages");
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
			   file = new FileInputStream("testlabels");
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

	public static char[][] get_next_image(String which){
		
		char[][]c=new char[28][28];
		if(which=="training"){  
			for(int i=0;i<28;i++){
				   c[i]= stored_training_data.get(0).toString().toCharArray();
				   stored_training_data.remove(0);
			   }
		}else {
			for(int i=0;i<28;i++){
				   c[i]= stored_test_data.get(0).toString().toCharArray();
				   stored_test_data.remove(0);
			   }
		}
		return c;
	}
	
	public static void printlikeyhood(int n){
		
		System.out.println("");
		System.out.println(n+"'s feature likelyhood  [+]high probability   [.]medium probability   [ ]low probability");
		for (int i=0;i<28;i++){
			for (int j =0;j<28;j++){
				double z=Math.log(color_likelyhood[n][i][j]);
				if (z>-0.85){
					System.out.print("+");
				}else if (z<-4){
					System.out.print(" ");
				}else System.out.print(".");
			}
			System.out.println("");
		}
	}
	
	public static void main(String args[]){
		
		char[][] training=new char[28][28];
		char[][] testing=new char[28][28];
		
		for (int i=0;i<10;i++){
			best_sample_counter[i]=Integer.MIN_VALUE;
			worst_sample_counter[i]=Integer.MAX_VALUE;
		}
		
		read_files();
		
		/*
		 * 
		 * Training Process
		 * 
		 * */
		while(training_counter<=4000/*# of training samples*/){
		
		current_class=stored_training_label.get(training_counter);//find the given class
		training=get_next_image("training");//read the corresponding training image
		
		for(int i=0;i<28;i++){//counting the frequency of colored pixels 
			for (int j=0;j<28;j++){
				if(training[i][j]!=' ')pixel_counter[current_class][i][j]++;
			}
		}
		
		for (int i=0;i<10;i++){//counting the number of each class appeared in training sample
			if (current_class==i)class_counter[i]++;
		}
		int smooth=1;//smooth factor from 1 to 50
		for(int i=0;i<10;i++){//calculate the likelihood for white or colored pixels for given class
			for (int j=0;j<28;j++){
				for (int k=0;k<28;k++){
					color_likelyhood[i][j][k]=(((double)(pixel_counter[i][j][k]+smooth)/(double)(class_counter[i]+smooth*2)));
					white_likelyhood[i][j][k]=(((double)(class_counter[i]-pixel_counter[i][j][k]+smooth)/(double)(class_counter[i]+smooth*2)));
				}
			}
		}			
		
		training_counter++;
		for (int i=0;i<10;i++){
		prior[i]=((double)class_counter[i])/((double)training_counter);
			}
	}
		

		/*
		 * 
		 * TESTING
		 * 
		 * */
		while(!stored_test_data.isEmpty()){
			int result_temp = 0;
			double max=Integer.MIN_VALUE;
			
			testing=get_next_image("testing");
			
			for(int i=0;i<10;i++){//calculate posterior for each class
				posterior[i]=Math.log(prior[i]);
				for (int j=0;j<28;j++){
					for(int k=0;k<28;k++){
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
				if(posterior[i]>=best_sample_counter[i]){
					best_sample_counter[i]=posterior[i];	
					for (int w=0;w<28;w++){
						for (int x=0;x<28;x++){
							best_sample[i][w][x]=testing[w][x];
						}
					}
					
				}
				if(posterior[i]>=-200&&posterior[i]<=worst_sample_counter[i]){
					worst_sample_counter[i]=posterior[i];	
					for (int w=0;w<28;w++){
						for (int x=0;x<28;x++){
							worst_sample[i][w][x]=testing[w][x];
						}
					}
					
				}
			}
			test_result.add(result_temp);
		}
		int n=test_result.size();
		int[] number_counter=new int[10];
		int correct_total=0;
		int[] correct_each=new int[10];
		int[][] confusion_matrix=new int[10][10];
		/*
		 * 
		 * GET DATAS
		 * 
		 * */
		for (int i=0;i<test_result.size();i++){
			confusion_matrix[stored_test_label.get(i)][test_result.get(i)]++;
			number_counter[stored_test_label.get(i)]++;
			if(test_result.get(i)==stored_test_label.get(i)) {
				correct_total++;
				correct_each[test_result.get(i)]++;
			}
			System.out.print(test_result.get(i)+" | "+stored_test_label.get(i));
			System.out.println("");
		}
		double percentage= (double)correct_total/(double)n;
		
		for (int i=0;i<10;i++){
			double each_percentage=(double)correct_each[i]/(double)number_counter[i];
			System.out.println("");
			System.out.println("The Classification Ratio for "+ i +" is "+each_percentage);

		}
		System.out.println("Total Percentage = "+percentage);
		System.out.println("");
		System.out.println("");
		System.out.println("Confusion Matrix(frequency)");
		for(int i=0;i<10;i++){
			for (int j=0;j<10;j++){
				System.out.print(confusion_matrix[i][j]+" | ");
			}
			System.out.println("");
		}
		System.out.println("");
		System.out.println("");
		System.out.println("Confusion Matrix(Ratio)");		
		for(int i=0;i<10;i++){
			for (int j=0;j<10;j++){
				double z=((double)confusion_matrix[i][j]/(double)number_counter[i]);
				System.out.printf("%.3f",z);
				System.out.print(" ");
			}
			System.out.println("");
		}
		
		/*
		 * 
		 * Calculate Odds for (4,9) (5,3) (7,9) (8,3)
		 * 
		 * */
		double[][] odd_pair49=new double[28][28];
		double[][] odd_pair53=new double[28][28];
		double[][] odd_pair79=new double[28][28];
		double[][] odd_pair83=new double[28][28];
		
		for (int i=0;i<28;i++){
			for (int j =0;j<28;j++){
				odd_pair49[i][j]=(double)color_likelyhood[4][i][j]/(double)color_likelyhood[9][i][j];
				odd_pair53[i][j]=(double)color_likelyhood[5][i][j]/(double)color_likelyhood[3][i][j];
				odd_pair79[i][j]=(double)color_likelyhood[7][i][j]/(double)color_likelyhood[9][i][j];
				odd_pair83[i][j]=(double)color_likelyhood[8][i][j]/(double)color_likelyhood[3][i][j];
			}
		}
		printlikeyhood(4);
		printlikeyhood(9);
		System.out.println("");
		System.out.println("Odd pair for (4,9)/ [+]for 4's characteristics [ ] for common value "
				+ "[-]for 9' characteristics");
		for (int i=0;i<28;i++){
			for (int j =0;j<28;j++){
				double z=Math.log(odd_pair49[i][j]);
				if (z>0.5){
					System.out.print("+");
				}else if (z<-0.5){
					System.out.print("-");
				}else System.out.print(" ");
			}
			System.out.println("");
		}
		
		printlikeyhood(5);
		printlikeyhood(3);
		System.out.println("");
		System.out.println("Odd pair for (5,3)/ [+]for 5's characteristics [ ] for common value "
				+ "[-]for 3' characteristics");
		for (int i=0;i<28;i++){
			for (int j =0;j<28;j++){
				double z=Math.log(odd_pair53[i][j]);
				if (z>0.5){
					System.out.print("+");
				}else if (z<-0.5){
					System.out.print("-");
				}else System.out.print(" ");
			}
			System.out.println("");
		}

		printlikeyhood(7);
		printlikeyhood(9);
		System.out.println("");
		System.out.println("Odd pair for (7,9)/ [+]for 7's characteristics [ ] for common value "
				+ "[-]for 9' characteristics");
		for (int i=0;i<28;i++){
			for (int j =0;j<28;j++){
				double z=Math.log(odd_pair79[i][j]);
				if (z>0.5){
					System.out.print("+");
				}else if (z<-0.5){
					System.out.print("-");
				}else System.out.print(" ");
			}
			System.out.println("");
		}
		
		printlikeyhood(8);
		printlikeyhood(3);
		System.out.println("");
		System.out.println("Odd pair for (8,3)/ [+]for 8's characteristics [ ] for common value "
				+ "[-]for 3' characteristics");
		for (int i=0;i<28;i++){
			for (int j =0;j<28;j++){
				double z=Math.log(odd_pair83[i][j]);
				if (z>0.5){
					System.out.print("+");
				}else if (z<-0.5){
					System.out.print("-");
				}else System.out.print(" ");
			}
			System.out.println("");
		}
		
		for (int i=0;i<10;i++){
			System.out.println(i+" Best Sample");
			for(int j=0;j<28;j++){
				for (int k=0;k<28;k++){
						
						System.out.print(best_sample[i][j][k]);

				}
				System.out.println("");
			}
			System.out.println("");
			System.out.println("");
		}
		
		for (int i=0;i<10;i++){
			System.out.println(i+" Worst");
			for(int j=0;j<28;j++){
				for (int k=0;k<28;k++){
						
						System.out.print(worst_sample[i][j][k]);

				}
				System.out.println("");
			}
			System.out.println("");
			System.out.println("");
		}
		
	}
}
