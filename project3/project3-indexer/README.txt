***********************************************************
TEAM: based_vic
**********************************************************
 
TEAM MEMBERS:
Calvin Chan
Kuan-Hsuan Yeh

**********************************************************


##Indexer
We decided to create Lucene indexes on item_id, item_name, and content (item_id+item_name+category+description). Since the goal is to efficiently perform keyword search on item name, category, and description, building Lucene indexes on these attributes will faciliate the search performace. However, only the item id and item name will be stored.