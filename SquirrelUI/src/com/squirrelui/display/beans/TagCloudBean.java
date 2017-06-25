package com.squirrelui.display.beans;

import java.awt.List;
import java.util.Random;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.tagcloud.DefaultTagCloudItem;
import org.primefaces.model.tagcloud.DefaultTagCloudModel;
import org.primefaces.model.tagcloud.TagCloudItem;
import org.primefaces.model.tagcloud.TagCloudModel;

@ManagedBean
@ApplicationScoped
public class TagCloudBean {

	private TagCloudModel model;

	public TagCloudBean() {
		String[] tagList = { "Samsung", "HTC", "Apple", "Motorola", "LG",
				"MicroMax", "Sony", "Nokia", "Karbonn", "Blackberry" };
		Random rand = new Random();
		model = new DefaultTagCloudModel();
		for (int i = 0; i < tagList.length; i++) {
			model.addTag(new DefaultTagCloudItem(tagList[i], rand.nextInt(6)));
		}

	}

	public TagCloudModel getModel() {
		return model;
	}

	public void onSelect(SelectEvent event) {
		TagCloudItem item = (TagCloudItem) event.getObject();
		FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Item Selected", item.getLabel());
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
}
