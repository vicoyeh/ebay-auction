package edu.ucla.cs.cs144;

    public class Bid {
        public String BidderID;
        public String BidderRating;
        public String Location;
        public String Latitude;
        public String Longitude;
        public String Country;
        public String Time;
        public String Amount;

        Bid(String uid, String rating, String location,String latitude, String longitude, String country, String time, String amount) {
            BidderID=uid;
	        BidderRating=rating;
	        Location=location;
	        Latitude=latitude;
	        Longitude=longitude;
	        Country=country;
	        Time=time;
	       	Amount=amount;          
        }
    }

