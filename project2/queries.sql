SELECT COUNT(*) 
    FROM Users;
SELECT COUNT(*) 
    FROM Items INNER JOIN Location ON location_id = Location.id 
    WHERE BINARY Location.location = 'New York';
SELECT COUNT(*) FROM (SELECT COUNT(*) as count FROM Items_Categories GROUP BY item_id) AS auctions
    WHERE count = 4;
SELECT id AS ITEMID FROM (
    SELECT id, amount FROM Items INNER JOIN Bids ON Items.id = Bids.item_id WHERE ends > '2001-12-20 00:00:01' GROUP BY Items.id
) as Max_Items_Bids 
    ORDER BY amount DESC
    LIMIT 1;
SELECT COUNT(*) FROM Users WHERE seller_rating > 1000;
SELECT COUNT(*) FROM Users WHERE seller_rating IS NOT NULL AND bidder_rating IS NOT NULL;
SELECT COUNT(DISTINCT CATEGORY) FROM (
    SELECT category_id AS CATEGORY FROM Items_Categories INNER JOIN (
        SELECT Items.id, MAX(amount) as highest_bid FROM Items INNER JOIN Bids ON Bids.item_id = Items.id WHERE amount > 100 GROUP BY Items.id
    ) as Max_Items_Bids ON Items_Categories.item_id = Max_Items_Bids.id GROUP BY category_id
) as Categories_GT100;
