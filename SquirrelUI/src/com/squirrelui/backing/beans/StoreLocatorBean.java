package com.squirrelui.backing.beans;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import com.squirrel.ui.lucene.SquirrelLuceneUtils;
import com.squirrelui.display.beans.ProductsBean;

@ManagedBean
@RequestScoped
public class StoreLocatorBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7421735408155508880L;

	private String query;
	private List<ProductsBean> results;
	SquirrelLuceneUtils utils = new SquirrelLuceneUtils();
	Properties properties = new Properties();
	private String luceneidx;

	public StoreLocatorBean() throws IOException {
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
		
		simpleModel = new DefaultMapModel();
		
		//Shared coordinates
		LatLng coord1 = new LatLng(12.9736977, 77.7016084);
		LatLng coord2 = new LatLng(12.9259, 77.6229);
		LatLng coord3 = new LatLng(12.9714, 77.6393);
		LatLng coord4 = new LatLng(12.9876, 77.5535);
		LatLng coord5 = new LatLng(12.5656, 77.35);
		LatLng coord6 = new LatLng(12.9200, 77.6100);
		
		//Basic marker
		simpleModel.addOverlay(new Marker(coord1, "Univercell - VV Puram"));
		simpleModel.addOverlay(new Marker(coord2, "Univercell - Koramangala"));
		simpleModel.addOverlay(new Marker(coord3, "Mobile Store - Indiranagar"));
		simpleModel.addOverlay(new Marker(coord4, "Sangeetha - Rajajinagar"));
		simpleModel.addOverlay(new Marker(coord5, "Viveks - Wilson garden"));
		simpleModel.addOverlay(new Marker(coord6, "Girias - BTM layout"));


	}

	private MapModel simpleModel;



	public MapModel getSimpleModel() {
		return simpleModel;
	}
}
