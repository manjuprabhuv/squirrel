package com.squirrel.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;

import com.squirrel.commons.DataProviderConstants;

public class SquirrelUtils {

	public static boolean extractImage(String imgsrc, String imageName,
			String Directory, boolean skipDirectoryCheck) throws IOException {
		Response resultImageResponse = null;

		if (!new java.io.File(Directory + "\\" + imageName + ".jpg").exists()) {
			resultImageResponse = Jsoup.connect(imgsrc)
					.userAgent(DataProviderConstants.DATA_USER_AGENT)
					.timeout(5000).ignoreContentType(true).execute();
			if (!skipDirectoryCheck) {
				File dir = new File(Directory);
				if (!dir.exists()) {
					dir.mkdirs();
				}
			}
			FileOutputStream out = new FileOutputStream(new java.io.File(
					Directory + "\\" + imageName + ".jpg"));

			out.write(resultImageResponse.bodyAsBytes());
			out.close();
		}

		return true;

	}

	public static String getProductCode(String str) {
		String strArray[] = str.split(" ");
		String productCode = "";
		for (int i = 0; i < strArray.length; i++) {
			String digits = strArray[i].replaceAll("[^0-9.]", "");
			if(i<strArray.length-1 && ("GB".equalsIgnoreCase(strArray[i+1])||"G".equalsIgnoreCase(strArray[i+1])))
				continue;
			if (digits != null && !digits.contains(".")
					&& digits.trim().length() >= 2 &&!strArray[i].contains("GB")  ) {
				
				productCode = strArray[i];
				productCode = productCode.toUpperCase();
				//System.out.println(productCode);
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
	
	public static String extractValueMapper(String column, String data,String pattern) {
		// String txt =
		// "product_code::X101|product_code2::X1201|product_code3::X1031";

		String re1 = "(" + column + ")"; // Variable Name 1
		String re2 = ".*?"; // Non-greedy match on filler
		//String re3 = "((?:[a-z][a-z0-9_]*))"; // Variable Name 2
		String re3 = pattern; // Variable Name 2

		Pattern p = Pattern.compile(re1 + re2 + re3, Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL);
		Matcher m = p.matcher(data);
		if (m.find()) {
			// String var1=m.group(1);
			String var2 = m.group(2);
			return var2;
			// System.out.print("("+var1.toString()+")"+"("+var2.toString()+")"+"\n");
		}
		return "";
	}
	
	

}
