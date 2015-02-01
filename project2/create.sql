CREATE TABLE Location (
	id INT,
	location VARCHAR(255),
	country VARCHAR(255),
	lat DECIMAL(10,6),
	lng DECIMAL(10,6),
	PRIMARY KEY (id)
);
CREATE TABLE Users (
	id varchar(255),
	location_id INT,
	bidder_rating INT,
	seller_rating INT,
	PRIMARY KEY (id),
	FOREIGN KEY (location_id) REFERENCES Location (id)
	ON DELETE SET NULL
	ON UPDATE CASCADE
);
CREATE TABLE Categories (
	id INT,
	name VARCHAR(255),
	PRIMARY KEY (id)
);
CREATE TABLE Items (
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(255),
	currently DECIMAL(8,2),
	buy_price DECIMAL(8,2),
	first_bid DECIMAL(8,2),
	location_id INT,
	started TIMESTAMP,
	ends TIMESTAMP,
	seller_id varchar(255),
	description VARCHAR(4000),
	PRIMARY KEY (id),
	FOREIGN KEY (seller_id) REFERENCES Users (id)
	ON DELETE CASCADE
	ON UPDATE CASCADE,
	FOREIGN KEY (location_id) REFERENCES Location (id)
	ON DELETE SET NULL
	ON UPDATE CASCADE
);
CREATE TABLE Items_Categories (
	item_id INT,
	category_id INT,
	CONSTRAINT pk_item_category PRIMARY KEY (item_id, category_id),
	FOREIGN KEY (item_id) REFERENCES Items (id)
	ON DELETE CASCADE
	ON UPDATE CASCADE,
	FOREIGN KEY (category_id) REFERENCES Categories (id)
	ON DELETE CASCADE
	ON UPDATE CASCADE
);
CREATE TABLE Bids (
	bidder_id varchar(255),
	time TIMESTAMP,
	amount DECIMAL(8,2),
	item_id INT,
	CONSTRAINT pk_bidder_time_item PRIMARY KEY (bidder_id, time, item_id),
	FOREIGN KEY (item_id) REFERENCES Items (id)
	ON DELETE CASCADE
	ON UPDATE CASCADE,
	FOREIGN KEY (bidder_id) REFERENCES Users (id)
	ON DELETE CASCADE
	ON UPDATE CASCADE
);
