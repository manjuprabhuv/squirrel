package com.squirrelui.backing.beans;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;


@ManagedBean
@ApplicationScoped
public class FeaturesBean {
	
	private String FeatureName;
	private String FeatureVal;

	
	
    public FeaturesBean(String FeatureName, String FeatureVal) {
        this.setFeatureName(FeatureName);
        this.setFeatureVal(FeatureVal);

}



	public String getFeatureName() {
		return FeatureName;
	}



	public void setFeatureName(String featureName) {
		FeatureName = featureName;
	}



	public String getFeatureVal() {
		return FeatureVal;
	}



	public void setFeatureVal(String featureVal) {
		FeatureVal = featureVal;
	}

}
