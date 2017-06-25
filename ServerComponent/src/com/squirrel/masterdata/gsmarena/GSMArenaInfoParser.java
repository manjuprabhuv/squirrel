package com.squirrel.masterdata.gsmarena;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.squirrel.commons.DataProviderConstants;
import com.squirrel.logger.SquirrelLogger;

public class GSMArenaInfoParser {
	SquirrelLogger logger = new SquirrelLogger("GSMArenaInfoParser");
			

	/**
	 * @param args
	 * @throws IOException
	 */
	public final static String DATA_CAMERA = "camera";

	public final static String DATA_COLORS = "colors";
	public final static String DATA_OTHER_FEATURES = "other_features";
	public final static String DATA_REVIEW_LINK = "Review_Link";
	public final static String DATA_RATING = "rating";

	public String parseInfo(Document doc) throws IOException {
		// Document doc = Jsoup.parse(new File("samsung1.html"), "utf-8");
		Elements elements = doc.select("tr");
		boolean isCamera = false;
		String data = "";
		String key = "";
		boolean first = true;
		for (Element element : elements) {
			Elements thElement = element.select("th");
			if (thElement != null && thElement.size() >= 0
					&& DATA_CAMERA.equalsIgnoreCase(thElement.text())) {
				isCamera = true;
			}
			if (thElement != null && thElement.size() > 0
					&& !DATA_CAMERA.equalsIgnoreCase(thElement.text())) {
				isCamera = false;
			}
			Elements element2 = element.select("td[class=ttl]");

			String value = "";
			if (element2 != null && element2.size() >= 1) {
				if (isCamera) {
					key = DATA_CAMERA + "_" + element2.get(0).text();
				} else {
					if (key.equalsIgnoreCase(DATA_COLORS)) {
						if (element2.get(0).text().length() == 1) {
							key = DATA_OTHER_FEATURES;
						}
					} else {
						key = element2.get(0).text();
					}
				}
			}
			element2 = null;
			element2 = element.select("td[class=nfo]");
			if (element2 != null && element2.size() >= 1) {

				value = element2.get(0).text();
				value = value.replace(",",
						DataProviderConstants.DATA_SEMI_COLON);

			}
			// System.out.println(key + DataProviderConstants.DATA_DOUBLE_COLON
			// + value);
			if (key != null && key.length() > 0) {
				key = key.replaceAll(" ", "_");
				key = key.toLowerCase();
			}
			logger.debug("key "+key);
			logger.debug("value "+value);
			
			if (first) {

				data = key + DataProviderConstants.DATA_DOUBLE_COLON + value;
				first = false;
			} else {
				if (key.length() == 1) {
					data = data + DataProviderConstants.DATA_SEMI_COLON + value;
				} else {
					data = data + DataProviderConstants.DATA_SEPARATOR + key
							+ DataProviderConstants.DATA_DOUBLE_COLON + value;
				}
			}
			logger.debug("data "+data);
		}
		logger.info("Element processing is done...");
		// for rating
		Elements ratingdl = doc.select("dl[id=vote-grph]");
		Elements ratings = ratingdl.select("tt");
		int count = 0;
		double rating = 0;
		for (Element element : ratings) {
			String value = element.text();
			if (value.contains("%")) {
				data = data + DataProviderConstants.DATA_SEPARATOR
						+ "Daily Interest"
						+ DataProviderConstants.DATA_DOUBLE_COLON + value;
			} else {
				if (count != 3) {
					double dValue=0;
					try{
					dValue = Double.parseDouble(value);
					}catch(Exception e){
						
					}
					rating = rating + dValue;
				} else {
					double dValue=0;
					try{
					dValue = Double.parseDouble(value);
					}catch(Exception e){
						
					}
					rating = rating + dValue;
					rating = rating / 3;
					data = data + DataProviderConstants.DATA_SEPARATOR
							+ DATA_RATING
							+ DataProviderConstants.DATA_DOUBLE_COLON + rating;
				}
			}
			count++;
			// System.out.println("daily "+rating);
		}
		logger.info("Rating Calculations is done...ratig ="+rating);
		Elements reviewLinks = doc.select("li[class=specs-cp-review]");
		String sReviewLinks = "";
		first = true;
		for (Element element : reviewLinks) {
			// System.out.println(element.text());
			// System.out.println(element.select(
			// DataProviderConstants.DATA_ANCHOR_HREF).attr(
			// DataProviderConstants.DATA_HREF));
			if (first) {
				sReviewLinks = DataProviderConstants.DATA_GSM_ARENA_URL
						+ element
								.select(DataProviderConstants.DATA_ANCHOR_HREF)
								.attr(DataProviderConstants.DATA_HREF);
				first = false;
			} else {
				sReviewLinks = sReviewLinks
						+ DataProviderConstants.DATA_SEMI_COLON
						+ DataProviderConstants.DATA_GSM_ARENA_URL
						+ element
								.select(DataProviderConstants.DATA_ANCHOR_HREF)
								.attr(DataProviderConstants.DATA_HREF);
			}
		}
		
		data = data + DataProviderConstants.DATA_SEPARATOR + DATA_REVIEW_LINK
				+ DataProviderConstants.DATA_DOUBLE_COLON + sReviewLinks;
		logger.info("Review Links.. Final data");
		return data;

	}

}
