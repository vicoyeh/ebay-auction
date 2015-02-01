SELECT COUNT(*) 
    FROM Users;
SELECT COUNT(*) 
    FROM Items INNER JOIN Location ON location_id = Location.id 
    WHERE BINARY Location.location = 'New York';
SELECT COUNT(*) FROM (SELECT COUNT(*) as count FROM Items_Categories GROUP BY item_id) AS auctions
    WHERE count = 4;
SELECT id AS ITEMID FROM (
    SELECT id, amount FROM Items INNER JOIN Bids ON Items.id = Bids.item_id WHERE ends > '2001-12-20 00:00:01' GROUP BY Items.id
) as Items_Bids WHERE amount = (
    SELECT MAX(amount) FROM (
        SELECT id, amount FROM Items INNER JOIN Bids ON Items.id = Bids.item_id WHERE ends > '2001-12-20 00:00:01' GROUP BY Items.id
    ) as Max_Bid
);
SELECT COUNT(*) FROM Users WHERE seller_rating > 1000;
SELECT COUNT(*) FROM Users WHERE seller_rating IS NOT NULL AND bidder_rating IS NOT NULL;
SELECT COUNT(DISTINCT category_id) AS 'COUNT(DISTINCT CATEGORY)' FROM (
    SELECT category_id FROM Items_Categories INNER JOIN (
        SELECT * FROM (
            SELECT Items.id, MAX(amount) as highest_bid FROM Items INNER JOIN Bids ON Bids.item_id = Items.id GROUP BY Items.id
        ) AS Items_Bids WHERE Items_Bids.highest_bid > 100
    ) as Max_Items_Bids ON Items_Categories.item_id = Max_Items_Bids.id GROUP BY category_id
) as Items_Cats;
