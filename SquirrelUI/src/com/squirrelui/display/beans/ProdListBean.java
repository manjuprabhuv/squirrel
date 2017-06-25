package com.squirrelui.display.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.squirrel.ui.lucene.SquirrelLuceneUtils;

@ManagedBean
@ViewScoped
public class ProdListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7421735408155508880L;

	private String brand;
	private String prodcode;
	private List<ProductsBean> prods;
	private List<SummaryBean> SummaryLst;
	private ProductsBean selectedProd;
	SquirrelLuceneUtils utils = new SquirrelLuceneUtils();
	private String luceneidx;
	Properties properties = new Properties();

	public List<ProductsBean> getProds() {
		return prods;
	}

	public ProdListBean() throws IOException {

		InputStream input = FacesContext.getCurrentInstance()
				.getExternalContext()
				.getResourceAsStream("properties/squirrel.properties");

		try {
			properties.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!getRequestParameter("brand").equals(brand)) {
			prods = new ArrayList<ProductsBean>();
			prods = (utils.getProductsByManufacturer(getBrand().toLowerCase(), 100,
					getLuceneidx()));
			System.out.println(prods.size());
			for (ProductsBean values : prods)
			{
				setProdcode(values.getProdCode().toLowerCase());
				setProdcode(values.getProductKey());
				setSummaryLst(values.getSummary());
			}
		}

	}

	public String getLuceneidx() {
		setLuceneidx(properties.getProperty("luceneidx"));
		return luceneidx;
	}

	public void setLuceneidx(String luceneidx) {
		this.luceneidx = luceneidx;
	}

	public ProductsBean getSelectedProd() {
		return selectedProd;
	}

	public void setSelectedProd(ProductsBean selectedProd) {
		this.selectedProd = selectedProd;
	}

	public static String getRequestParameter(String name) {
		return (String) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get(name);
	}

	public String getBrand() {
		setBrand(getRequestParameter("brand"));
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getProdcode() {
		return prodcode;
	}

	public void setProdcode(String prodcode) {
		this.prodcode = prodcode;
	}

	public List<SummaryBean> getSummaryLst() {
		return SummaryLst;
	}

	public void setSummaryLst(List<SummaryBean> summaryLst) {
		SummaryLst = summaryLst;
	}
	

}
