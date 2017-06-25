package com.squirrel.dataprovider.flipkart;

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

public class FlipKartInfoCrawler implements Runnable {

	public FlipKartInfoCrawler(String url, SquirreCachedlList phoneData) {
		this.phoneData = phoneData;
		this.url = url;
	}

	public FlipKartInfoCrawler() {

	}

	public static final String flipkartURL = "http://www.flipkart.com/mobiles/pr?sid=tyy%2C4io";
	public static final String flipkartappleURL = "http://www.flipkart.com/mobiles/pr?sid=tyy%2C4io&q=apple";
	public static final String imageLoc = "D:\\project\\stage\\flipkart\\images\\";
	public String url = "";
	public static List<String> failedURL = new ArrayList<String>();
	SquirreCachedlList phoneData;

	public static void main(String[] args) {
		String manufactuer = "sleect*";
		
		System.out.println(manufactuer.replaceAll("\\*", ""));
	}

	public void crawl() throws IOException {
		SquirrelCacheUtils.clearCache();
		Document doc = null;
		doc = Jsoup.connect(flipkartURL)
				.userAgent(DataProviderConstants.DATA_USER_AGENT).timeout(5000)
				.get();
		String ProductCount = doc.select("span[class=items]").text();

		int pCount = Integer.parseInt(ProductCount);

		SquirrelThreadFactory threadFactory = new SquirrelThreadFactory(
				"SquirrelThead");
		ExecutorService es = Executors.newFixedThreadPool(10, threadFactory);
		SquirreCachedlList phoneData = new SquirreCachedlList();
		boolean addapple = false;
		for (int i = 1; i < pCount; i = i + 20) {
			if (i == 1) {

				es.execute(new FlipKartInfoCrawler(flipkartURL, phoneData));
			} else {
				es.execute(new FlipKartInfoCrawler(flipkartURL
						+ "&layout=grid&start=" + i, phoneData));
				if(!addapple){
					es.execute(new FlipKartInfoCrawler(flipkartappleURL, phoneData));
					addapple=true;
				}

			}
			
				
			
		}

		es.shutdown();
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		phoneData.finalizeCache("flipkart");
		System.out.println(failedURL.size());
	}

