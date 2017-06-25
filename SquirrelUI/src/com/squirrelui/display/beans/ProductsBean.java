package com.squirrelui.display.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class ProductsBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ProdName;
	private String ProdCode;
	private String AvgPrice;
	private String Brand;
	private int AvgRating;
	private String productKey;
	private List<SummaryBean> Summary;
	
	
	
	public List<SummaryBean> getSummary() {
		return Summary;
	}

	public void setSummary(String summary) {
		Summary = new ArrayList<SummaryBean>();
		String[] summaryList = summary.split("::"); 
		for (int i = 0; i < summaryList.length; i++) {
			String[] Columns = summaryList[i].split("==");
			for (int j=0; j< Columns.length;)
			{
			if(Columns.length == 2)
			{
			SummaryBean summarybean = new SummaryBean(Columns[j],Columns[j+1]);
			Summary.add(summarybean);
			j=j+2;
			}
			else
				j=j+1;
			}
		}

	}



	private ProductsBean selectedProd;

	public void setProdName(String prodName) {
		ProdName = prodName;
	}

	public void setProdCode(String prodCode) {
		ProdCode = prodCode;
	}

	public void setAvgPrice(String avgPrice) {
		AvgPrice = avgPrice;
	}

	public void setBrand(String brand) {
		Brand = brand;
	}

	public void setAvgRating(String avgRating) {
		double d = Double.parseDouble(avgRating);
		AvgRating = Integer.valueOf((int)d);
	}

	public ProductsBean(){
		
	}
	
    public String getProductKey() {
		return productKey;
	}


	public void setProductKey(String productKey) {
		this.productKey = productKey;
	}


	public ProductsBean(String ProdName, String ProdCode, String AvgPrice, String Brand, Integer AvgRating) {
        this.ProdName = ProdName;
        this.ProdCode = ProdCode;
        this.AvgPrice = AvgPrice;
        this.Brand = Brand;
        this.AvgRating = AvgRating;
}

	
	public String getProdName() {
		return ProdName;
	}
	
	public String getProdCode() {
		return ProdCode;
	}
		
	public String getAvgPrice() {
		return AvgPrice;
	}
	
	public String getBrand() {
		return Brand;
	}

	public Integer getAvgRating() {
		return AvgRating;
	}
	
	
	public ProductsBean getSelectedProd() {
		return selectedProd;
	}



	public void setSelectedProd(ProductsBean selectedProd) {
		this.selectedProd = selectedProd;
	}
	
	
}
