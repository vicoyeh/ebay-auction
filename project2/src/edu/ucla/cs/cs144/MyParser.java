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
import java.lang.String;
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
    
    static final String cs = "|*|";
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
        String bidder_rating;
        String seller_rating;

        User(String id, String location_id, String bidder_rating, String seller_rating) {
            this.id = id;
            this.location_id = location_id;
            this.bidder_rating = bidder_rating; 
            this.seller_rating = seller_rating; 
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

    //Maps for tuples storage
    public static HashMap<String,User> userMap = new HashMap<String,User>();
    public static HashMap<String,Location> locationMap = new HashMap<String,Location>();
    public static HashMap<String,Item> itemMap = new HashMap<String,Item>();
    public static HashMap<String,ItemCategory> itemCategoryMap = new HashMap<String,ItemCategory>();
    public static HashMap<String,Category> categoryMap = new HashMap<String,Category>();
    public static HashMap<String,Bid> bidMap = new HashMap<String,Bid>();


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

    /* Hash the string
     */
    static int hash(String str) {
        return str.hashCode();
    }


    /* Convert date from MMM-dd-yy HH:mm:ss format to yyyy-MM-dd HH:mm:ss format
     */
    static String changeDateFormat(String date) {
        String returnMe="";
        try  {
            DateFormat originalFormat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");
            DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date oldDate = originalFormat.parse(date); 
            returnMe = newFormat.format(oldDate); 
        } catch (ParseException e) {
            System.out.println("Print date error");
        }   
        return returnMe;
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
        int categoryIndex;
        int locationIndex;

        //iterate through the item list
        for (int i=0; i<items.length; i++) {

            //item tuple
            String itemId = items[i].getAttribute("ItemID");
            String name = getElementTextByTagNameNR(items[i],"Name");
            String currently = strip(getElementTextByTagNameNR(items[i],"Currently"));
            String buy_price = strip(getElementTextByTagNameNR(items[i],"Buy_Price"));
            if (buy_price == "") {
                buy_price = "\\N";
            }
            String first_bid = strip(getElementTextByTagNameNR(items[i],"First_Bid"));
            String started = changeDateFormat(getElementTextByTagNameNR(items[i],"Started"));
            String ends = changeDateFormat(getElementTextByTagNameNR(items[i],"Ends"));
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
                    cid = categoryMap.get(category).id;
                } else {
                    categoryIndex=hash(category);
                    String cidString = Integer.toString(categoryIndex);
                    Category newCategory = new Category(cidString, category);
                    categoryMap.put(category, newCategory);
                    cid = cidString;
                }

                //item_category tuple
                ItemCategory newItemCategory = new ItemCategory(itemId,cid);
                itemCategoryMap.put(itemId+cid,newItemCategory);

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
                
                String lat = "\\N";
                String lng = "\\N";
                String locName = "\\N";  
                if (location != null) {
                    if (location.hasAttribute("Latitude")) {
                        lat = location.getAttribute("Latitude");
                    }
                    
                    if (location.hasAttribute("Longitude")) {
                        lng = location.getAttribute("Longitude");
                    }
                    locName = getElementText(location);                  
                }
               
                String country = getElementTextByTagNameNR(bidder,"Country");
                String time = changeDateFormat(getElementTextByTagNameNR(bids[j],"Time"));

                String amount = strip(getElementTextByTagNameNR(bids[j], "Amount"));

                //storing data
                //location tuple
                String location_id;
                if (locationMap.containsKey(locName)) {
                    location_id = locationMap.get(locName).id;
                } else {
                    locationIndex = hash(locName);
                    String locIDString = Integer.toString(locationIndex);
                    Location newLocation = new Location(locIDString,locName,country,lng,lat);
                    locationMap.put(locName,newLocation);
                    location_id = locIDString;
                }

                //user tuple
                String bidder_id = uid;
                User existingUser = userMap.get(uid);
                if (existingUser == null) {
                    User newUser = new User(uid, location_id, rating, "\\N");
                    userMap.put(uid,newUser);
                } else {
                    existingUser.bidder_rating = rating;
                } 

                //bid tuple
                Bid newBid = new Bid(bidder_id,time,amount,itemId);
                bidMap.put(bidder_id+time+itemId,newBid);
                

            }

            //seller tuple
            Element iLocation = getElementByTagNameNR(items[i],"Location");
            String iLat = "\\N";
            if (iLocation.hasAttribute("Latitude")) {
                iLat = iLocation.getAttribute("Latitude");
            }
            String iLng = "\\N";
            if (iLocation.hasAttribute("Longitude")) {
                iLng = iLocation.getAttribute("Longitude");
            }
            String iLocName = getElementText(iLocation);
            String iCountry = getElementTextByTagNameNR(items[i],"Country");
            
            

            String iLocation_id;
            if (locationMap.containsKey(iLocName)) {
                
                iLocation_id = locationMap.get(iLocName).id;

            } else {
                locationIndex = hash(iLocName);
                String locIDString = Integer.toString(locationIndex);
                Location newILocation = new Location(locIDString,iLocName,iCountry,iLng,iLat);
                locationMap.put(iLocName,newILocation);
                iLocation_id = locIDString;
            }            
       
            Element seller = getElementByTagNameNR(items[i],"Seller");
            String seller_rating = seller.getAttribute("Rating");
            String seller_id = seller.getAttribute("UserID");
            User existingUser = userMap.get(seller_id);
            if (existingUser == null) {
                User newUser = new User(seller_id, iLocation_id, "\\N", seller_rating);
                userMap.put(seller_id, newUser);
            } else {
                existingUser.seller_rating = seller_rating; 
            } 

            //save item tuple
            Item newItem = new Item(itemId, name, currently, buy_price,first_bid,iLocation_id,started,
                                    ends,seller_id, description);  
            itemMap.put(itemId,newItem);      
            

        }
        
        //debug
        //System.out.println("Complete item!!!!!!!!");
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


        //output dat files  

        // output Location tuples
        try {

            FileWriter fw = new FileWriter("dat/Locations.dat");
            BufferedWriter bw= new BufferedWriter(fw);
     
            for (Location i : locationMap.values()) {

                String tuple= i.id + cs + i.location + cs + i.country + cs + i.lat + cs + i.lng;
                tuple += "\n";
                bw.write(tuple); 
            }

            bw.close();
            fw.close();

        } catch (IOException e) {
                e.printStackTrace();
        }


        // output User tuples
        try {

            FileWriter fw = new FileWriter("dat/Users.dat");
            BufferedWriter bw= new BufferedWriter(fw);
            
            for (User i : userMap.values()) {
                String tuple = i.id + cs + i.location_id + cs + i.bidder_rating + cs + i.seller_rating;      
                tuple += "\n";
                bw.write(tuple); 
            }

            bw.close();
            fw.close();

        } catch (IOException e) {
                e.printStackTrace();
        }

        // output Category tuples
        try {

            FileWriter fw = new FileWriter("dat/Categories.dat");
            BufferedWriter bw= new BufferedWriter(fw);
     
            for (Category i : categoryMap.values()) {
                String tuple = i.id + cs + i.name;
                tuple += "\n";
                bw.write(tuple); 
            }

            bw.close();
            fw.close();

        } catch (IOException e) {
                e.printStackTrace();
        }        

        // output Item tuples
        try {

            FileWriter fw = new FileWriter("dat/Items.dat");
            BufferedWriter bw= new BufferedWriter(fw);
     
            for (Item i : itemMap.values()) {
                String tuple = i.id + cs + i.name + cs + i.currently + cs + i.buy_price + cs + i.first_bid + cs + i.location_id +
                        cs + i.started + cs + i.ends + cs + i.seller_id + cs + i.description;
                tuple += "\n";
                bw.write(tuple); 
            }

            bw.close();
            fw.close();

        } catch (IOException e) {
                e.printStackTrace();
        }    

        // output ItemCategory tuples
        try {

            FileWriter fw = new FileWriter("dat/ItemCategories.dat");
            BufferedWriter bw= new BufferedWriter(fw);
     
            for (ItemCategory i : itemCategoryMap.values()) {
                String tuple = i.iid + cs + i.cid;
                tuple += "\n";
                bw.write(tuple); 
            }

            bw.close();
            fw.close();

        } catch (IOException e) {
                e.printStackTrace();
        }    

        // output Bid tuples
        try {

            FileWriter fw = new FileWriter("dat/Bids.dat");
            BufferedWriter bw= new BufferedWriter(fw);
     
            for (Bid i : bidMap.values()) {
                String tuple = i.bid + cs + i.time + cs + i.amount + cs + i.iid;
                tuple += "\n";
                bw.write(tuple); 
            }

            bw.close();
            fw.close();

        } catch (IOException e) {
                e.printStackTrace();
        }    


    }
}
