package com.squirrelui.backing.beans;

import java.util.Map;

import javax.annotation.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped

public class ProdBackingBean {

	public void editAction() {
		 
		  Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
	                
		  String action = params.get("action");
	          //...
	 
		}
	 
	
}
