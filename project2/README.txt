***********************************************************
TEAM: based_vic
**********************************************************
 
TEAM MEMBERS:
Calvin Chan
Kuan-Hsuan Yeh

**********************************************************

Part B: Design your relational schema
=====================================
1) List your relations
Relations:
* Items(id, name, currently, buy_price, first_bid, location_id, started, ends, seller_id, description)
	primary key: id
	foreign key: seller_id,
	foreign key: location_id 

* Items_categories(item_id, category_id)
	primary key: (item_id, category_id)
	foreign key: item_id
	foreign key: category_id

* Categories(id, name)
	primary: id 

* Bids(bidder_id, time, amount, item_id)
	primary key: (bidder_id, time, item_id)
	foreign key: item_id
	foreign key: bidder_id

* Users(id, location_id, bidder_rating, seller_rating)
	primary key: id
	foreign key: location_id 

* Location(id, location, country, lng, lat)
	primary key: id 

2) List all completely nontrivial functional dependencies that hold on each relation
Items: 
	none
Items_categories: 
	none
Categories:
	none
Bids:
	none
Users:
	none
Location:
	location -> country
	location -> (lng, lat)

3) Are all of your relations in Boyce-Codd Normal Form (BCNF)? 
Yes, all of the relations are in BCNF. Locaiton is the only relation that has functional depencies on non-effective specified keys. But since the location attribute is a superkey, the condition of BCNF is satisfied.

4) Are all of your relations in Fourth Normal Form (4NF)?
Yes, every multivalue dependencies of the relations have the determinant as a superkey. Specifically, the Items_categories relation specifies a many-to-many relationship which satisfies the multivalue dependent nature of 4NF. 

=====================================


