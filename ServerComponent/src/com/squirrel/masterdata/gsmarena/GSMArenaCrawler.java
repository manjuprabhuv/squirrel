package com.squirrel.masterdata.gsmarena;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.squirrel.commons.DataProviderConstants;
import com.squirrel.logger.SquirrelLogger;
import com.squirrel.utils.SquirreCachedlList;
import com.squirrel.utils.SquirrelCacheUtils;
import com.squirrel.utils.SquirrelThreadFactory;
import com.squirrel.utils.SquirrelUtils;

public class GSMArenaCrawler implements Runnable {
	SquirrelLogger logger = new SquirrelLogger("GSMArenaCrawler");
	public final static String GSM_ARENA_URL = "http://www.gsmarena.com/";
	public final static String GSM_ARENA_MAKERS_URL = "http://www.gsmarena.com/makers.php3";
	public final static String PHP = ".php";

	public final static String ADD_CLICK_PHP = "adclick.php";
	public final static String ABS_SRC = "abs:src";

	public final static String IMG = "img";
	//
	private boolean skipDirectoryCreation = false;
	public static Map<String, String> failedProdInfoList = new HashMap<String, String>();
	public static Map<String, String> failedImageList = new HashMap<String, String>();
	public final static String BRAND_IMAGES_LOC = "D:\\project\\stage\\gsmArena\\brandImages";
	Thread t;

	public GSMArenaCrawler() {

	}

	String uniqeURL;
	String baseURL;
	String gsmMCode;
	String PcLeastDigit;
	boolean firstPass;
	SquirreCachedlList phoneData;

	public GSMArenaCrawler(String uniqeURL, String baseURL, String gsmMCode,
			String PcLeastDigit, boolean firstPass) {
		this.uniqeURL = uniqeURL;
		this.baseURL = baseURL;
		this.gsmMCode = gsmMCode;
		this.PcLeastDigit = PcLeastDigit;
		this.firstPass = firstPass;
		this.phoneData = new SquirreCachedlList();
	}

