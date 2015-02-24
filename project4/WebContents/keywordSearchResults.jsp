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
</head>

<body>

	
<%
	SearchResult[] results = (SearchResult[])request.getAttribute("results");
%>
	
	
<div>
	<h1>Results</h1>
	<ul>
		<% for (SearchResult item: results) { %>
			<li>ID:<%= item.getItemId() %> Name:<%= item.getName()%></li>	
		<% } %>
	</ul>
</div>

</body>
</html>