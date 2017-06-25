package com.squirrel.hadoop.tasks;

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
			
		}
		w.addDocument(doc);
	}
	public void getProductsByManufacturer(String str,int limit,String indexLocation) throws IOException {
		//List<ProductsBean> result = new ArrayList<ProductsBean>();
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
			String name = d.get("name");
			String rating = d.get("rating");
			String price = d.get("avgprice");
			System.out.println(name);
			//System.out.println(rating);
			//System.out.println(price);
		}

		// reader can only be closed when there
		// is no need to access the documents any more.
		reader.close();
		//return result;
	}
	public List<String> searchProductName(String str,int limit,String indexLocation) throws IOException {
		List<String> result = new ArrayList<String>();
		Query q = null;
		try {
			q = new QueryParser(Version.LUCENE_41, "name", analyzer)
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
			
			System.out.println(d.get("productKey"));
			System.out.println(d.get("rating"));
			System.out.println(d.get("avgprice"));
			System.out.println(d.get("productCode"));
			System.out.println(d.get("indexname"));
			System.out.println(d.get("name"));
			System.out.println(d.get("manufacturer"));
			System.out.println(d.get("productCatagory"));
			System.out.println(d.get("summary"));
			
			
			result.add(d.get("name"));
		}

		// reader can only be closed when there
		// is no need to access the documents any more.
		reader.close();
		return result;
	}
	
	public List<String> productComparator(String str,int limit,String indexLocation) throws IOException{
		List<String> returnList = new ArrayList<String>();
		
		Query q = null;
		try {
			q = new QueryParser(Version.LUCENE_41, "name", analyzer)
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
		//System.out.println("Found " + hits.length + " hits.");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			String productKey = d.get("productKey");
			String productCode = d.get("productCode");
			String name = d.get("name");
			String manufacturer = d.get("manufacturer");
			String result = "";
			result = productKey+";"+productCode+";"+name+";"+manufacturer;
			if(result.length()>0){
				result = result.toLowerCase();
				returnList.add(result);
			}
		}

		// reader can only be closed when there
		// is no need to access the documents any more.
		reader.close();
		return returnList;
	
	}


}
