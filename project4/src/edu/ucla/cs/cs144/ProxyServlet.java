package edu.ucla.cs.cs144;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.*;
import java.io.*;

public class ProxyServlet extends HttpServlet implements Servlet {
       
    public ProxyServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String query = URLEncoder.encode(request.getParameter("q"),"UTF-8");
        String urlStr = "http://google.com/complete/search?output=toolbar&q="+query;
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        response.setContentType("text/xml");
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		StringBuffer sb = new StringBuffer();
 
		while ((line = in.readLine()) != null) {
			sb.append(line);
		}
		in.close();

		//out
		PrintWriter pw = response.getWriter();
		pw.println(sb.toString());

    }
}
