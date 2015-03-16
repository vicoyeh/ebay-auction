package edu.ucla.cs.cs144;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.net.*;
import java.io.*;

public class TransactionServlet extends HttpServlet implements Servlet {
       
    public TransactionServlet() {}

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession(true);
        Item cur_item = (Item)session.getAttribute("cur_item");
        if (cur_item == null) {
            String url = "http://"+request.getServerName()+":1448"+request.getContextPath()+"/keywordSearch.html";
            response.sendRedirect(url);
            
        }   
        else {          
            request.setAttribute("Name", cur_item.Name);
            request.setAttribute("ItemID",cur_item.Id);
            request.setAttribute("Buy_Price",cur_item.Buy_Price);
            String card = (String) request.getParameter("card");
            request.setAttribute("card", card);
            Date date=new Date();
            request.setAttribute("time",date.toString());

            request.getRequestDispatcher("/confirmation.jsp").forward(request,response);
        }
    }
}
