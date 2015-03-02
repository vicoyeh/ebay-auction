<%@ page import="edu.ucla.cs.cs144.*" %>
<%@ page import="java.util.ArrayList" %>

<!DOCTYPE html>
<html lang="en">
<head>
	<!--META BEGIN-->
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	<meta name="keywords" content="ebay, bidding, data, search">
	<!--META END-->
	<title>Item - Ebay</title>
</head>

<body>
<form action="./item" method="GET">
	<div>
		<label>Item ID</label>
		<input name="id" type="text">
	</div>
	<button type="submit">Search</button>
</form>


<h1>ID:<%= request.getAttribute("ItemID") %> Name:<%= request.getAttribute("Name") %></h1>
<h4>Categories</h4>
<%

	ArrayList<String> Categories = (ArrayList<String>) request.getAttribute("Categories");
	String categoryStr="";
%>
<% for (String item: Categories) { 
		categoryStr+=item+", ";
	} 
	categoryStr=categoryStr.substring(0,categoryStr.length()-2);
%>
<p><%=categoryStr%></p>

<h3>Info</h3>
<div>
<p>Currently: <%=request.getAttribute("Currently")%></p>
<p>Buy Price: <%=request.getAttribute("Buy_Price")%></p>
<p>First Bid: <%=request.getAttribute("First_Bid")%></p>
<p>Started: <%=request.getAttribute("Started")%></p>
<p>Ends: <%=request.getAttribute("Ends")%></p>
<p>Description: <%=request.getAttribute("Description")%></p>
</div>
	
<h3>Bids (<%=request.getAttribute("Number_of_Bids")%>)</h3>
<%
ArrayList<Bid> Bids = (ArrayList<Bid>) request.getAttribute("Bids");
%>
<ol>
<% for (Bid item: Bids) { %>
		<li>
			<p><%=item.BidderID%> (Rating: <%=item.BidderRating%>)</p>
			<p>Location: <%=item.Location%> (Lat: <%=item.Latitude%>, Lng: <%=item.Longitude%>)</p>
			<p>Country: <%=item.Country%></p>
			<p>$<%=item.Amount%> at <%=item.Time%></p>
		</li>
<% } %>
</ol>


<h3>Location</h3>
<div>
<p>Location: <%=request.getAttribute("Location")%></p>
<p>Latitude: <%=request.getAttribute("Latitude")%>, Longtitude: <%=request.getAttribute("Longitude")%></p>
<p>Country: <%=request.getAttribute("Country")%></p>
</div>

<h3>Seller Info</h3>
<div>
<p>User ID: <%=request.getAttribute("UserID")%></p>
<p>Rating: <%=request.getAttribute("Rating")%></p>
</div>


</body>
</html>