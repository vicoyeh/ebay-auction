package edu.ucla.cs.cs144;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

    public class Bid implements Comparable<Bid> {
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

          @Override
          public int compareTo(Bid o) {
        
            SimpleDateFormat format =
                new SimpleDateFormat("MMM-dd-yy HH:mm:ss");

            try {
                Date d1 = format.parse(Time);
                Date d2 = format.parse(o.Time);
                return d2.compareTo(d1);
            }
            catch(ParseException pe) {
                pe.printStackTrace();
            }


            return o.Time.compareTo(Time);
          }
    }

