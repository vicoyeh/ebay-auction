***********************************************************
TEAM: based_vic
**********************************************************
 
TEAM MEMBERS:
Calvin Chan
Kuan-Hsuan Yeh

**********************************************************

We decided to create Lucene indexes on id, name, and description attributes of the Item table and id and name attributes of the Categories table. Since the goal is to efficiently perform keyword search on item name, category, and description, building Lucene indexes on these attributes will faciliate the search performace. However, only the values of ids and item name will be stored.