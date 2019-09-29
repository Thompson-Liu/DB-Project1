package parser;
import java.util.*;
import java.io.*;
/**
 * Generating large size data table 
 * to test our implementation
 *
 */
public class GeneratingTests {
	
	static String fileName ="Natural";
	static int row= 1000;
	static int column = 3;
	
	public static void main(String[] arg0) {
		try {
			
			PrintWriter out = new PrintWriter(new File(fileName));
			Random rand = new Random();
			int count=0;
			int countTwo=0;
			while(count<row)
		      {
//		        	int a=rand.nextInt(10);
				int a = count;
		 			out.print(a);
		 			out.print(",");
		 			
		 			int b=count;
//		            int b=rand.nextInt(10);
		            out.print(b);
		            out.print(",");
		            
		            int c=count;
//		            int c=rand.nextInt(10);
		 			out.print(c);
//		 			out.print(",");
//		 			
//		 			int d=rand.nextInt(10);
//		 			out.print(d);
//		 			out.print(",");
//		 			
//		 			int e=rand.nextInt(10);
//		 			out.print(e);
//		 			out.print(",");
//		 			
//		 			int f=1;
//		 			out.print(f);
		 			
		 			
		            count++;
		            out.println();
		      }
			
			
			
			out.close();
		}
		catch(IOException e) {
			System.out.println("hmm not good! ");
		}
	}
	
}
