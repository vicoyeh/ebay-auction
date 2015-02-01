SELECT COUNT(*) 
	FROM Users;
SELECT COUNT(*) 
	FROM Items INNER JOIN Location ON location_id = Location.id 
	WHERE Location.location = 'New York';
SELECT COUNT(*) 
	FROM (SELECT COUNT(*) as count FROM Items_Categories GROUP BY item_id) AS auctions
	WHERE count > 4;
SELECT id FROM (
	SELECT id as id, MAX(currently) as max 
		FROM Items
		WHERE ends > '2001-12-20 00:00:01'
		RIGHT OUTER JOIN Bids on id=item_id
);
