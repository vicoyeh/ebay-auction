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
	       	String id;
	       	
	       	ArrayList<String> idList = new ArrayList<String>();
	        while (rs.next()) {
	        	iid = rs.getInt(1);
	        	id = Integer.toString(iid);
	        	idList.add(id);
	        }

	        //last parameter is arbitrary for the number of items returned by basic search
	        SearchResult[] resultList = basicSearch(query, 0, 5000);
	        ArrayList<SearchResult> resultArray = new ArrayList<SearchResult>();
	        for (int i=0;i<resultList.length;i++) {
	        	if (idList.contains(resultList[i].getItemId())) {
	        		resultArray.add(resultList[i]);
	        	}
	        }

	        //filter numResultsToSkip+Return
	        ArrayList<SearchResult> filterList = new ArrayList<SearchResult>();
	        int count=0;
	        for (int i=numResultsToSkip;i<resultArray.size();i++, count++) {
	        	if (count==numResultsToReturn) {
	        		break;
	        	}
	        	filterList.add(resultArray.get(i));
	        }

	        //convert to array
	        returnList = filterList.toArray(new SearchResult[filterList.size()]);
			
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
	        PreparedStatement retrieveItem = conn.prepareStatement(
	            "SELECT * FROM Items INNER JOIN Users ON seller_id = Users.id INNER JOIN Location ON location_id = Location.id WHERE Items.id = ?"
	        );
            retrieveItem.setString(1, itemId);
            ResultSet rs = retrieveItem.executeQuery();
            rs.first();
            if (rs.getRow() != 0) {
                org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();                

                Element root = doc.createElement("Item");
                root.setAttribute("ItemID", itemId);

                // name
                Element itemName = doc.createElement("Name");
                itemName.appendChild(doc.createTextNode(escapeSpecial(rs.getString("name"))));
                root.appendChild(itemName);

                // categories
                PreparedStatement retrieveCategories = conn.prepareStatement(
                    "SELECT category FROM Categories INNER JOIN Items_Categories ON id = category_id WHERE item_id = ?"
                );
                retrieveCategories.setString(1, itemId);
                ResultSet categoriesRs = retrieveCategories.executeQuery();
                while (categoriesRs.next()) {
                    Element category = doc.createElement("Category");
                    category.appendChild(doc.createTextNode(escapeSpecial(categoriesRs.getString("category"))));
                    root.appendChild(category);
                }
                categoriesRs.close();
                retrieveCategories.close();

                // currently
                Element currently = doc.createElement("Currently");
                currently.appendChild(doc.createTextNode("$" + rs.getString("currently")));
                root.appendChild(currently);
    
                // buyprice
                String bp = rs.getString("buy_price");
                if (bp != null) {
                    Element buyPrice = doc.createElement("Buy_Price");
                    buyPrice.appendChild(doc.createTextNode("$" + bp));
                    root.appendChild(buyPrice);
                }

                // firstbid
                Element firstBid = doc.createElement("First_Bid");
                firstBid.appendChild(doc.createTextNode("$" + rs.getString("first_bid")));
                root.appendChild(firstBid);

                Element bids = doc.createElement("Bids");
                PreparedStatement retrieveBids = conn.prepareStatement(
                    "SELECT * FROM Bids INNER JOIN Users ON bidder_id = Users.id INNER JOIN Location ON Users.location_id = Location.id WHERE item_id = ?"
                );
                retrieve.setString(1, itemId);
                ResultSet bidsRs = retrieveBids.executeQuery();
                Integer numBids = 0;
                while (bidsRs.next()) {
                    Element bid = doc.createElement("Bid");

                    // bidder
                    Element bidder = doc.createElement("Bidder");
                    bidder.setAttribute("UserID", escapeSpecial(bidsRs.getString("bidder_id")));
                    bidder.setAttribute("Rating", bidresult.getString("bidder_rating"));
                    String loc = bidsRs.getString("location");
                    if (loc != null) {
                        Element location = doc.createElement("Location");
                        location.appendChild(doc.createTextNode(escapeSpecial(loc)));
                        bidder.appendChild(location);
                    }
                    String country = bidsRs.getString("country");
                    if (country != null) {
                        Element country = doc.createElement("Country");
                        country.appendChild(doc.createTextNode(escapeSpecial(country)));
                        bidder.appendChild(country);
                    }
                    bid.appendChild(bidder);

                    // time
                    Element time = doc.createElement("Time");
                    time.appendChild(doc.createTextNode(convertDate(bidresult.getString("time"), "yyyy-MM-dd HH:mm:ss", "MMM-dd-yy HH:mm:ss")));
                    bid.appendChild(time);

                    // amount
                    Element amount = doc.createElement("Amount"); 
                    amount.appendChild(doc.createTextNode(bidresult.getString("amount")));
                    bid.appendChild(amount);

                    bids.appendChild(bid);
                    numBids++;
                }

                // numbids
                Element numberBids = doc.createElement("Number_of_Bids");
                numberBids.appendChild(doc.createTextNode(Integer.toString(numBids)));
                root.appendChild(numberBids);
                
                // bids
                root.appendChild(bids);
                bidsRs.close();
                retrieveBids.close();

                // location
                Element location = doc.createElement("Location");
                location.appendChild(doc.createTextNode((escapeSpecial(rs.getString("location")))));
                root.appendChild(location);

                // country
                Element country = doc.createElement("Country");
                country.appendChild(doc.createTextNode((escapeSpecial(rs.getString("country")))));
                root.appendChild(country);

                // started
                Element started = doc.createElement("Started");
                started.appendChild(doc.createTextNode(convertDate(rs.getString("started"), "yyyy-MM-dd HH:mm:ss", "MMM-dd-yy HH:mm:ss")));
                root.appendChild(started);

                // ends
                Element ends = doc.createElement("Ends");
                ends.appendChild(doc.createTextNode(convertDate(rs.getString("ends"), "yyyy-MM-dd HH:mm:ss", "MMM-dd-yy HH:mm:ss")));
                root.appendChild(ends);

                // seller
                Element seller = doc.createElement("Seller");
                seller.setAttribute("UserID", (escapeSpecial(rs.getString("seller_id"))));
                seller.setAttribute("Rating", rs.getString("seller_rating"));
                root.appendChild(seller);

                // description
                Element description = doc.createElement("Description");
                description.appendChild(doc.createTextNode(escapeSpecial(rs.getString("description"))));
                root.appendChild(description);

                doc.appendChild(root);
            }

            // Write the XML
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult res = new StreamResult(writer);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(source, res);
            xmlstore = writer.toString();

            rs.close();
            retrieveItem.close();

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

    private String escapeSpecial(String unescapedString) {
        return unescapedString.replaceAll("\"", "&quot;")
            .replaceAll("\'", "&apos;")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&lt;");
    }

    private String convertDate(String date, String original_format, String converted_format) {
        SimpleDateFormat sdf = new SimpleDateFormat(original_format);
        String formatted_date = "";
        try {
            Date d = sdf.parse(date);
            sdf.applyPattern(converted_format);
            out = sdf.format(d);
        } catch (Exception e) {
            System.out.println("Error formatting date");
        }

        return formatted_date;
    }

}
