/* CS144
 *
 * Parser skeleton for processing item-???.xml files. Must be compiled in
 * JDK 1.5 or above.
 *
 * Instructions:
 *
 * This program processes all files passed on the command line (to parse
 * an entire diectory, type "java MyParser myFiles/*.xml" at the shell).
 *
 * At the point noted below, an individual XML file has been parsed into a
 * DOM Document node. You should fill in code to process the node. Java's
 * interface for the Document Object Model (DOM) is in package
 * org.w3c.dom. The documentation is available online at
 *
 * http://java.sun.com/j2se/1.5.0/docs/api/index.html
 *
 * A tutorial of Java's XML Parsing can be found at:
 *
 * http://java.sun.com/webservices/jaxp/
 *
 * Some auxiliary methods have been written for you. You may find them
 * useful.
 */

package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;


class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;
    
    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    

    //Data structures for DB relations

    //Item
    public static class Item {
        String id;
        String name;
        String currently;
        String buy_price;
        String first_bid;
        String location_id;
        String started;
        String ends;
        String seller_id;
        String description;

        Item(String id, String name, String currently, String buy_price, String first_bid, 
                String location_id, String started, String ends, String seller_id, String description ) {
            this.id = id;
            this.name = name;
            this.currently = currently;
            this.buy_price = buy_price;
            this.first_bid = first_bid;
            this.location_id = location_id;
            this.started = started;
            this.ends = ends;
            this.seller_id = seller_id;
            this.description = description;
        }
    }

    //ItemCategory
    public static class ItemCategory {
        String iid;
        String cid;

        ItemCategory(String iid, String cid) {
            this.iid = iid;
            this.cid = cid;
        }
    }

    //Category
    public static class Category {
        String id;
        String name;

        Category(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    //Bid
    public static class Bid {
        String bid;
        String time;
        String amount;
        String iid;

        Bid(String bid, String time, String amount, String iid) {
            this.bid = bid;
            this.time = time;
            this.amount = amount;
            this.iid = iid;
        }
    }

    //User
    public static class User {
        String id;
        String location_id;
        String rating;

        User(String id, String location_id, String rating) {
            this.id = id;
            this.location_id = location_id;
            this.rating = rating; 
        }
    }

    //Location
    public static class Location {
        String id;
        String location;
        String country;
        String lng;
        String lat;

        Location(String id, String location, String country, String lng, String lat) {
            this.id = id;
            this.location = location;
            this.country = country;
            this.lng = lng;
            this.lat = lat;
        }
    }

    //Vectors for tuples storage
    public static ArrayList<Item> itemList = new ArrayList<Item>();
    public static ArrayList<ItemCategory> itemCategoryList = new ArrayList<ItemCategory>();
    public static ArrayList<Category> categoryList = new ArrayList<Category>();
    public static ArrayList<Bid> bidList = new ArrayList<Bid>();
    public static ArrayList<User> userList = new ArrayList<User>();
    public static ArrayList<Location> locationList = new ArrayList<Location>();

    //map category name to its id
    public static HashMap<String, String> categoryMap = new HashMap<String, String>();
    //set for storing user id
    public static HashSet<String> uidSet = new HashSet<String>();
    //map location name to its id
    public static HashMap<String, String> locationMap = new HashMap<String, String>();

    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
    }
    
    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        /* Fill in code here (you will probably need to write auxiliary
            methods). */
    
        //get the root of XML DOM tree
        Element root = doc.getDocumentElement();
        Element[] items = getElementsByTagNameNR(root, "Item");

        //index for category ID
        int categoryIndex = 0;
        int locationIndex = 0;

        //iterate through the item list
        for (int i=0; i<items.length; i++) {

            //item tuple
            String itemId = items[i].getAttribute("ItemID");
            String name = getElementTextByTagNameNR(items[i],"Name");
            String currently = strip(getElementTextByTagNameNR(items[i],"Currently"));
            String buy_price = strip(getElementTextByTagNameNR(items[i],"Buy_Price"));
            String first_bid = strip(getElementTextByTagNameNR(items[i],"First_Bid"));
            //todo
            String started = getElementTextByTagNameNR(items[i],"Started");
            String ends = getElementTextByTagNameNR(items[i],"Ends");
            String description = getElementTextByTagNameNR(items[i],"Description");
            //truncate description if it has more than 4000 characters
            if (description.length()>4000)
                    description = description.substring(0, 4000);
     


            //category tuple
            Element[] categories = getElementsByTagNameNR(items[i], "Category");
            for(int j=0; j<categories.length; j++) {
                String category = getElementText(categories[j]);
                String cid;
                if (categoryMap.containsKey(category)) {
                    cid = categoryMap.get(category);
                } else {
                    String cidString = Integer.toString(categoryIndex++);
                    categoryMap.put(category, cidString);
                    Category newCategory = new Category(cidString, category);
                    categoryList.add(newCategory);
                    cid = cidString;
                }

                //item_category tuple
                ItemCategory newItemCategory = new ItemCategory(itemId,cid);
                itemCategoryList.add(newItemCategory);

            }

            //bid tuple
            int num_of_bids = Integer.parseInt(getElementTextByTagNameNR(items[i],"Number_of_Bids"));
            Element bidsTag = getElementByTagNameNR(items[i], "Bids");
            Element[] bids = getElementsByTagNameNR(bidsTag,"Bid");
            
            //iterate through bids
            for (int j=0; j<num_of_bids; j++) {
                //get current bid
                Element bidder = getElementByTagNameNR(bids[j],"Bidder");

                //user tuple
                String rating = bidder.getAttribute("Rating");
                String uid = bidder.getAttribute("UserID");

                //location
                Element location = getElementByTagNameNR(bidder,"Location");
                String lat = location.getAttribute("Latitude");
                String lng = location.getAttribute("Longitude");
                String locName = getElementText(location);
                String country = getElementTextByTagNameNR(bidder,"Country");
                //todo
                String time = getElementTextByTagNameNR(bids[j],"Time");

                String amount = strip(getElementTextByTagNameNR(bids[j], "Amount"));

                //storing data
                //location tuple
                String location_id;
                if (locationMap.containsKey(locName)) {
                    location_id = locationMap.get(locName);
                } else {
                    String locIDString = Integer.toString(locationIndex++);
                    locationMap.put(locName,locIDString);
                    Location newLocation = new Location(locIDString,locName,country,lng,lat);
                    locationList.add(newLocation);
                    location_id = locIDString;
                }

                //user tuple
                String bidder_id = uid;
                if (!uidSet.contains(uid)) {
                    uidSet.add(uid);
                    User newUser = new User(uid, location_id, rating);
                    userList.add(newUser);
                }  

                //bid tuple
                Bid newBid = new Bid(bidder_id,time,amount,itemId);
                bidList.add(newBid);

            }

            //seller tuple
            Element iLocation = getElementByTagNameNR(items[i],"Location");
            String iLat =  iLocation.getAttribute("Latitude");
            String iLng = iLocation.getAttribute("Longitude");
            String iLocName = getElementText(iLocation);
            String iCountry = getElementTextByTagNameNR(items[i],"Country");
            
            String iLocation_id;
            if (locationMap.containsKey(iLocName)) {
                iLocation_id = locationMap.get(iLocName);
            } else {
                String locIDString = Integer.toString(locationIndex++);
                locationMap.put(iLocName,locIDString);
                Location newILocation = new Location(locIDString,iLocName,iCountry,iLng,iLat);
                locationList.add(newILocation);
                iLocation_id = locIDString;
            }            
       
            Element seller = getElementByTagNameNR(items[i],"Seller");
            String seller_rating = seller.getAttribute("Rating");
            String seller_id = seller.getAttribute("UserID");
            if (!uidSet.contains(seller_id)) {
                uidSet.add(seller_id);
                User newUser = new User(seller_id, iLocation_id, seller_rating);
                userList.add(newUser);
            }   

            //save item tuple
            Item newItem = new Item(itemId, name, currently, buy_price,first_bid,iLocation_id,started,
                                    ends,seller_id, description);  
            itemList.add(newItem);    


            //debug
            //System.out.println("Complete item: "+itemId);

        }
        
        
        
        /**************************************************************/
        
    }
    
    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }

        
        
    }
}
