package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

public class AuctionSearch implements IAuctionSearch {

	/* 
         * You will probably have to use JDBC to access MySQL data
         * Lucene IndexSearcher class to lookup Lucene index.
         * Read the corresponding tutorial to learn about how to use these.
         *
	 * You may create helper functions or classes to simplify writing these
	 * methods. Make sure that your helper functions are not public,
         * so that they are not exposed to outside of this class.
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
         */

	static private Connection conn = null;
    static private IndexSearcher searcher = null;
    static private QueryParser parser = null;

	
    AuctionSearch() {
        // create a connection to the database to retrieve Items from MySQL
		try {
		    conn = DbManager.getConnection(true);
		} catch (SQLException ex) {
		    System.out.println(ex);
		}

    }

    public void closeDB() throws IOException {
		// close the database connection
		if (conn!=null) {
			try {
			    conn.close();
			} catch (SQLException ex) {
			    System.out.println(ex);
			}
		}
    }

	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {


		try {
	        searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File("/var/lib/lucene/index1/"))));
	        //default search field is name
	        parser = new QueryParser("content", new StandardAnalyzer());

	 		Query q = parser.parse(query);
	 		//perform search
	 		TopDocs topList = searcher.search(q,numResultsToSkip+numResultsToReturn);
		 	ScoreDoc[] hits = topList.scoreDocs;
		 	SearchResult[] resultList = new SearchResult[hits.length-numResultsToSkip];


		 	for (int i=numResultsToSkip;i<hits.length;i++) {
		 		Document doc = searcher.doc(hits[i].doc);
				SearchResult sr = new SearchResult(doc.get("iid"),doc.get("name"));
		 		resultList[i-numResultsToSkip] = sr;
			}

		 	return resultList;

	 	} catch (IOException e) {
	 		e.printStackTrace();
	 	} catch (ParseException e) {
	 		System.out.println(e.getMessage());
	 	}

	 	return null;
	}

	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {
		
  		SearchResult[] returnList = null;

  		Connection conn = null;
		try {
		    conn = DbManager.getConnection(true);
		} catch (SQLException ex) {
		    System.out.println(ex);
		}

		try {
	  		String geoParam = "'Polygon(("+region.getLx()+" "+region.getLy()+
	  									","+region.getRx()+" "+region.getLy()+
	  									","+region.getRx()+" "+region.getRy()+
	  									","+region.getLx()+" "+region.getRy()+
	  									","+region.getLx()+" "+region.getLy()+"))'";
	        
	        PreparedStatement prepareRetrieveLoc = conn.prepareStatement(
	            "SELECT itemId FROM LocIndex WHERE " +
	    			"MBRContains(GeomFromText("+geoParam+"),location);"
	        );

	        ResultSet rs = prepareRetrieveLoc.executeQuery();
	       	int iid;
	       	String id,category;
	       	
	       	ArrayList<String> idList = new ArrayList<String>();
	        while (rs.next()) {
	        	iid = rs.getInt(1);
	        	id = Integer.toString(iid);
	        	idList.add(id);
	        }
	        SearchResult[] resultList = basicSearch(query, 0, idList.size());
	        ArrayList<SearchResult> resultArray = new ArrayList<SearchResult>();

	        for (int i=0;i<resultList.length;i++) {
	        	if (idList.contains(resultList[i].getItemId())) {
	        		resultArray.add(resultList[i]);
	        	}
	        }

	        //find matched elements
	        returnList = new SearchResult[resultArray.size()];
	        for (int i=0;i<resultArray.size();i++) {
	        	returnList[i] = resultArray.get(i);
	        }
			
		} catch (SQLException ex) {
	        System.out.println(ex);
	    } 
        
        // close the database connection
		try {
			conn.close();
		} catch (SQLException ex) {
			System.out.println(ex);
		}	

		return returnList;
	}

	public String getXMLDataForItemId(String itemId) {
		String xml="";

  		Connection conn = null;
  		//open database connection
		try {
		    conn = DbManager.getConnection(true);
		} catch (SQLException ex) {
		    System.out.println(ex);
		}

		try {
			Statement retrieveQuery = conn.createStatement();

			//todo




		} catch (SQLException ex) {
	        System.out.println(ex);
	    } 
        
        // close database connection
		try {
			conn.close();
		} catch (SQLException ex) {
			System.out.println(ex);
		}		

		return xml;
	}
	
	public String echo(String message) {
		return message;
	}

}
