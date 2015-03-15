<!DOCTYPE html>
<html lang="en">
<head>
	<!--META BEGIN-->
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	<meta name="keywords" content="ebay, bidding, data, search">
	<!--META END-->
	<title>Pay Now - Ebay</title>

</head>
<body>

<p>ID: <%= request.getAttribute("ItemID")%></p> 
<p>Name: <%= request.getAttribute("Name")%></p>
<p>Buy Price: <%= request.getAttribute("Buy_Price")%></p>
<form action="" method="POST">
	<label>Credit card number</label>
	<input name="card" type="text">
	<button type="submit">Submit</button>
</form>

</body>
</html>
