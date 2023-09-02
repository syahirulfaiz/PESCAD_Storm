# PESCAD_Storm

<b>Big Data Directed Acyclic Graph Model for Covid-19 Event Stream Detection ([Publication](https://doi.org/10.1016/j.patcog.2021.108404)) <br/></b> 
The aim of this work is to detect anomalous events associated with COVID-19 from Twitter. To this end, we propose a distributed Directed Acyclic Graph topology framework to aggregate and process large-scale real-time tweets related to COVID-19. The core of our system is a novel lightweight algorithm that can automatically detect anomaly events.


## Using: Poisson Event Stream Collective Anomaly Detection (PESCAD) Algorithm ##

----------------------------------------------------------------
### APACHE STORM : ###

In Spout.java, please configure these lines:<br/>

String consumerKey = "PUT YOUR consumerKey HERE";<br/>
String consumerSecret = "PUT YOUR consumerSecret HERE";<br/>
String accessToken = "PUT YOUR accessToken HERE";<br/>
String accessTokenSecret = "PUT YOUR accessTokenSecret HERE";<br/>

### VISUAL: ###
In map.php, please configure these lines:<br/>

accessToken: 'copy_your_access_token_here'<br/>
'access_key' => 'COPY_YOUR_ACCESS_KEY_HERE',<br/>

---------------------------------------------------------------

Import the "Apache Storm" as a maven project.<br/>

Import the "test.sql" into a MySQL DB named "test".<br/>

Put the "visual" folder in your www or htdocs directory.<br/>
