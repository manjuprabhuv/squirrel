package com.squirrel.crawler;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class InfibeamInfoCrawler {

	public static void main(String[] args) throws IOException {
		Document doc = null;
		doc = Jsoup.parse(new File("samsung1.html"), "utf-8");
		Elements ProductCount = doc.select("ul[class=srch_result default]");
		//System.out.println(ProductCount.toString());
		Elements lines = ProductCount.select("li");
		//System.out.println(lines.toString());
		for (Element element : lines)
		{
			Elements link = element.select("a");
			String productHref = link.attr("href");
			String productName = element.select("span[class=title]").text();
			String productPrice = element.select(
					"span[class=normal]").text();
			String MarketPrice = element.select(
					"span[class=scratch]").text();
			System.out.println("----------------------------------------");
			System.out.println("Product Name - "+productName);
			System.out.println("Product Link - www.infibeam.com"+productHref);
			System.out.println("Market Price - "+ MarketPrice);
			System.out.println("Infibeam Price - "+ productPrice);
			System.out.println("----------------------------------------");
		}
		
	}

}
