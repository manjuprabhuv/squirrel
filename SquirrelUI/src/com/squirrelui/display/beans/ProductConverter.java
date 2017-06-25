package com.squirrelui.display.beans;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "product")
public class ProductConverter implements Converter {

	public Object getAsObject(FacesContext facesContext, UIComponent component,
			String submittedValue) {
		if (submittedValue.trim().equals("")) {
			return null;
		} else {

			String values[] = submittedValue.split("_");
			ProductsBean p = new ProductsBean();
			p.setBrand(values[0]);
			p.setProductKey(values[0]+"_"+values[1]);
			p.setProdName(values[2]);
			p.setProdCode(values[1]);

			return p;

		}

		// return null;
	}

	public String getAsString(FacesContext facesContext, UIComponent component,
			Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			ProductsBean bean = ((ProductsBean) value);
			String key = bean.getProductKey();
			String name = bean.getProdName();
			return key + "_" + name;
		}
	}
}
