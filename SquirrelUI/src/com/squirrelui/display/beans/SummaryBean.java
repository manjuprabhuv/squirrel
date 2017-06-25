package com.squirrelui.display.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class SummaryBean {
	
	private String SummaryColNm;
	private String SummaryColVal;
	
	
	
	public String getSummaryColNm() {
		return SummaryColNm;
	}
	public void setSummaryColNm(String summaryColNm) {
		SummaryColNm = summaryColNm;
	}
	public String getSummaryColVal() {
		return SummaryColVal;
	}
	public void setSummaryColVal(String summaryColVal) {
		SummaryColVal = summaryColVal;
	}
	
	public SummaryBean(String ColNm, String ColVal)
	{
		setSummaryColNm(ColNm);
		setSummaryColVal(ColVal);
	}

}
