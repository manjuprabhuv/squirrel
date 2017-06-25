package com.squirrelui.display.beans;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;

@ManagedBean
@ApplicationScoped
public class EMarketBean {

	private List<String> images;
	private MenuModel leftmenu;
	private List<SelectItem> brands = new ArrayList<SelectItem>();

	public EMarketBean() {
		InputStream input = FacesContext.getCurrentInstance()
				.getExternalContext()
				.getResourceAsStream("properties/squirrel.properties");
		Properties properties = new Properties();
		try {
			properties.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] imgList = properties.getProperty("espots").split(",");
		String[] brandList = properties.getProperty("brands").split(",");

		images = new ArrayList<String>();

		for (int i = 0; i < imgList.length; i++) {
			images.add(imgList[i].toLowerCase());
		}

		SelectItem item;

		for (int i = 0; i < brandList.length; i++) {
			item = new SelectItem(brandList[i], brandList[i]);
			brands.add(item);
		}

		leftmenu = new DefaultMenuModel();
		Submenu submenu = new Submenu();
		submenu.setLabel("Mobiles");
		MenuItem menuitem;

		for (int i = 0; i < brandList.length; i++) {
			menuitem = new MenuItem();
			menuitem.setValue(brandList[i]);
			menuitem.setUrl("ProdList.jsf?brand=" + brandList[i].toLowerCase());
			submenu.getChildren().add(menuitem);
			leftmenu.addSubmenu(submenu);
		}

	}

	public MenuModel getLeftmenu() {
		return leftmenu;
	}

	public List<SelectItem> getBrands() {
		return brands;
	}

	public List<String> getImages() {
		return images;
	}
}