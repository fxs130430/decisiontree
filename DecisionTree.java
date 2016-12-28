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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author asus
 */
public class DecisionTree 
{
    private TreeNode                                    m_root;
    private ArrayList<Features_record>                  m_arrDataSet;
    private Queue<TreeNode>                             m_Queue;
    private ArrayList<Features_record>                  m_data_train;
    private ArrayList<Features_record>                  m_data_test;
    private LinkedHashMap<String,ArrayList<String>>     m_mapPossibleValues;
    
    public DecisionTree()
    {
        m_root = new TreeNode(this,null);
        m_arrDataSet = new ArrayList<>();
        m_Queue = new LinkedList<TreeNode>();
        m_mapPossibleValues = new LinkedHashMap<String,ArrayList<String>>();
        m_data_train = new ArrayList<Features_record>();
        m_data_test = new ArrayList<Features_record>();
    }
    public void CreateDataHeader(String[] x_strFeatures)
    {
        for(String s: x_strFeatures)
            m_mapPossibleValues.put(s, new ArrayList<String>());
    }
    public void AddSample(String[] x_strSample,boolean x_bLabel,boolean x_bTrainData)
    {
        if(x_strSample.length != m_mapPossibleValues.size())
        {
            System.out.printf("invalid feature vectore size(given %d,expected %d)!\r\n", x_strSample.length, m_mapPossibleValues.size());
            System.exit(-1);
        }
        
        String[] arrCols = getColumns();
        Features_record rec= new Features_record(arrCols);
        rec.SetLabel(x_bLabel);
        
        Iterator it = m_mapPossibleValues.keySet().iterator();
        for(int i = 0 ; i < x_strSample.length ; i++)
        {
            ArrayList<String> arrPossibleVals = (new ArrayList<ArrayList<String>>(m_mapPossibleValues.values())).get(i);
            String key = (String)it.next();
            if(!arrPossibleVals.contains(x_strSample[i]))
                arrPossibleVals.add(x_strSample[i]);
            rec.SetAttributeValue(key, x_strSample[i]);
        }
        if(x_bTrainData)
            m_data_train.add(rec);
        else 
            m_data_test.add(rec);
        
    }
    public double GetErrorOnTestData()
    {
        double percentage = 0.0;
        for(Features_record rec: m_data_test)
        {
            int nClass = (rec.GetLabel())? 1 : 0;
            String[] arrVals = rec.GetAttributeArray();
            if(Query(arrVals) != nClass)
                percentage += 1;
        }
        return (percentage / m_data_test.size()) * 100;
    }
    public TreeNode GetRoot()
    {
        return m_root;
    }
    private String[] getColumns()
    {
        String[] arrCols = new String[m_mapPossibleValues.size()];
        int i = 0;
        for (Map.Entry<String,ArrayList<String>> entry : m_mapPossibleValues.entrySet())
        {
            arrCols[i] = entry.getKey();
            i++;
        }
        return arrCols;
    }
    public void MakeTree()
    {
        for(Features_record rec: m_data_train)
        {
            if(rec.GetLabel())
                m_root.AddPositiveExample(rec);
            else
                m_root.AddNegativeExamples(rec);
        }
        ArrayList<String> arrFeatures = new ArrayList<String>();
        String[] arrCols = getColumns();
        for(String s: arrCols)
            arrFeatures.add(s);
        m_root.SetExandingCandidates(arrFeatures);
        
        
        m_Queue.add(m_root);
        while(!m_Queue.isEmpty())
        {
            TreeNode node = m_Queue.remove();
            ProcessNode(node);            
        }        
    }
    private void ProcessNode(TreeNode x_node)
    {
        if(x_node.IsLeaf())
        {
            x_node.SetDecisionClass();
            return;
        }
        if(x_node.GetCandidateCount() > 0)
        {
            String strBestFeature = getBestAttribute(x_node);
            ArrayList<TreeNode>arr_children = x_node.InsertChildrenBasedOnAttributeValues(strBestFeature);
            for(TreeNode n: arr_children)
                m_Queue.add(n);
        }
        else // majority vote
        {
            x_node.SetDecisionClass();
        }
    }
    private String getBestAttribute(TreeNode x_Node)
    {
        String[] arrCandidates = x_Node.GetExpandingCandidates();
        String strBestFeature = arrCandidates[0];
        double dBestInfo_gain = x_Node.GetInformationGain(strBestFeature);
        
        for(String s: arrCandidates)
        {
            if(x_Node.GetInformationGain(s) > dBestInfo_gain)
            {
                strBestFeature = s;
                dBestInfo_gain = x_Node.GetInformationGain(s);
            }
        }        
        return strBestFeature;
    }
    public String[] getAttributePossibleValues(String x_strAttrib)
    {
        if(m_mapPossibleValues.containsKey(x_strAttrib))
        {
            ArrayList<String> arr = (ArrayList<String>)m_mapPossibleValues.get(x_strAttrib);
            String[] arrPossibleVals = new String[arr.size()];
            arrPossibleVals = arr.toArray(arrPossibleVals);
            return arrPossibleVals;
        }
        else
        {
            System.out.printf("invalid attribute %s\r\n",x_strAttrib);
            System.exit(-1);
        }
        return null;
    }
    public int Query(String[] x_arrFeatures)
    {
        String[] arrCols = getColumns();
        Features_record rec= new Features_record(arrCols);
        Iterator it = m_mapPossibleValues.keySet().iterator();
        for(int i = 0 ; i < x_arrFeatures.length ; i++)
        {
            String key = (String)it.next();
            rec.SetAttributeValue(key, x_arrFeatures[i]);
        }
        if(m_root == null)
        {
            System.out.println("Corrupted Tree!");
            System.exit(-1);
        }
        TreeNode node = m_root;
        while(node != null)
        {
            //System.out.printf("%s\r\n",node.GetAttribute());
            if(node.IsLeaf())
                return node.getDecisionClass();
            String strAttribute = node.GetAttribute();
            String strVal = rec.GetAttributeValue(strAttribute);
            node = node.getChild(strVal);
        }
        return -1;
    }
    public void PrintTree()
    {
        if(m_root != null)
            m_root.print();
    }
    
}
