package com.squirrel.crawler;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FlipKartInfoCrawler {

	public static void main(String[] args) throws IOException {
		Document doc = null;
		doc = Jsoup.parse(new File("samsung1.html"), "utf-8");
		String ProductCount = doc.select("span[class=items]").text();
		int pageCount = 0;
		try {
			int pCount = Integer.parseInt(ProductCount);
			pageCount = pCount / 20;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Elements elements = doc.select("div[data-tooltip-class=mobile]");
		int cnt=1;
		for (Element element : elements) {
			Elements link = element.select("a[class=fk-anchor-link]");
			String productHref = link.attr("href");
			String productName = link.text();
			String productRating = element.select("div[class=fk-stars-small]").attr("title");
			String productPrice = element.select(
					"span[class=price final-price]").text();
			if(productPrice.length()==0){
				productPrice = element.select(
						"span[class=fkcb-price price final-price]").text();
			}
			Elements priceElms = element.select("span[class=cb-prc]");
			String productCashBack = (priceElms != null && priceElms.size() > 0) ? priceElms.text() : "";
			System.out.println("productHref >>"+productHref);
			System.out.println("productName >>"+productName);
			System.out.println("productRating >>"+productRating);
			System.out.println("productPrice >>"+productPrice);
			System.out.println("productCashBack >>"+productCashBack);
			System.out.println("**************************"+cnt);
			cnt++;
		}
	}

}
