***********************************************************
TEAM: based_vic
**********************************************************
 
TEAM MEMBERS:
Calvin Chan
Kuan-Hsuan Yeh

**********************************************************

Q1. For which communication(s) do you use the SSL encryption?
A: (4)->(5) and (5)->(6)

Q2. How do you ensure that the item was purchased exactly at the Buy_Price of that particular item?
A: When a user visits an item page, we save the buy price of the item in a http session. Therefore, our servlet can simply retrieve the item price from the session later.
