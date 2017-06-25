package com.squirrel.ui.lucene;

import java.io.IOException;
import java.util.List;

import com.squirrelui.display.beans.ProductsBean;

public class SquirrelLuceneClient {

	public static void main(String[] args) throws IOException {
		SquirrelLuceneUtils utils = new SquirrelLuceneUtils();
		List<ProductsBean> result = utils.searchProductName("A110", 5,
				"C:\\DATA\\EclipseWorkspace\\lucene\\index");
		for (ProductsBean values : result) {
			System.out.println(values.getProdName());
		}
		result = utils.getProductsByManufacturer("micromax", 5,
				"C:\\DATA\\EclipseWorkspace\\lucene\\index");
		for (ProductsBean values : result) {
			System.out.println(values.getProductKey());
		}
	}
}
