package com.squirrelui.backing.beans;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;


@ManagedBean
@ApplicationScoped
public class ReviewsBean {
	
	private String ReviewVideo;

	
	

	public ReviewsBean(String reviewVideo) {

			this.setReviewVideo(reviewVideo);
		

}



	public String getReviewVideo() {
		return ReviewVideo;
	}



	public void setReviewVideo(String reviewVideo) {
		ReviewVideo = reviewVideo;
	}

}
