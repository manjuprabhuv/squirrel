package com.squirrel.dataprovider.mobilestore;

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

public class MobileStoreInfoCrawler implements Runnable {

	public MobileStoreInfoCrawler(String url, String manufacturer,
			SquirreCachedlList phoneData) {
		this.phoneData = phoneData;
		this.url = url;
		this.manufacturer = manufacturer;
	}

	public MobileStoreInfoCrawler() {

	}

	private static int productCount(Document doc) {
		String summary = doc.select("div[class=search-summary]").get(0).text();
		Pattern p = Pattern.compile("Showing ([^<]*) Results");
		Matcher m = p.matcher(summary);
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

	public static final String mobileStoreMobileURL = "http://www.themobilestore.in/home-mobiles-&-tablet-mobiles";
	public static final String mobileStoreBaseURL = "http://www.themobilestore.in";
	public String url = "";
	public String manufacturer = "";
	public static List<String> failedURL = new ArrayList<String>();
	SquirreCachedlList phoneData;

	public void crawl() throws IOException {
		SquirrelCacheUtils.clearCache();
		Document doc = null;
		doc = Jsoup.connect(mobileStoreMobileURL)
				.userAgent(DataProviderConstants.DATA_USER_AGENT).timeout(5000)
				.get();

		Elements links = doc.select("div[id=facet-make]").select("li");
		SquirreCachedlList phoneData = new SquirreCachedlList();
		SquirrelThreadFactory threadFactory = new SquirrelThreadFactory(
				"SquirrelThead");
		ExecutorService es = Executors.newFixedThreadPool(10, threadFactory);
		for (Element element : links) {
			String manufacturer = element.attr("data-value");
			String manufactureUrl = element.select("a").attr("href");
			es.execute(new MobileStoreInfoCrawler(manufactureUrl, manufacturer,
					phoneData));

		}

		es.shutdown();
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		phoneData.finalizeCache("mobilestore");
		System.out.println(failedURL.size());
	}

	public static void main(String[] args) {
		String summary = "Showing 112 Results  in";
		
		
		StringBuffer sBuffer = new StringBuffer();
		
		//int pageCount = Integer.parseInt(digits);
	}

	// http://www.flipkart.com/mobiles/pr?sid=tyy%2C4io&layout=grid&start=21&ajax=true
	// http://www.flipkart.com/mobiles/pr?sid=tyy%2C4io
	// http://www.flipkart.com/cameras/pr?sid=jek%2Cp31&layout=grid&start=21&ajax=true
	public void run() {
		Document doc = null;
		try {
			doc = Jsoup.connect(mobileStoreBaseURL + url)
					.userAgent(DataProviderConstants.DATA_USER_AGENT)
					.timeout(3000).get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			failedURL.add(url);
		}

		double productCount = productCount(doc);
		int pageCount = (int) Math.ceil(productCount / 16);
		for (int i = 0; i <= pageCount; i++) {
			Elements ProductCount = doc.select("li[class=clearfix]");
			// System.out.println(ProductCount.toString());

			// System.out.println(lines.toString());
			for (Element element : ProductCount) {
				String data = populateInfo(element);
				System.out.println(data);
				try {
					phoneData.add("mobilestore", data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// return dataList;
	}

	private String populateInfo(Element results) {

		String productHref = "";
		String productName = "";
		String productCode = "";
		Elements titles = results.select("div[class=variant-image]");
		productHref = titles.select("a").attr("href");
		productName = titles.select("img").attr("title");
		if (productName.contains(",")) {
			productName = productName
					.substring(0, productName.indexOf(",") - 1);
		}
		productCode = SquirrelUtils.getProductCode(productName);
		String price = results.select("span[class=variant-final-price]").text();

		String returnString;
		returnString = "product_name::" + productName + "|"
				+ "manufacturer::" + manufacturer + "|" + "product_code::"
				+ productCode + "|" + "product_href::"
				+ productHref + "|" + "price::" + cleanNumberInfo(price);
		return returnString;
	}
	//R 28,300
	private String cleanNumberInfo(String price) {		
		String returnstr = price.replaceAll("[^0-9.]", "");
		returnstr = returnstr.trim();
		return returnstr.replaceAll(" ", "");
	}

	

}
