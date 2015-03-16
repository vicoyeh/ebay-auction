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
	<title>Confirmation - Ebay</title>

</head>
<body>
<h1>Your transaction is complete!</h1>
<p>ID: <%= request.getAttribute("ItemID")%></p> 
<p>Name: <%= request.getAttribute("Name")%></p>
<p>Buy Price: <%= request.getAttribute("Buy_Price")%></p>
<p>Credit card number: <%= request.getAttribute("card")%></p>
<p>Time: <%= request.getAttribute("time")%></p>

</body>
</html>