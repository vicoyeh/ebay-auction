***********************************************************
TEAM: based_vic
**********************************************************
 
TEAM MEMBERS:
Calvin Chan
Kuan-Hsuan Yeh

**********************************************************

##Publish as Web Service
We experienced significant difficulty when trying to deploy our java classes as service archive file in axis2. We followed the instruction on the course website properly but still received the following error when we accessed http://localhost:1448/axis2/services/AuctionSearchService/echo?message=helloThere:

<soapenv:Reason xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">

	<soapenv:Text xml:lang="en-US">unknown</soapenv:Text>

</soapenv:Reason>  

We reinstalled axis2 and restarted tomcat server multiple times, but still could not solve the problem. Throughout the debugging process, we made two posts on Piazza and even emailed TA. The bug remained as we've spent about ten hours on it. After looking through dozens of Stack Overflow posts, we learnt to examine Tomcat server logs and error logs. The bug was found to be permission error due to declaring a subclass in AuctionSearch.java, which returned HTTP 500 error on the server side. We solved the problem by removing the subclass and initilized everything in search functions. If possible, we'd like to get a grace day back because the problem could not be solved on Piazza for over two days and the spec did not provide instruction on examining server side error.


Github Commit History on AuctionSearch.java: 
https://github.com/vic317yeh/ebay-auction/commit/fbcc423a5a619f3df246880f1f4632c03153f665