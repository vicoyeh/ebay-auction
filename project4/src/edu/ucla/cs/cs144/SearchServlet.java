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
        String tmpNumResultsToSkip = request.getParameter("numResultsToSkip");
        String tmpNumResultsToReturn = request.getParameter("numResultsToReturn");
    	int numResultsToSkip=0;
        int numResultsToReturn=0;
        if (!tmpNumResultsToSkip.isEmpty())
           numResultsToSkip =Integer.parseInt(tmpNumResultsToSkip);
        if (!tmpNumResultsToReturn.isEmpty())
    	   numResultsToReturn=Integer.parseInt(tmpNumResultsToReturn);



    	AuctionSearchClient searchClient = new AuctionSearchClient();
    	SearchResult[] results = searchClient.basicSearch(q,numResultsToSkip,numResultsToReturn);

    	request.setAttribute("results",results);
        request.setAttribute("q",q);
        request.setAttribute("numResultsToSkip",tmpNumResultsToSkip);
        request.setAttribute("numResultsToReturn",tmpNumResultsToReturn);
        request.getRequestDispatcher("/keywordSearchResults.jsp").forward(request,response);
    }
}
