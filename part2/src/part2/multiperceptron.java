package part2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class multiperceptron {

	
	static ArrayList<String> stored_training_data= new ArrayList<String>();
	static ArrayList<Integer> stored_training_label= new ArrayList<Integer>();
	static ArrayList<String> stored_test_data= new ArrayList<String>();
	static ArrayList<Integer> stored_test_label= new ArrayList<Integer>();
	static double[][][]w=new double[10][28][28];//weight factor
	static int[]b=new int[10];//bias
	
	
	
	public static ArrayList<Integer[][]> read_training_files() { //read training files

		   String s;
		   FileInputStream file;
		   
		try {//reading training images
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
		
		try {//reading training labels
			   file = new FileInputStream("traininglabels");
			   InputStreamReader isr=new InputStreamReader(file);
			   BufferedReader br=new BufferedReader(isr);
			   while((s=br.readLine())!=null){
				   stored_training_label.add(Integer.parseInt(s));
				}br.close();
		} catch (IOException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}
		
		/*
		 * 
		 * Save training images to an ArrayList<Integer[][]>
		 * 
		 * */

		ArrayList<Integer[][]> k= new ArrayList<Integer[][]>();
		
		/*
		 * 
		 * 从读取的training data里面取接下来的28行出来，存到一个二维数组里（就是下一个图）
		 * 
		 */
		
		for (int i=0;i<5000;i++){
			Integer[][]c=new Integer[28][28];
			for (int j=0;j<28;j++){
				char[]temp=stored_training_data.get(0).toString().toCharArray();
				   for (int k1=0;k1<28;k1++){
					   if (temp[k1]!=' '){
						   c[j][k1]=1;
					   }else c[j][k1]=0;
				   }
				   stored_training_data.remove(0);
			}

			k.add(c);

			
		}
		

		return k;		
		
	}
	

	
	public static void read_test_files(){//reading test files

		   String s;
		   FileInputStream file;
		   
		try {//reading testing images
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
		
		try {//reading test labels and save to an ArrayList<Integer>
			   file = new FileInputStream("testlabels");
			   InputStreamReader isr=new InputStreamReader(file);
			   BufferedReader br=new BufferedReader(isr);
			   while((s=br.readLine())!=null){
				   stored_test_label.add(Integer.parseInt(s));
				}br.close();
		} catch (IOException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}
		
	}
		
	public static int[][] test_sample(){//return next 28x28 image from test images
		/*
		 * 
		 * 从读取的test data里面取接下来的28行出来，存到一个二维数组里（就是下一个图）
		 * 
		 * */
		int[][] c= new int[28][28];
		char[]temp= new char[28];
		for (int j=0;j<28;j++){
			   temp=stored_test_data.get(0).toString().toCharArray();
			   for (int k=0;k<28;k++){
				   if (temp[k]!=' '){
					   c[j][k]=1;
				   }else c[j][k]=0;
			   }
			   stored_test_data.remove(0);
		}
		return c;
	}
	
	
	
	public static void initial_w(){//initial weight factors
		for (int i=0;i<10;i++){
			for (int j=0;j<28;j++){
				for (int k=0;k<28;k++){
					w[i][j][k]=2.0-4.0*Math.random();//weight factor *******tune********
				}
			}
		}
	}
	
	
	public static void training(){//training process
		System.out.println("Learning Curve Table");
		ArrayList<Integer[][]> c= new ArrayList<Integer[][]>();
		ArrayList<Integer>order=new ArrayList<Integer>();
		int t=0;		
		c=read_training_files();//get training data
		initial_w();//initialize w

		for(int i=0;i<5000;i++){
			order.add(i);
		}

		
		while(t<30){//epoch loop *******tune*******
			int incorrect=0;
			double alpha=0.1/(t+0.1);//decaying rate *******tune*******
			
			for (int i=0;i<5000;i++){//sample loop
			
				int index=order.get(i);
				
				Integer[][]sample=c.get(index);//get next sample				
				double max=Integer.MIN_VALUE;
				int result=0;//identification result for each sample				
				for (int j=0;j<10;j++){//identification loop
					double sum=0;
					for (int x=0;x<28;x++){
						for (int y=0;y<28;y++){
							sum+= w[j][x][y]*(double)sample[x][y];// w*x for each element
						}
					}
					sum+=b[j];//******bias tune*****
					if (sum>max){//find the class with biggest output sgn(w*x)
						max=sum;
						result=j;
					}
				}
			
				int true_result=stored_training_label.get(index);//get the true class for this sample								
				if (result!=true_result){//update weight factors if the two results are not the same	
					incorrect++;
					b[true_result]+=alpha;
					b[result]-=alpha;
					for (int x=0;x<28;x++){
						for (int y=0;y<28;y++){
							w[true_result][x][y]+=alpha*(double)sample[x][y];					
							w[result][x][y]-=alpha*(double)sample[x][y];
						}
					}										
				}
				
				
			}
			double rate=1-(double)incorrect/5000.0;

			System.out.println(rate);

			t++;//update epoch
			Collections.shuffle(order);//change the order of training samples //******order tune****
		}	
		
		System.out.println("");
		System.out.println("");
				
	}
	
	
	public static void testing(){//testing process
		
		read_test_files();
		int counter=0;//count the number of test samples
		int correct=0;//count the number of right classification
		int[][]confusion_matrix=new int[10][10];
		int[]digit_counter=new int[10];
		while (!stored_test_data.isEmpty()){
			counter++;
			int [][] c= test_sample();//get next test image	
			double max=Integer.MIN_VALUE;
			int result=0;
			
			for (int j=0;j<10;j++){//identification loop
				double sum=0;
				for (int x=0;x<28;x++){
					for (int y=0;y<28;y++){
						sum+= w[j][x][y]*c[x][y];
					}
				}
				sum+=b[j]; //******bias tune*****
					if (sum>max){
						max=sum;
						result=j;
					}
			}
		//	System.out.println(result+"|"+stored_test_label.get(0));
			int true_result=stored_test_label.get(0);
			confusion_matrix[true_result][result]++;
			digit_counter[true_result]++;
			if (result==true_result)correct++;
			
			stored_test_label.remove(0);
			
		}
		double s= (double)correct/(double)counter;
	//	System.out.println("");
		System.out.println("The Overall Accuracy is "+ s);
		System.out.println("");
		System.out.println("");
		System.out.println("Confusion Matrix(Ratio)");		
		for(int i=0;i<10;i++){
			for (int j=0;j<10;j++){
				double z=((double)confusion_matrix[i][j]/(double)digit_counter[i]);
				System.out.printf("%.3f",z);
				System.out.print(" ");
			}
			System.out.println("");
		}
		
	}
	
	
	public static void main(String args[]){

	
		training();
		testing();
		
		for(int i=0;i<10;i++){
			for (int x=0;x<28;x++){
				for (int y=0;y<28;y++){
					if(w[i][x][y]>5){
						System.out.print("*");
					}else if (w[i][x][y]>3.5){
						System.out.print("*");
					}else if (w[i][x][y]<-3){
						System.out.print("o");
					}else System.out.print(".");
//					System.out.printf("%+6.3f",w[i][x][y]);	
//					System.out.print(" ");
				}
				System.out.println("");
			}
			System.out.println("");
		}

	}
}
