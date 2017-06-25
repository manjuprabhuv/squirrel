package com.squirrel.dataprovider.infibeam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.squirrel.commons.DataProviderConstants;
import com.squirrel.masterdata.gsmarena.GSMArenaCrawler;
import com.squirrel.utils.SquirreCachedlList;
import com.squirrel.utils.SquirrelCacheUtils;
import com.squirrel.utils.SquirrelThreadFactory;
import com.squirrel.utils.SquirrelUtils;

public class InfibeamInfoCrawler implements Runnable {

	public InfibeamInfoCrawler(String url, String manufacturer,
			SquirreCachedlList phoneData) {
		this.phoneData = phoneData;
		this.url = url;
		this.manufacturer = manufacturer;
	}

	public InfibeamInfoCrawler() {

	}

	private static int pageCount(String input) {

		// mTitlePattern = Pattern.compile("<title>([^<]*)</title>");
		StringBuffer sBuffer = new StringBuffer();
		Pattern p = Pattern.compile("of ([^<]*) ");
		Matcher m = p.matcher(input);
		int count = 0;
		while (m.find()) {
			String str = m.group(1);
			if (str.contains("-"))
				continue;
			else
				count = Integer.parseInt(str);
		}
		return count;

	}

	public static final String infibeamMobileURL = "http://www.infibeam.com/Mobiles/";
	public static final String infibeamURL = "http://www.infibeam.com";
	public String url = "";
	public String manufacturer = "";
	public static List<String> failedURL = new ArrayList<String>();
	SquirreCachedlList phoneData;

	public void crawl() throws IOException {
		SquirrelCacheUtils.clearCache();
		Document doc = null;
		doc = Jsoup.connect(infibeamMobileURL)
				.userAgent(DataProviderConstants.DATA_USER_AGENT).timeout(5000)
				.get();

		Elements elms = doc.select("div[id=make]");
		Elements links = elms.select("a[href]");
		SquirreCachedlList phoneData = new SquirreCachedlList();
		SquirrelThreadFactory threadFactory = new SquirrelThreadFactory(
				"SquirrelThead");
		ExecutorService es = Executors.newFixedThreadPool(10, threadFactory);
		for (Element element : links) {
			String manufactureUrl = element.attr("href");
			String manufacturer = element.attr("title").toLowerCase();

			es.execute(new InfibeamInfoCrawler(manufactureUrl, manufacturer,
					phoneData));
			
		}

		es.shutdown();
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		phoneData.finalizeCache("infibeam");
		System.out.println(failedURL.size());
	}

	// http://www.flipkart.com/mobiles/pr?sid=tyy%2C4io&layout=grid&start=21&ajax=true
	// http://www.flipkart.com/mobiles/pr?sid=tyy%2C4io
	// http://www.flipkart.com/cameras/pr?sid=jek%2Cp31&layout=grid&start=21&ajax=true
	public void run() {
		Document doc = null;
		try {
			doc = Jsoup.connect("http://www.infibeam.com"+url)
					.userAgent(DataProviderConstants.DATA_USER_AGENT)
					.timeout(3000).get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			failedURL.add(url);
		}
		String resultsSummary = doc.select("div[class=resultsSummary]").text();
		int prodcount = pageCount(resultsSummary);
		for (int i = 0; i < prodcount; i = i + 20) {
			Elements ProductCount = doc.select("ul[class=srch_result default]");
			// System.out.println(ProductCount.toString());
			Elements lines = ProductCount.select("li");
			// System.out.println(lines.toString());
			for (Element element : lines) {
				String data = populateInfo(element);
				System.out.println(data);
				try {
					phoneData.add("infibeam", data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		// return dataList;
	}

	private String populateInfo(Element element) {
		String returnString;
		Elements link = element.select("a");
		String productHref = link.attr("href");
		String productName = element.select("span[class=title]").text();
		String productCode = SquirrelUtils.getProductCode(productName);
		String productPrice = element.select("span[class=normal]").text();
		String MarketPrice = element.select("span[class=scratch]").text();
		returnString = "product_name::" + stripColorInfo(productName) + "|"
				+"manufacturer::" + manufacturer + "|"
				+ "product_code::" + stripColorInfo(productCode) + "|"
				+ "product_href::" + productHref + "|" + "price::"
				+ cleanNumberInfo(productPrice) + "|" + "MarketPrice::"
				+ cleanNumberInfo(MarketPrice);
		return returnString;
	}

	private String cleanNumberInfo(String rating) {
		StringBuffer sBuffer = new StringBuffer();
		Pattern p = Pattern.compile("[0-9]+.[0-9]*|[0-9]*.[0-9]+|[0-9]+");
		Matcher m = p.matcher(rating);
		while (m.find()) {
			sBuffer.append(m.group());
		}
		String returnstr = sBuffer.toString();
		return returnstr.replaceAll(" ", "");
	}

	private String stripColorInfo(String productName) {
		if (productName.contains("(") && productName.contains(")")) {
			int index = productName.lastIndexOf("(");
			productName = productName.substring(0, index);
			productName = productName.trim();
			return productName;
		} else {
			return productName;
		}
	}

	/*
	 * public static void main(String[] args) { FlipKartInfoCrawler c = new
	 * FlipKartInfoCrawler(); //c.stripColorInfo("asdasd ( asdad)");
	 * System.out.println(c.stripColorInfo("asdasd (asdasd)")); }
	 */

}
