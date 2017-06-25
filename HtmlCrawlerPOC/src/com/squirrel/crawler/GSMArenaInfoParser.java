package com.squirrel.crawler;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GSMArenaInfoParser {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Document doc = Jsoup.parse(new File("samsung1.html"), "utf-8");
		Elements elements = doc.select("tr");
		boolean isCamera = false;
		for (Element element : elements) {
			Elements thElement = element.select("th");			
			if (thElement != null && thElement.size() >= 0
					&& "Camera".equalsIgnoreCase(thElement.text())) {
				isCamera = true;
			}if(thElement != null && thElement.size() > 0
					&& !"Camera".equalsIgnoreCase(thElement.text())){
				isCamera = false;
			}
			Elements element2 = element.select("td[class=ttl]");
			String key = "";
			String value = "";
			if (element2 != null && element2.size() >= 1) {
				if (isCamera) {
					key = "Camera_" + element2.get(0).text();
				} else {
					key = element2.get(0).text();
				}
			}
			element2 = null;
			element2 = element.select("td[class=nfo]");
			if (element2 != null && element2.size() >= 1) {

				value = element2.get(0).text();

			}
			System.out.println(key + " :: " + value);

		}
		//for rating
		Elements ratingdl = doc.select("dl[id=vote-grph]");
		Elements ratings = ratingdl.select("tt");
		for (Element element : ratings) {
			
			System.out.println(element.text());
		}
		Elements reviewLinks = doc.select("li[class=specs-cp-review]");
		for (Element element : reviewLinks) {
			System.out.println(element.text());			
			System.out.println(element.select("a[href]").attr("href"));
		}

	}

}
