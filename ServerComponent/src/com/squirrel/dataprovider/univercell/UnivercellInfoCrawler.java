package com.squirrel.dataprovider.univercell;

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

public class UnivercellInfoCrawler implements Runnable {

	public UnivercellInfoCrawler(String url, SquirreCachedlList phoneData) {
		this.phoneData = phoneData;
		this.url = url;
	}

	public UnivercellInfoCrawler() {

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

	public static final String univercellURL = "/control/AjaxCategoryDetail?productCategoryId=%s&category_id=%s&attrName=&min=&max=&sortSearchPrice=&VIEW_INDEX=1&VIEW_SIZE=1000&serachupload=&sortupload=";
	private static final String baseurl = "http://www.univercell.in";
	public String url = "";
	public String manufacturer = "";
	public static List<String> failedURL = new ArrayList<String>();
	SquirreCachedlList phoneData;
	String[] types = { "PRO-SMART", "PRO-TABLET", "PRO-BASIC" };

public static void main(String[] args) {
	String str = "Apple New iPad 64 GB with WiFi";
	for (int i = 0; i < 10; i++) {
		if(i==5){
			continue;
		}
		System.out.println(i);
	}
	System.out.println(getProductCode(str));
}

	public void crawl() throws IOException {

		SquirrelCacheUtils.clearCache();

		SquirrelThreadFactory threadFactory = new SquirrelThreadFactory(
				"SquirrelThead");
		ExecutorService es = Executors.newFixedThreadPool(3, threadFactory);
		phoneData = new SquirreCachedlList();
		for (int i = 0; i < types.length; i++) {
			String formatedurl = String.format(univercellURL, types[i],
					types[i]);
			String manufactureUrl = baseurl + formatedurl;
			es.execute(new UnivercellInfoCrawler(manufactureUrl, phoneData));

		}

		es.shutdown();
		try {
			es.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		phoneData.finalizeCache("univercell");
		// System.out.println(failedURL.size());
	}

	// http://www.flipkart.com/mobiles/pr?sid=tyy%2C4io&layout=grid&start=21&ajax=true
	// http://www.flipkart.com/mobiles/pr?sid=tyy%2C4io
	// http://www.flipkart.com/cameras/pr?sid=jek%2Cp31&layout=grid&start=21&ajax=true
	public void run() {
		Document doc = null;
		try {
			doc = Jsoup.connect( url)
					.userAgent(DataProviderConstants.DATA_USER_AGENT)
					.timeout(25000).get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("***************"+url+"**********");
			e.printStackTrace();
			failedURL.add(url);
		}
		Elements ProductCount = doc.select(
				"div[class=productsummary-container matrix]").select("table");
		// System.out.println(ProductCount.toString());

		Elements lines = ProductCount.select("td");
		// System.out.println(lines.toString());
		for (Element element : lines) {
			String data = populateInfo(element);
			System.out.println(data);
			try {
				phoneData.add("univercell", data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// return dataList;
	}

	private String populateInfo(Element element) {

		Elements linkcont = element.select("div[class=smallimage]");
		Elements link = linkcont.select("a");
		String productHref = link.attr("href");
		Elements img = linkcont.select("img");
		String imglink = img.attr("src");
		String productName = element.select("div[class=productsummary]")
				.select("div[class=productinfo]").text();
		productName = productName.substring(0, productName.indexOf("Rs"));
		Elements manufElement = element.select("div[class=productinfo]");
		String manufacturer = "";
		if (manufElement.size() > 1) {
			manufacturer = manufElement.get(0).text();
		}
		String productCode = getProductCode(productName);
		String productPrice = "Please check product page for offer price";
		String MarketPrice = element.select("span[class=basePrice]")
				.select("span[title=Exclusive Online Price]")
				.select("span[class=basePrice]").text();

		if (MarketPrice.isEmpty()) {
			productPrice = element.select("span[class=regularPrice]")
					.select("span[title=Exclusive Online Price]").text();
			MarketPrice = productPrice;
		}

		if (!productName.isEmpty()) {

			String returnString = "product_name::"
					+ productName + "|"
					+ "manufacturer::" + manufacturer + "|" + "product_code::"
					+ productCode + "|" + "product_href::" + productHref + "|"
					+ "price::" + cleanNumberInfo(productPrice) + "|"
					+ "MarketPrice::" + cleanNumberInfo(MarketPrice);
			return returnString;
		}
		return "";

	}
	
	private static String getProductCode(String str){
		String productCode = SquirrelUtils.getProductCode(str); 
		String returnstr = productCode;
		int index = str.indexOf(productCode);
		
		if(index!=-1 && index>3){
			if(' '==str.charAt(index-1) && ' '==str.charAt(index-3)){
				char ch =str.charAt(index-2);
				returnstr = ch+productCode;
			}else if(index>4 &&' '==str.charAt(index-1) && ' '==str.charAt(index-4)){
				String sub = str.substring(index-4, index-1);
				if(!"GB".contains(sub))
					returnstr = sub+productCode;
			}
		}
	
		return returnstr;
	}

	private  String cleanNumberInfo(String price) {
		String returnstr="";
		returnstr = price.replaceAll("\\.00", "");
		returnstr = returnstr.replaceAll(",", "");
		//returnstr = returnstr.replaceAll("\\.", "");
		returnstr = returnstr.replaceAll("Rs\\.", "");
		return returnstr;
	}

}
