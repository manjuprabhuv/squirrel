package com.squirrel.hadoop.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HTable;

import com.squirrelui.backing.beans.FeaturesBean;
import com.squirrelui.backing.beans.ReviewsBean;
import com.squirrelui.backing.beans.VendorsBean;


public class HadoopSquirrelClient {
	
	Properties properties = new Properties();

	
	
	//Sample Call
	//public static void main(String[] args) throws IOException {
		//getRowExample();
	//}
	
	public HadoopSquirrelClient() {
		InputStream input = FacesContext.getCurrentInstance()
				.getExternalContext()
				.getResourceAsStream("properties/squirrel.properties");

		try {
			properties.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public List<FeaturesBean> getfeatures(String prodname) throws IOException
	{
		List<FeaturesBean> featureList = new ArrayList<FeaturesBean>();
		HTable table = remoteHbaseTable(properties.getProperty("HBaseMainTable"));
		String[] colFamilyNames = {properties.getProperty("HBaseMainTable.features")};
		HashMap<String, Object> rows = HbaseUtils.getRow(table, prodname, colFamilyNames);
		for (String rowkey : rows.keySet()) {
			Object value = rows.get(rowkey);
			System.out.println("Key :: "+rowkey);
			if(value instanceof String){
				System.out.println("Key :: "+rowkey+" ====== Value ::"+value);
			}else{
				HashMap<String, Object> row = (HashMap<String, Object>)rows.get(rowkey);
				for (String colNames : row.keySet()) {
					String colvalue =(String)row.get(colNames);
					FeaturesBean featurebean = new FeaturesBean(colNames,colvalue);
					featureList.add(featurebean);
					System.out.println("colNames :: "+colNames+" ====== colvalue ::"+colvalue);
				}
				
			}
			
		
		}
		return featureList;
	}
	
	
	public List<ReviewsBean> getReviews(String prodname) throws IOException
	{
		List<ReviewsBean> reviewList = new ArrayList<ReviewsBean>();
		HTable table = remoteHbaseTable(properties.getProperty("HBaseMainTable"));
		String[] colFamilyNames = {properties.getProperty("HBaseMainTable.reviews")};
		HashMap<String, Object> rows = HbaseUtils.getRow(table, prodname, colFamilyNames);
		for (String rowkey : rows.keySet()) {
			Object value = rows.get(rowkey);
			System.out.println("Key :: "+rowkey);
			if(value instanceof String){
				System.out.println("Key :: "+rowkey+" ====== Value ::"+value);
			}else{
				HashMap<String, Object> row = (HashMap<String, Object>)rows.get(rowkey);
				for (String colNames : row.keySet()) {
					String colvalue =(String)row.get(colNames);
					if(colNames.equalsIgnoreCase("reviewVideo"))
					{
					ReviewsBean reviewbean = new ReviewsBean(colvalue);
					reviewList.add(reviewbean);
					}
					System.out.println("colNames :: "+colNames+" ====== colvalue ::"+colvalue);
				}
				
			}
			
		
		}
		return reviewList;
	}
	
	
	public List<VendorsBean> getVendors(String prodname) throws IOException
	{
		List<VendorsBean> vendorsList = new ArrayList<VendorsBean>();
		VendorsBean vendorbean;
		String price="",shipping="",buylink="",vendor="";
		HTable table = remoteHbaseTable(properties.getProperty("HBaseMainTable"));
		String[] colFamilyNames = {"flipkart","infibeam"};
		HashMap<String, Object> rows = HbaseUtils.getRow(table, prodname, colFamilyNames);
		for (String rowkey : rows.keySet()) {
			Object value = rows.get(rowkey);
			System.out.println("Key :: "+rowkey);
			//vendorbean = new VendorsBean("Vendor",rowkey);
			//vendorsList.add(vendorbean);
			if(value instanceof String){
				System.out.println("Key :: "+rowkey+" ====== Value ::"+value);
			}else{
				HashMap<String, Object> row = (HashMap<String, Object>)rows.get(rowkey);
				if(!row.keySet().isEmpty())
				{
					price="Not Available"; shipping="No Shipping info Available"; buylink=""; vendor="";
				for (String colNames : row.keySet()) {
					String colvalue =(String)row.get(colNames);
					vendor=rowkey;
					if(colNames.equalsIgnoreCase("price"))
						price=colvalue;
					else if(colNames.equalsIgnoreCase("shipping"))
						shipping=colvalue;
					else if(colNames.equalsIgnoreCase("product_href"))
						buylink=colvalue;


					System.out.println("colNames :: "+colNames+" ====== colvalue ::"+colvalue);
				}
				vendorbean = new VendorsBean(vendor,price,shipping,buylink);
				vendorsList.add(vendorbean);
				}

				
			}
			
		
		}
		return vendorsList;
	}

	public static HTable remoteHbaseTable(String table) throws IOException{
		Configuration config = HBaseConfiguration.create();
		System.out.println("***************************************");
		String hbaseZookeeperQuorum="192.168.56.101";
		String hbaseZookeeperClientPort="2222";
		config.set(HConstants.ZOOKEEPER_QUORUM ,hbaseZookeeperQuorum);
		config.set(HConstants.ZOOKEEPER_CLIENT_PORT, hbaseZookeeperClientPort);

		HTable hTable = new HTable(config,table);
		return hTable;
	}
	

}
