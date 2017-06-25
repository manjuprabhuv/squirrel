package com.squirrelui.backing.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.squirrel.ui.lucene.SquirrelLuceneUtils;
import com.squirrelui.display.beans.ProductsBean;

@ManagedBean
@ViewScoped
public class SearchBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7421735408155508880L;

	private String query;
	private List<ProductsBean> results;
	SquirrelLuceneUtils utils = new SquirrelLuceneUtils();
	Properties properties = new Properties();
	private String luceneidx;

	public SearchBean() throws IOException {
		results = new ArrayList<ProductsBean>();

		InputStream input = FacesContext.getCurrentInstance()
				.getExternalContext()
				.getResourceAsStream("properties/squirrel.properties");

		try {
			properties.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getSearchResults();
	}

	public String getLuceneidx() {
		setLuceneidx(properties.getProperty("luceneidx"));
		return luceneidx;
	}

	public void setLuceneidx(String luceneidx) {
		this.luceneidx = luceneidx;
	}

	public List<ProductsBean> getResults() {
		return results;
	}

	public void setResults(List<ProductsBean> results) {
		this.results = results;
	}

	public List<ProductsBean> getSearchResults() {

		try {
			results = utils.searchProductName(getQuery().toLowerCase(), 100, getLuceneidx());
			if (results.isEmpty()) {
				results = utils.searchProductName(getQuery().toLowerCase() + "*", 100,
						getLuceneidx());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return results;
	}

	public static String getRequestParameter(String name) {
		return (String) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get(name);
	}

	public String getQuery() {
		setQuery(getRequestParameter("srchquery"));
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}
