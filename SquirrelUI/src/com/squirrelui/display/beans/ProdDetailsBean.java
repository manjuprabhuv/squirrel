package com.squirrelui.display.beans;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.squirrel.hadoop.tasks.HadoopSquirrelClient;
import com.squirrel.ui.lucene.SquirrelLuceneUtils;
import com.squirrelui.backing.beans.FeaturesBean;
import com.squirrelui.backing.beans.ReviewsBean;
import com.squirrelui.backing.beans.VendorsBean;

@ManagedBean
@ViewScoped
public class ProdDetailsBean {

	private List<FeaturesBean> features;
	private Integer rating;
	private String price;
	private List<VendorsBean> vendors;
	private List<ReviewsBean> reviews;
	private List<ProductsBean> prods;
	SquirrelLuceneUtils utils = new SquirrelLuceneUtils();
	private String luceneidx;
	Properties properties = new Properties();
	private String vendorurl;
	
	
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}



	

	public String getLuceneidx() {
		setLuceneidx(properties.getProperty("luceneidx"));
		return luceneidx;
	}

	public void setLuceneidx(String luceneidx) {
		this.luceneidx = luceneidx;
	}


	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}



    public static String getRequestParameter(String name) {
        return (String) FacesContext.getCurrentInstance().getExternalContext()
            .getRequestParameterMap().get(name);
    }
	
	//@PostConstruct
	public ProdDetailsBean() throws IOException {
		
		InputStream input = FacesContext.getCurrentInstance()
				.getExternalContext()
				.getResourceAsStream("properties/squirrel.properties");

		try {
			properties.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		System.out.println(getRequestParameter("prdnm"));
		HadoopSquirrelClient details = new HadoopSquirrelClient();

					setFeatures(details.getfeatures(getRequestParameter("prdkey")));
					setVendors(details.getVendors(getRequestParameter("prdkey")));
					setReviews(details.getReviews(getRequestParameter("prdkey")));
					
					prods = new ArrayList<ProductsBean>();
					prods = (utils.searchProductName(getRequestParameter("prdnm").toLowerCase(), 1,
							getLuceneidx()));
					System.out.println(prods.size());
					for (ProductsBean values : prods)
					{
						setRating(values.getAvgRating());
						setPrice(values.getAvgPrice());
					}

	}

	public List<FeaturesBean> getFeatures() {
		return features;
	}

	public void setFeatures(List<FeaturesBean> features) {
		this.features = features;
	}
	
	

	public List<VendorsBean> getVendors() {
		return vendors;
	}

	public void setVendors(List<VendorsBean> vendors) {
		this.vendors = vendors;
	}

	public List<ReviewsBean> getReviews() {
		return reviews;
	}

	public void setReviews(List<ReviewsBean> reviews) {
		this.reviews = reviews;
	}
	
	
	public void gotourl() throws IOException
	{
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		externalContext.redirect(vendorurl);
	}

	public String getVendorurl() {
		return vendorurl;
	}

	public void setVendorurl(String vendorurl) {
		this.vendorurl = vendorurl;
	}


}