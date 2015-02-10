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
			int numResultsToReturn) throws IOException, ParseException {

        searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File("/var/lib/lucene/"))));
        //default search field is name
        parser = new QueryParser("content", new StandardAnalyzer());

 		Query q = parser.parse(query);
 		//perform search
 		TopDocs topList = searcher.search(q,numResultsToSkip+numResultsToReturn);
 		ScoreDoc[] hits = topList.scoreDocs;
 		SearchResult[] resultList = new SearchResult[hits.length-numResultsToSkip];

 		for (int i=numResultsToSkip;i<hits.length;i++) {
 			Document doc = searcher.doc(hit[i].doc);
 			SearchResult sr = new SearchResult(doc.iid,doc.name);
 			resultList[i-numResultsToSkip] = sr;
 		}

		return resultList;
	}

	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {
		// TODO: Your code here!
		return new SearchResult[0];
	}

	public String getXMLDataForItemId(String itemId) {
		// TODO: Your code here!
		return "";
	}
	
	public String echo(String message) {
		return message;
	}

}
