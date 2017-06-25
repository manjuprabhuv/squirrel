package com.squirrel.hadoop.tasks;

import java.io.IOException;
import java.util.List;


public class SquirrelLuceneClient {
	
	public static void main(String[] args) throws IOException {
		SquirrelLuceneUtils utils = new SquirrelLuceneUtils();
		List<String> result = utils.searchProductName("Celkon C105", 5, "/home/manjuprabhuv/lucene/index");
				for (String values : result) {
					System.out.println(values);
				}
		//utils.getProductsByManufacturer("Apple",  50, "/home/manjuprabhuv/lucene/index");
	}
}
