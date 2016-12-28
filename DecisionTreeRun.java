/*
%    Copyright 2016 by Farhad Shakerin
% 
%    Permission to use this software is granted subject to the 
%    following restrictions and understandings: 
% 
%    1. This material is for educational and research purposes only. 
% 
%    2. Farhad Shakerin has provided this software AS IS. Farhad
%       has made no warranty or representation that the 
%       operation of this software will be error-free, and he is 
%       under no obligation to provide any services, by way of 
%       maintenance, update, or otherwise. 
% 
%    3. Any user of such software agrees to indemnify and hold 
%       harmless Farhad Shakerin from 
%       all claims arising out of the use or misuse of this 
%       software, or arising out of any accident, injury, or damage 
%       whatsoever, and from all costs, counsel fees and liabilities 
%       incurred in or about any such claim, action, or proceeding 
%       brought thereon. 
% 
%    4. Users are requested, but not required, to inform Farhad Shakerin
%       of any noteworthy uses of this software.
*/
package decisiontree;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class DecisionTreeRun {

    public static void main(String[] args) 
    {
        DecisionTree tree = new DecisionTree();
        tree.CreateDataHeader(new String[]{"cap_shape","cap_surface","cap_color",
                                           "bruises","odor","gill_attachment",
                                           "gill_spacing","gill_size","gill_color",
                                           "stalk_shape","stalk_root","stalk_surface_above_ring",
                                           "stalk_surface_below_ring","stalk_color_above_ring",
                                           "stalk_color_below_ring","veil_type","veil_color",
                                           "ring_number","ring_type","spore_print_color",
                                           "population","habitat"});
        try
        {
            FileInputStream fstream = new FileInputStream("mush_train.data");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            int line = 1;
            while ((strLine = br.readLine()) != null)   
            {
                 String[] sample_vec = strLine.split(",");
                 if(sample_vec.length != 23)
                 {
                     System.out.printf("invalid record format on line %d\n",line);
                     System.exit(-1);
                 }
                 String[] feature_vec = new String[22];
                 for(int i = 1 ; i < 23 ; i++)
                     feature_vec[i-1] = sample_vec[i];
                 if(!sample_vec[0].equals("p") && !sample_vec[0].equals("e"))
                 {
                     System.out.printf("invalid class %s on line %d\r\n", sample_vec[0],line);
                     System.exit(-1);
                 }
                 boolean bLabel = (sample_vec[0].equals("p")) ? true : false;
                 tree.AddSample(feature_vec, bLabel, true);
                 line++;
            }
            br.close();
            tree.MakeTree();
            tree.PrintTree();
            
            fstream = new FileInputStream("mush_test.data");
            br = new BufferedReader(new InputStreamReader(fstream));
            line = 1;
            while ((strLine = br.readLine()) != null)   
            {
                 String[] sample_vec = strLine.split(",");
                 if(sample_vec.length != 23)
                 {
                     System.out.printf("invalid record format on line %d\n",line);
                     System.exit(-1);
                 }
                 String[] feature_vec = new String[22];
                 for(int i = 1 ; i < 23 ; i++)
                     feature_vec[i-1] = sample_vec[i];
                 if(!sample_vec[0].equals("p") && !sample_vec[0].equals("e"))
                 {
                     System.out.printf("invalid class %s on line %d\r\n", sample_vec[0],line);
                     System.exit(-1);
                 }
                 boolean bLabel = (sample_vec[0].equals("p")) ? true : false;
                 tree.AddSample(feature_vec, bLabel, false);
                 line++;
            }
            br.close();
            double error = tree.GetErrorOnTestData();
            System.out.printf("%f percent errors on test data\r\n",error);
            
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    public static void testTennis()
    {
        DecisionTree tree = new DecisionTree();
        
        tree.CreateDataHeader(new String[]{"outlook", "temperature", "humidity", "wind"});
        
     /* d1 */   tree.AddSample(new String[]{"sunny", "hot", "high", "FALSE"}, false, true);
     /* d2 */   tree.AddSample(new String[]{"sunny", "hot", "high", "TRUE"}, false, true);
     /* d3 */   tree.AddSample(new String[]{"overcast", "hot", "high", "FALSE"},true, true);
     /* d4 */   tree.AddSample(new String[]{"rainy", "mild", "high", "FALSE" },true, true);
     /* d5 */   tree.AddSample(new String[]{"rainy", "cool", "normal", "FALSE"},true, true);    
     /* d6 */   tree.AddSample(new String[]{"rainy", "cool", "normal", "TRUE" },false, true);
     /* d7 */   tree.AddSample(new String[]{"overcast", "cool", "normal", "TRUE"},true, true);
     /* d8 */   tree.AddSample(new String[]{"sunny", "mild", "high", "FALSE"},false, true);
     /* d9 */   tree.AddSample(new String[]{"sunny", "cool", "normal", "FALSE"}, true, true);  
     /* d10 */  tree.AddSample(new String[]{"rainy", "mild", "normal", "FALSE"},true, true);
     /* d11 */  tree.AddSample(new String[]{"sunny", "mild", "normal", "TRUE"}, true, true);  
     /* d12 */  tree.AddSample(new String[]{"overcast", "mild", "high", "TRUE"}, true, true);
     /* d13 */  tree.AddSample(new String[]{"overcast", "hot", "normal", "FALSE"},true, true);    
     /* d14 */  tree.AddSample(new String[]{"rainy", "mild", "high", "TRUE"},false, true);
        
        tree.AddSample(new String[]{"overcast", "mild", "high", "TRUE"}, false, false);
        tree.AddSample(new String[]{"overcast", "hot", "normal", "FALSE"},false, false);    
        tree.MakeTree();
        tree.PrintTree();
        
        double error = tree.GetErrorOnTestData();
        System.out.print(error);
    }
}
