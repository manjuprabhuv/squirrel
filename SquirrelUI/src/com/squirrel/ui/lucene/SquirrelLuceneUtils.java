package com.squirrel.ui.lucene;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.squirrelui.display.beans.ProductsBean;
@SuppressWarnings("deprecation")
public class SquirrelLuceneUtils {
	final Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_41);
	public IndexWriter initializeIndex(String path) throws IOException,
			ParseException {

		
		final IndexWriter w = new IndexWriter(FSDirectory.open(new File(path)),
				new IndexWriterConfig(Version.LUCENE_41, analyzer));

		return w;

	}

	public void addDoc(IndexWriter w, Map<String, String> inputMap)
			throws IOException {
		Document doc = new Document();
		for (String key : inputMap.keySet()) {
			String value = inputMap.get(key);
			doc.add(new TextField(key, value, Field.Store.YES));
			doc.add(new StringField(key, value, Field.Store.YES));
		}
		w.addDocument(doc);
	}
	
	public List<ProductsBean> searchProductName(String str,int limit,String indexLocation) throws IOException {
		List<ProductsBean> result = new ArrayList<ProductsBean>();
		Query q = null;
		try {
			q = new QueryParser(Version.LUCENE_41, "indexname", analyzer)
					.parse(str);
		} catch (org.apache.lucene.queryparser.classic.ParseException e) {
			e.printStackTrace();
		}
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(
				limit, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		System.out.println("Found " + hits.length + " hits.");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);			
			result.add(populateBean(d));
		}

		// reader can only be closed when there
		// is no need to access the documents any more.
		reader.close();
		return result;
	}
	
	public List<ProductsBean> getProductsByManufacturer(String str,int limit,String indexLocation) throws IOException {
		List<ProductsBean> result = new ArrayList<ProductsBean>();
		Query q = null;
		try {
			q = new QueryParser(Version.LUCENE_41, "manufacturer", analyzer)
					.parse(str);
		} catch (org.apache.lucene.queryparser.classic.ParseException e) {
			e.printStackTrace();
		}
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexLocation)));
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(
				limit, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		System.out.println("Found " + hits.length + " hits.");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);			
			result.add(populateBean(d));
		}

		// reader can only be closed when there
		// is no need to access the documents any more.
		reader.close();
		return result;
	}
	
	
	
	
	private ProductsBean populateBean(Document d){
		String prodName = d.get("name");
		String productKey = d.get("productKey");
		String rating = d.get("rating");
		String avgprice = d.get("avgprice");
		String productCode = d.get("productCode").toLowerCase();
		String manufacturer = d.get("manufacturer");
		String productCatagory = d.get("productCatagory");
		String summary = d.get("summary");
		System.out.println(d.get("summary"));
		ProductsBean p = new ProductsBean();
		p.setProductKey(productKey);
		p.setProdName(prodName);
		p.setBrand(manufacturer);
		p.setProdCode(productCode);
		p.setAvgPrice(avgprice);
		p.setSummary(summary);
		if (!rating.isEmpty()){
			p.setAvgRating(rating);
		}else {p.setAvgRating("0.0");}
		return p;
	}


}
