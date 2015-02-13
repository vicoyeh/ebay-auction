CREATE TABLE IF NOT EXISTS LocIndex (
	itemId INT,
	location POINT NOT NULL, SPATIAL INDEX(location) 
) ENGINE=MyISAM;
INSERT INTO LocIndex (itemId,location)
	SELECT Items.id, Point(IFNULL(Location.lat,0),IFNULL(Location.lng,0)) 
		FROM Items INNER JOIN Location
		ON Items.location_id=Location.id;