	// http://www.flipkart.com/mobiles/pr?sid=tyy%2C4io&layout=grid&start=21&ajax=true
	// http://www.flipkart.com/mobiles/pr?sid=tyy%2C4io
	// http://www.flipkart.com/cameras/pr?sid=jek%2Cp31&layout=grid&start=21&ajax=true
	public void run() {
		Document doc = null;
		try {
			doc = Jsoup.connect(url)
					.userAgent(DataProviderConstants.DATA_USER_AGENT)
					.timeout(3000).get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(url);
			failedURL.add(url);
			throw new RuntimeException();
		}
		List<String> dataList = new ArrayList<String>();
		Elements elements = doc.select("div[data-tooltip-class=mobile]");

		for (Element element : elements) {
			Elements link = element.select("a[class=fk-anchor-link]");

			// Elements variantElement =
			// element.select("div[class=variants dont-show]");
			/*
			 * String variant = ""; if(variantElement!=null &&
			 * variantElement.select("a[href]")!=null){ variant =
			 * variantElement.select("a[href]").attr("href"); }
			 */
			String productHref = link.attr("href");
			String productName = link.text();
			String productRating = element.select("div[class=fk-stars-small]")
					.attr("title");
			String productPrice = element.select(
					"span[class=price final-price]").text();
			if (productPrice.length() == 0) {
				productPrice = element.select(
						"span[class=fkcb-price price final-price]").text();
			}
			Elements priceElms = element.select("span[class=cb-prc]");
			String productCashBack = (priceElms != null && priceElms.size() > 0) ? priceElms
					.text() : "";
			String ProductCode = SquirrelUtils.getProductCode(productName);
			ProductCode = stripColorInfo(ProductCode);
			String manufacturer = extractManufacturer(productHref);

			String data = "product_code::" + ProductCode + "|"
					+ "product_href::" + productHref + "|" + "manufacturer::"
					+ manufacturer + "|" + "product_name::"
					+ stripColorInfo(productName) + "|" + "rating::"
					+ cleanNumberInfo(productRating) + "|" + "price::"
					+ cleanNumberInfo(productPrice) + "|"
					+ "product_cash_back::" + cleanNumberInfo(productCashBack);
			try {
				data = data + "|"
						+ productInfo(productHref, ProductCode, manufacturer);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				phoneData.add("flipkart", data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(data);

		}
		phoneData.finalizeCache("flipkart");
		// return dataList;
	}

	private String extractManufacturer(String href) {
		String manufactuer = "";
		Pattern p = Pattern.compile("/([a-zA-Z0-9 .%]+)-");
		Matcher m = p.matcher(href);
		while (m.find()) {
			manufactuer = m.group(1);
		}
		return manufactuer;
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
		productName = productName.replaceAll("\\*", "");
		if (productName.contains("(") && productName.contains(")")) {
			int index = productName.lastIndexOf("(");
			productName = productName.substring(0, index);
			productName = productName.trim();
			return productName;
		} else {
			return productName;
		}
	}

	public String productInfo(String url, String productcode,
			String manufacturer) throws IOException {
		Document doc = null;
		try{
		doc = Jsoup.connect("http://flipkart.com" + url)
				.userAgent(DataProviderConstants.DATA_USER_AGENT).timeout(5000)
				.get();
		}catch(Exception e){
			System.out.println("http://flipkart.com" + url);
			throw new IOException();
		}
		String returninfo = "";
		if (doc.select("embed") != null
				&& doc.select("embed").toString().length() > 0) {
			String reviewVideo = doc.select("embed").attr("src");

			returninfo = "reviewVideo::" + reviewVideo + "|";

		}
		Elements ul = doc.select("ul[id=pp-small-carousel]");
		Elements imageList = ul.select("li");

		for (Element element : imageList) {

			if (element != null && element.select("img") != null
					&& element.select("img").attr("src") != null
					&& element.select("img").attr("src").length() > 0) {
				String imagesrc = element.select("img").attr("src");
				String imageMedium = imagesrc.replace("-40x", "-275x").replace(
						"x40-", "x275-");
				String imageLarge = imagesrc.replace("-40x", "-400x").replace(
						"x40-", "x400-");
				SquirrelUtils.extractImage(imagesrc, productcode + "-small",
						imageLoc + manufacturer + "\\" + productcode, false);
				SquirrelUtils.extractImage(imageMedium,
						productcode + "-medium", imageLoc + manufacturer + "\\"
								+ productcode, false);
				SquirrelUtils.extractImage(imageLarge, productcode + "-large",
						imageLoc + manufacturer + "\\" + productcode, true);
				break;// for now
			}

		}

		if (doc.select("div[class=offer-box fk-font-small]") != null
				&& doc.select("div[class=offer-box fk-font-small]").select(
						"div[class=line]") != null) {
			String offer = doc.select("div[class=offer-box fk-font-small]")
					.select("div[class=line]").text();
			if (offer.length() > 0)
				returninfo = returninfo + "offer::" + offer + "|";
		}
		if (doc.select("div[class=shipping-details]") != null
				&& doc.select("div[class=shipping-details]").text() != null
				&& doc.select("div[class=shipping-details]").text().length() > 0) {
			String shipping = doc.select("div[class=shipping-details]").text(); // remove
																				// [?]
			shipping = shipping.replace("[?]", "");
			returninfo = returninfo + "shipping::" + shipping + "|";
		}
		
		Elements specs = doc.getElementById("specifications").select("table");
		for (Element table : specs) {
			Elements rows = table.select("tr");
			for (Element row : rows) {
				String key = row.select("td[class=specs-key]").text();
				key = key.trim();
				key = key.replace(" " , "_");
				String value = row.select("td[class=specs-value fk-data]").text();
				value = value.trim();			
				if(!(key.isEmpty()&& value.isEmpty())){
					returninfo = returninfo + "info."+key+"::" + value + "|";
				}
			}
		}
		
		return returninfo;
	}

	/*
	 * public static void main(String[] args) { FlipKartInfoCrawler c = new
	 * FlipKartInfoCrawler(); //c.stripColorInfo("asdasd ( asdad)");
	 * System.out.println(c.stripColorInfo("asdasd (asdasd)")); }
	 */

}
