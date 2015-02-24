package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String q=request.getParameter("q");
    	int numResultsToSkip=Integer.parseInt(request.getParameter("numResultsToSkip"));
    	int numResultsToReturn=Integer.parseInt(request.getParameter("numResultsToReturn"));

    	AuctionSearchClient searchClient = new AuctionSearchClient();
    	SearchResult[] results = searchClient.basicSearch(q,numResultsToSkip,numResultsToReturn);

    	request.setAttribute("results",results);
        request.getRequestDispatcher("/keywordSearchResults.jsp").forward(request,response);
    }
}
