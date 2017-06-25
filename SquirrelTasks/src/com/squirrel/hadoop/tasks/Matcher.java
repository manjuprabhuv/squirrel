package com.squirrel.hadoop.tasks;

import java.util.ArrayList;
import java.util.List;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.MongeElkan;

public class Matcher {
	static int matchcounter = 0;
	static int nomatchcounter = 0;

	public static String approxMatch(List<String> list, String productName,
			String manufacturer, boolean retry) {
		manufacturer = manufacturer.toLowerCase().trim();
		productName = productName.toLowerCase().trim();
		String[] toks = productName.split(" ");
		String prdname = "";
		if (retry) {
			for (int i = 0; i < toks.length; i++) {
				boolean b = isProductCode(toks[i]);
				if (!b) {
					if (i == 0) {
						prdname = toks[i];
					} else {
						prdname = prdname + " " + toks[i];
					}
				}
			}
			productName = prdname;
		}

		List<String> matchedList = new ArrayList<String>();

		int count = 0;
		for (String value : list) {

			String[] array = value.split(";");
			String name = array[2];
			
			if (productName.contains(manufacturer)
					&& !name.toLowerCase().contains(manufacturer)) {
				name = manufacturer + " " + name;
			}
			name = name.replace(" iii", "3");
			name = name.replace(" ii", "2");
			productName = productName.replace(" iii", "3");
			productName = productName.replace(" ii", "2");
			AbstractStringMetric metric = new MongeElkan();
			float result = metric.getSimilarity(name, productName);
			if (result >= .9 && manufacturer.equalsIgnoreCase(array[3])) {				
				if (name.equalsIgnoreCase(productName)) {
					return array[0];
				}
				matchedList.add(value);

			} else if (count < 2) {
				matchedList.add(value);
			}
			count++;
		}

		String returnString = "";
		for (String value : matchedList) {
			String[] array = value.split(";");
			String name = array[2];
			if(!manufacturer.equalsIgnoreCase(array[3])){
				continue;
			}
			if (productName.toLowerCase().contains(manufacturer)
					&& !name.toLowerCase().contains(manufacturer)) {
				name = manufacturer + " " + name;
			}
			// name.replace(manufacturer, "");
			String[] prodNamefromLucene = name.split(" ");
			String[] prodNamefromHbase = productName.split(" ");
			String[] biggerarray = null;
			String[] smallerArray = null;
			if (prodNamefromHbase.length > prodNamefromLucene.length) {
				biggerarray = prodNamefromHbase;
				smallerArray = prodNamefromLucene;
			} else {
				biggerarray = prodNamefromLucene;
				smallerArray = prodNamefromHbase;
			}
			int totaltokens = smallerArray.length;
			int matchcount = 0;
			for (String token1 : smallerArray) {
				for (String token2 : biggerarray) {
					if (token1.toLowerCase().contains("_iii")) {
						token1.replace("_iii", "3");
					}
					if (token2.toLowerCase().contains("iii")) {
						token2.replace("_iii", "3");
					}
					if (token1.equalsIgnoreCase(token2)) {
						matchcount++;
						break;
					}

				}
			}
			if (biggerarray.length == smallerArray.length) {
				if (totaltokens >= 2 && totaltokens == matchcount + 1) {
					returnString = array[0];
					break;
				}

			}
			if (smallerArray.length > 5) {
				if (totaltokens >= 2
						&& totaltokens == matchcount
								+ factor(smallerArray.length)) {
					returnString = array[0];
					break;
				}

			}
			if (totaltokens >= 2 && totaltokens == matchcount) {
				returnString = array[0];
				break;
			}
		}
		if(returnString.isEmpty() && !retry){
			return approxMatch(matchedList, productName, manufacturer, true);
		}
		return returnString;
	}

	private static int factor(int num) {
		if (num <= 6) {
			return 2;
		} else if (num <= 10) {
			return 3;
		}
		return 0;
	}

	public static boolean isProductCode(String str) {

		String digits = str.replaceAll("[^0-9.]", "");
		if (digits != null && !digits.contains(".")
				&& digits.trim().length() >= 2) {

			return true;
		}

		return false;
	}

	public static String getProductCode(String str) {
		String strArray[] = str.split(" ");
		String productCode = "";
		for (int i = 0; i < strArray.length; i++) {
			String digits = strArray[i].replaceAll("[^0-9.]", "");
			if (i < strArray.length - 1
					&& ("GB".equalsIgnoreCase(strArray[i + 1]) || "G"
							.equalsIgnoreCase(strArray[i + 1])))
				continue;
			if (digits != null && !digits.contains(".")
					&& digits.trim().length() >= 2
					&& !strArray[i].contains("GB")) {

				productCode = strArray[i];
				productCode = productCode.toUpperCase();
				// System.out.println(productCode);
				break;
			}
		}

		if (productCode.length() == 0) {
			productCode = str;
			productCode = productCode.trim();
			productCode = productCode.replace(" ", "_");
		}
		return productCode;
	}

}
