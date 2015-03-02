<%@ page import="edu.ucla.cs.cs144.SearchResult" %>
<!DOCTYPE html>
<html lang="en">
<head>
	<!--META BEGIN-->
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	<meta name="keywords" content="ebay, bidding, data, search">
	<!--META END-->
	<title>Search Results - Ebay</title>

        <script type="text/javascript" src="autosuggest.js"></script>
        <script type="text/javascript" src="suggestions.js"></script>
        <link rel="stylesheet" type="text/css" href="autosuggest.css" /> 
	<script type="text/javascript">
        window.onload = function () {
           var oTextbox = new AutoSuggestControl(document.getElementById("txtBox"), new StateSuggestions()); 
        }
    </script>


</head>

<body>
<form action="./search" method="GET">
	<div>
		<label>Keywords</label>
		<input id="txtBox" name="q" type="text">
	</div>
	<input name="numResultsToSkip" value="0" type="hidden">
	<input name="numResultsToReturn" value="20" type="hidden">
	<button type="submit">Search</button>
</form>
	
<%
	SearchResult[] results = (SearchResult[])request.getAttribute("results");
%>
	
	
<div>
	<h1>Results</h1>
	<ul>
		<% for (SearchResult item: results) { %>
			<a href="/eBay/item?id=<%=item.getItemId()%>"><li>ID:<%= item.getItemId() %> Name:<%= item.getName()%></li></a>	
		<% } %>
	</ul>
</div>

<div>
<%
	String q=(String)request.getAttribute("q");
	int numResultsToSkip=Integer.parseInt((String)request.getAttribute("numResultsToSkip"));
	int numResultsToReturn=Integer.parseInt((String)request.getAttribute("numResultsToReturn"));

	int skip=numResultsToSkip-20;
	if (numResultsToSkip-20<0)
		skip=0;


 if (numResultsToSkip>0) { %>
	<a href="/eBay/search?q=<%= q %>&numResultsToSkip=<%= skip %>&numResultsToReturn=20">Previous</a> 
<% } %>

<% if (results.length==numResultsToReturn) { %>
	<a href="/eBay/search?q=<%= q %>&numResultsToSkip=<%= numResultsToSkip+20 %>&numResultsToReturn=20">Next</a>
<% } %>
</div>

</body>
</html>