	public void run() {
		int pageCount;
		try {
			pageCount = CrawlPage(this.uniqeURL, this.baseURL, this.gsmMCode,
					this.PcLeastDigit, this.firstPass, this.phoneData);
			for (int i = 2; i <= pageCount; i++) {
				// motorola-phones-f-4-0-p2.php
				String newBaseURL = this.baseURL + "-f-" + this.gsmMCode
						+ "-0-p" + i + PHP;
				System.out.println(newBaseURL);
				CrawlPage(this.uniqeURL, newBaseURL, this.gsmMCode,
						PcLeastDigit, false, phoneData);

			}
			recursiveFailedImagepuller();
			reccursiveCounter = 0;
			recursiveFailedInfoparser(this.phoneData, this.uniqeURL);
			String manuf = uniqeURL.replace("_", "");
			phoneData.finalizeCache(manuf);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void crawl(Properties props) throws IOException {
		SquirrelCacheUtils.clearCache();
		Document doc = null;
		// doc = Jsoup.parse(new File("samsung1.html"), "utf-8");

		try {
			doc = Jsoup.connect(GSM_ARENA_MAKERS_URL)
					.userAgent(DataProviderConstants.DATA_USER_AGENT)
					.timeout(3000).get();
		} catch (IOException e) {

			throw e;
		}

		Elements brands = doc.select("div[class=st-text]");
		Elements links = brands.select(DataProviderConstants.DATA_ANCHOR_HREF);
		// List<String> phoneData = new ArrayList<String>();
		SquirreCachedlList phoneData = new SquirreCachedlList();
		SquirrelThreadFactory threadFactory = new SquirrelThreadFactory("SquirrelThead");
		ExecutorService es = Executors.newFixedThreadPool(10, threadFactory);
		for (Element link : links) {
			String url = link.attr(DataProviderConstants.DATA_HREF);
			String imgsrc = "";
			Elements chldElements = link.children();
			Element childElement = chldElements.first();
			if (childElement != null
					&& IMG.equalsIgnoreCase(childElement.tagName())) {
				imgsrc = childElement.attr(ABS_SRC);

			}
			// System.out.println(url);
			if (imgsrc.length() > 0) {
				url = url.replace(PHP, "");
				String[] urlInfo = url.split("-");
				String manuf = urlInfo[0];
				String productCode = urlInfo[2];
				String PcLeastDigit = props.getProperty(manuf);
				if (PcLeastDigit == null
						|| (PcLeastDigit != null && PcLeastDigit
								.equalsIgnoreCase(""))) {
					PcLeastDigit = "-1";
				}
				// manuf;
				String baseURL = "";

				if (manuf.length() > 0 && productCode.length() > 0
						&& PcLeastDigit.length() > 0
						&& (productCode.equalsIgnoreCase("48"))) {
					try {
						SquirrelUtils.extractImage(imgsrc, manuf,
								BRAND_IMAGES_LOC, false);
					} catch (Exception e) {
						failedImageList.put(imgsrc, manuf);
					}
					baseURL = manuf + "-phones";
					es.execute(new GSMArenaCrawler(manuf + "_", baseURL,
							productCode, PcLeastDigit, true));
					/*
					 * new GSMArenaCrawler(manuf + "_", baseURL, productCode,
					 * PcLeastDigit, true, phoneData);
					 */
					// System.out.println(productCode);

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
		// return phoneData;
	}

	static int reccursiveCounter = 0;

	private void recursiveFailedInfoparser(SquirreCachedlList phoneData,
			String manuf) {
		manuf = manuf.replaceAll("_", "");
		Map<String, String> tempList = new HashMap<String, String>();
		if (failedProdInfoList.size() > 0) {
			tempList.putAll(failedProdInfoList);
			failedProdInfoList.clear();
			Set<String> keyset = tempList.keySet();
			Iterator<String> itr = keyset.iterator();
			while (itr.hasNext()) {
				String key = itr.next();
				String value = tempList.get(key);
				String[] str = value
						.split(DataProviderConstants.DATA_SEPARATOR);
				String imgsrc = str[0];
				String productCode = str[1];
				String productName = str[2];

				parseImageAndInfo(imgsrc, productCode, key, productName,
						phoneData, manuf);
				failedProdInfoList.remove(key);
			}
		}

		if (failedProdInfoList.size() > 0 && reccursiveCounter <= 4) {
			reccursiveCounter++;
			recursiveFailedInfoparser(phoneData, manuf);

		}

	}

	private void recursiveFailedImagepuller() {
		Map<String, String> tempList = new HashMap<String, String>();
		if (failedImageList.size() > 0) {
			tempList.putAll(failedImageList);
			failedImageList.clear();
			Set<String> keyset = tempList.keySet();
			Iterator<String> itr = keyset.iterator();
			while (itr.hasNext()) {
				String key = itr.next();
				String value = tempList.get(key);
				String productCode = "";
				String Directory = "";
				String manufacturer = "";
				if (value.contains(":")) {
					String str[] = value.split(":");
					productCode = str[0];
					manufacturer = str[1];
					Directory = BRAND_IMAGES_LOC + "\\" + manufacturer;
				} else {
					productCode = value;
					Directory = BRAND_IMAGES_LOC;
				}
				try {

					SquirrelUtils.extractImage(key, productCode, Directory,
							true);

					skipDirectoryCreation = true;
				} catch (Exception e) {
					failedImageList.put(key, productCode + ":" + manufacturer);
				}

			}
		}

		if (failedImageList.size() > 0 && reccursiveCounter <= 4) {
			reccursiveCounter++;
			recursiveFailedImagepuller();

		}
	}

	private int CrawlPage(String uniqeURL, String baseURL, String gsmMCode,
			String PcLeastDigit, boolean firstPass, SquirreCachedlList phoneData)
			throws IOException {
		// http://www.gsmarena.com/nokia-phones-f-1-0-p2.php
		// http://www.gsmarena.com/nokia-phones-1.php
		String url_s = "";
		if (firstPass) {
			url_s = GSM_ARENA_URL + baseURL + "-" + gsmMCode + PHP;
		} else {
			url_s = GSM_ARENA_URL + baseURL;
		}
		int pageCount = 0;
		Document doc = null;

		try {
			doc = Jsoup.connect(url_s)
					.userAgent(DataProviderConstants.DATA_USER_AGENT)
					.timeout(3000).get();
		} catch (IOException e) {

			throw e;
		}

		// doc = Jsoup.parse(new File("GSMCrawlerTest.html"), "utf-8");
		Elements links = doc.select(DataProviderConstants.DATA_ANCHOR_HREF);
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
			if (url.contains(uniqeURL) && !url.contains(ADD_CLICK_PHP)) { // use
																			// /sumsung_
				Elements chldElements = link.children();
				Element childElement = chldElements.first();
				if (childElement != null
						&& IMG.equalsIgnoreCase(childElement.tagName())) {
					imgsrc = childElement.attr(ABS_SRC);
				}
				String productName = link.text();
				String productCode = SquirrelUtils.getProductCode(productName);
				System.out.println("URL : " + url + "\t NAME : " + productName
						+ "\t IMG : " + imgsrc + "\t Product Code :"
						+ productCode);

				// * if (!productList.contains(productCode))
				String manuf = uniqeURL.replace("_", "");
				parseImageAndInfo(imgsrc, productCode, url, productName,
						phoneData, manuf);

			}
		}

		/*
		 * for (String str : list) { System.out.println(str); }
		 */
		return pageCount;
	}

	private void parseImageAndInfo(String imgsrc, String productCode,
			String url, String productName, SquirreCachedlList phoneData,
			String manufacturer) {
		try {
			String productInfo = "";
			productInfo = extractImageAndInfo(imgsrc, productCode, url,
					manufacturer);
			String data = DataProviderConstants.DATA_PRODUCT_CODE
					+ DataProviderConstants.DATA_DOUBLE_COLON + productCode
					+ DataProviderConstants.DATA_SEPARATOR
					+ DataProviderConstants.DATA_PRODUCT_NAME
					+ DataProviderConstants.DATA_DOUBLE_COLON + productName
					+ DataProviderConstants.DATA_SEPARATOR + productInfo;
			phoneData.add(manufacturer, data);
		} catch (Exception e) {
			failedProdInfoList.put(url, imgsrc
					+ DataProviderConstants.DATA_SEPARATOR + productCode
					+ DataProviderConstants.DATA_SEPARATOR + productName);
		}

	}

	// static List<String> list = new ArrayList<String>();

	

	private String extractImageAndInfo(String imgsrc, String productCode,
			String url, String manufacturer) throws IOException {
		String productInfo = "";

		String Directory = "D:\\project\\stage\\gsmArena\\" + manufacturer;
		if (!skipDirectoryCreation) {
			try {
				SquirrelUtils.extractImage(imgsrc, productCode, Directory,
						skipDirectoryCreation);

				skipDirectoryCreation = true;
			} catch (Exception e) {
				failedImageList.put(imgsrc, productCode + ":" + manufacturer);
			}
		} else {
			try {
				SquirrelUtils.extractImage(imgsrc, productCode, Directory,
						skipDirectoryCreation);
			} catch (Exception e) {
				failedImageList.put(imgsrc, productCode + ":" + manufacturer);
			}
		}
		System.out.println("image writing done");
		Document infoDoc = Jsoup.connect(GSM_ARENA_URL + url)
				.userAgent(DataProviderConstants.DATA_USER_AGENT).timeout(3000)
				.ignoreContentType(true).get();
		GSMArenaInfoParser parser = new GSMArenaInfoParser();
		productInfo = parser.parseInfo(infoDoc);
		/*
		 * if (!productList.contains(productCode)) {
		 * productList.add(productCode); }
		 */

		// System.out.println(doc.html());
		System.out.println("Html writing done");

		return productInfo;
	}

}
