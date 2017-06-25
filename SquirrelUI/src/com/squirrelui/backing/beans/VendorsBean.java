package com.squirrelui.backing.beans;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;


@ManagedBean
@ApplicationScoped
public class VendorsBean {
	
	private String ColName;
	private String ColVal;
	private String Vendor;
	private String Price;
	private String Shipping;
	private String BuyLink;

	
	
    public String getVendor() {
		return Vendor;
	}



	public void setVendor(String vendor) {
		Vendor = vendor;
	}



	public String getPrice() {
		return Price;
	}



	public void setPrice(String price) {
		Price = price;
	}



	public String getShipping() {
		return Shipping;
	}



	public void setShipping(String shipping) {
		Shipping = shipping;
	}



	public String getBuyLink() {
		return BuyLink;
	}



	public void setBuyLink(String buyLink) {
		BuyLink = buyLink;
	}



	public VendorsBean(String vendor, String price, String shipping, String buylink) {

			this.setVendor(vendor);

			this.setPrice(price);

			this.setShipping(shipping);

			if (vendor.equalsIgnoreCase("flipkart"))
				this.setBuyLink("http://www.flipkart.com"+buylink);
			else if (vendor.equalsIgnoreCase("infibeam"))
				this.setBuyLink("http://www.infibeam.com"+buylink);
			

}



	public String getColName() {
		return ColName;
	}



	public void setColName(String colName) {
		ColName = colName;
	}



	public String getColVal() {
		return ColVal;
	}



	public void setColVal(String colVal) {
		ColVal = colVal;
	}



}
