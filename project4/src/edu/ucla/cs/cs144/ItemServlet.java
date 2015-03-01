package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.text.*;
import java.util.*;
import java.lang.String;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}




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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String id = request.getParameter("id");

        AuctionSearchClient searchClient = new AuctionSearchClient();
        String xml = searchClient.getXMLDataForItemId(id);
      

      	try {
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		   	DocumentBuilder builder = factory.newDocumentBuilder();
		    StringReader reader = new StringReader(xml);
		    Document doc = builder.parse(new InputSource(reader));
		    Element root = doc.getDocumentElement();

		    String Name = getElementTextByTagNameNR(root,"Name");
		    String ItemID = root.getAttribute("ItemID");
		    //category
		    ArrayList<String> Categories=new ArrayList<String>();
		    Element[] categories=getElementsByTagNameNR(root,"Category");
		    for (int i=0;i<categories.length;i++) {
		    	Categories.add(getElementText(categories[i]));
		    }

		    String Currently = getElementTextByTagNameNR(root,"Currently");
		    String Buy_Price=getElementTextByTagNameNR(root,"Buy_Price");
		    if (Buy_Price.isEmpty())
		    	Buy_Price="N/A";
		    String First_Bid=getElementTextByTagNameNR(root,"First_Bid");
		    int Number_of_Bids=Integer.parseInt(getElementTextByTagNameNR(root,"Number_of_Bids"));
		    //bids
		    Element[] bids=getElementsByTagNameNR(root,"Bids");
		    ArrayList<Bid> Bids=new ArrayList<Bid>();
		    for (int i=0;i<Number_of_Bids;i++) {
		    	Element bidder=getElementByTagNameNR(bids[i],"Bidder");
		    	String uid=bidder.getAttribute("UserID");
		    	String rating=bidder.getAttribute("Rating");
		    	Element locationTag=getElementByTagNameNR(bidder,"Location");
		    	String location=getElementText(locationTag);
		    	String latitude=locationTag.getAttribute("Latitute");
		    	String longitude=locationTag.getAttribute("Longitude");
		    	String country=getElementTextByTagNameNR(bidder,"Country");
		    	String time=getElementTextByTagNameNR(bids[i],"Time");
		    	String amount=getElementTextByTagNameNR(bids[i],"Amount");
		    	Bid newBid=new Bid(uid,rating,location,latitude,longitude,country,time,amount);
		    	Bids.add(newBid);
		    }

		    Element locationTag=getElementByTagNameNR(root,"Location");
		    String Location=getElementText(locationTag);
		    String Latitude="";
		    String Longitude="";
		    if (!locationTag.getAttribute("Latitude").isEmpty())
		    	Latitude=locationTag.getAttribute("Latitude");
		    if (!locationTag.getAttribute("Longitude").isEmpty())
		    	Longitude=locationTag.getAttribute("Longitude");

		    String Country=getElementTextByTagNameNR(root,"Country");
		    String Started=getElementTextByTagNameNR(root,"Started");
		    String Ends=getElementTextByTagNameNR(root,"Ends");
		    Element sellerTag=getElementByTagNameNR(root,"Seller");
		    String SellerID=sellerTag.getAttribute("UserID");
		    String SellerRating=sellerTag.getAttribute("Rating");
		    String Description=getElementTextByTagNameNR(root,"Description");

		    //redirect
    		request.setAttribute("Name", Name);
    		request.setAttribute("ItemID",ItemID);
    		request.setAttribute("Categories",Categories);
			request.setAttribute("Currently", Currently);
			request.setAttribute("Buy_Price", Buy_Price);
			request.setAttribute("First_Bid", First_Bid);
			request.setAttribute("Number_of_Bids", Number_of_Bids);
			request.setAttribute("Bids", Bids);
			request.setAttribute("Location",Location);
			request.setAttribute("Latitude",Latitude);
			request.setAttribute("Longitude",Longitude);
			request.setAttribute("Country",Country);
			request.setAttribute("Started", Started);
			request.setAttribute("Ends",Ends);
			request.setAttribute("UserID", SellerID);
			request.setAttribute("Rating", SellerRating);
			request.setAttribute("Description", Description);
			
			request.getRequestDispatcher("/getItemResult.jsp").forward(request,response);
		    


		} catch (SAXException ex) {
			ex.printStackTrace();
		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		}






    }

}
