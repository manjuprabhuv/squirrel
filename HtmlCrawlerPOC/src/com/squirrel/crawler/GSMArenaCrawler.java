package com.squirrel.crawler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GSMArenaCrawler {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Properties props = new Properties();
		props.load(new FileInputStream("gsmarena.properties"));
		Enumeration en = props.propertyNames();
		do {
			String key = (String) en.nextElement();
			String values = props.getProperty(key);
			if (values.length() > 0) {

				String valueArray[] = values.split(",");
				String baseURL = "";
				if (valueArray.length == 3 && valueArray[0].length() > 0
						&& valueArray[1].length() > 0
						&& valueArray[2].length() > 0) {
					baseURL = valueArray[0];
					int pageCount = CrawlPage(key + "_", baseURL,
							valueArray[1], valueArray[2], true);
					for (int i = 2; i <= pageCount; i++) {
						// motorola-phones-f-4-0-p2.php
						String newBaseURL = baseURL + "-f-" + valueArray[1]
								+ "-0-p" + i + ".php";
						System.out.println(newBaseURL);
						CrawlPage(key + "_", newBaseURL, valueArray[1],
								valueArray[2], false);
					}
				}
			}
		} while (en.hasMoreElements());

	}

	private static int CrawlPage(String uniqeURL, String baseURL,
			String gsmMCode, String PcLeastDigit, boolean firstPass)
			throws IOException {
		// http://www.gsmarena.com/nokia-phones-f-1-0-p2.php
		// http://www.gsmarena.com/nokia-phones-1.php
		String url_s = "";
		if (firstPass) {
			url_s = "http://www.gsmarena.com/" + baseURL + "-" + gsmMCode
					+ ".php";
		} else {
			url_s = "http://www.gsmarena.com/" + baseURL;
		}
		int pageCount = 0;
		Document doc = null;
	//	try {
			//doc = Jsoup.connect(url_s).userAgent("Mozilla").timeout(3000).get();
		//} catch (IOException e) {
			System.out.println("*****************" + url_s
					+ "******************");
		//	throw e;
		//}
		System.out.println();
		doc = Jsoup.parse(new File("samsung1.html"), "utf-8");
		Elements links = doc.select("a[href]");
		int pclen = 0;
		try {
			pclen = Integer.parseInt(PcLeastDigit);
		} catch (Exception e) {
			pclen = 3;
		}

		for (Element link : links) {
			String url = link.attr("href");// use abs:href
			String imgsrc = "";

			if (firstPass && url.contains(baseURL + "-f-" + gsmMCode + "-0-")) {
				pageCount++;
			}
			if (url.contains(uniqeURL) && !url.contains("adclick.php")) { // use
																			// /sumsung_
				Elements chldElements = link.children();
				Element childElement = chldElements.first();
				if (childElement != null
						&& "img".equalsIgnoreCase(childElement.tagName())) {
					imgsrc = childElement.attr("abs:src");
				}
				System.out.println("URL : " + url + "\t NAME : " + link.text()
						+ "\t IMG : " + imgsrc + "\t Product Code :"
						+ getProductCode(link.text(), pclen));

			}
		}
		System.out
				.println("************************************************************");
		/*
		 * for (String str : list) { System.out.println(str); }
		 */
		return pageCount;
	}

	// static List<String> list = new ArrayList<String>();

	private static String getProductCode(String str, int length) {
		String strArray[] = str.split(" ");
		String productCode = "";
		if (length != -1) {
			for (int i = 0; i < strArray.length; i++) {
				String digits = strArray[i].replaceAll("[^0-9.]", "");
				if (digits != null && !digits.contains(".")
						&& digits.trim().length() >= length) {
					productCode = strArray[i];
					productCode = productCode.toUpperCase();
					System.out.println(productCode);
					break;
				}
			}
		}
		if (productCode.length() == 0) {
			productCode = str;
			// list.add(str);
		}
		return productCode;
	}

}
