# PESCAD_Storm
Big Data Directed Acyclic Graph Model for Covid-19 Event Stream Detection<br/>

*** Using: Poisson Event Stream Collective Anomaly Detection (PESCAD) Algorithm***<br/>

----------------------------------------------------------------
*** APACHE STORM : ***<br/>
In Spout.java, please configure these lines:<br/>

String consumerKey = "PUT YOUR consumerKey HERE";<br/>
String consumerSecret = "PUT YOUR consumerSecret HERE";<br/>
String accessToken = "PUT YOUR accessToken HERE";<br/>
String accessTokenSecret = "PUT YOUR accessTokenSecret HERE";<br/>

*** VISUAL: ***<br/>
In map.php, please configure these lines:<br/>

accessToken: 'copy_your_access_token_here'<br/>
'access_key' => 'COPY_YOUR_ACCESS_KEY_HERE',<br/>

---------------------------------------------------------------

Import the "Apache Storm" as a maven project.<br/>

Import the "test.sql" into a MySQL DB named "test".<br/>

Put the "visual" folder in your www or htdocs directory.<br/>
