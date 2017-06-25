package com.squirrelui.display.beans;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;

import com.squirrel.ui.lucene.SquirrelLuceneUtils;

@ManagedBean(name = "productAutoCompleteBean")
@RequestScoped
public class ProductAutoCompleteBean {

	private ProductsBean selectedProduct;
	private static String searchquery;
	private String luceneidx;
	Properties properties = new Properties();

	public ProductAutoCompleteBean() {
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

	public String getLuceneidx() {
		setLuceneidx(properties.getProperty("luceneidx"));
		return luceneidx;
	}

	public void setLuceneidx(String luceneidx) {
		this.luceneidx = luceneidx;
	}

	public ProductsBean getSelectedProduct() {
		return selectedProduct;
	}

	public void setSelectedProduct(ProductsBean selectedProduct) {
		this.selectedProduct = selectedProduct;
	}

	public List<ProductsBean> completeProduct(String query) {
		// List<ProductsBean> suggestions = new ArrayList<ProductsBean>();
		SquirrelLuceneUtils utils = new SquirrelLuceneUtils();
		List<ProductsBean> suggestions = null;
		setSearchquery(query);
		try {
			suggestions = utils.searchProductName(query, 5, getLuceneidx());
			List<ProductsBean> closeMatch = utils.searchProductName(
					query + "*", 5, getLuceneidx());
			if (suggestions != null) {
				suggestions.addAll(closeMatch);
			} else if (closeMatch != null) {
				return closeMatch;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return suggestions;
	}

	public String getSearchquery() {
		return searchquery;
	}

	public void setSearchquery(String searchquery) {
		ProductAutoCompleteBean.searchquery = "";
		ProductAutoCompleteBean.searchquery = searchquery;
	}

	public void handleSelect(SelectEvent event) throws IOException {

		ProductsBean p = (ProductsBean) event.getObject();
		FacesContext.getCurrentInstance().getExternalContext()
				.redirect("ProdDetails.jsf?brand=" + p.getBrand()+ "&prdkey=" + p.getProductKey()+ "&prdnm=" + p.getProdName()+"&prdcode=" +p.getProdCode());
		return;
	}

	public void searchdata() throws IOException {
		System.out.println(getSearchquery());
		FacesContext.getCurrentInstance().getExternalContext()
				.redirect("SearchResult.jsf?srchquery=" + getSearchquery());
	}

	/*
	 * public void handleUnselect(UnselectEvent event) { FacesMessage message =
	 * new FacesMessage(FacesMessage.SEVERITY_INFO, "Unselected:" +
	 * event.getObject().toString(), null);
	 * 
	 * FacesContext.getCurrentInstance().addMessage(null, message); }
	 */

}