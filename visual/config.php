 <?php 
//[2] Create connection 

//[2] Create connection  

	$servername = "localhost";
	$username = "root";
	$password = "";
	$dbname = "test";
	
	$conn = new mysqli($servername, $username, $password, $dbname);
	// Check connection
	if ($conn->connect_error) {
		die("Connection failed: " . $conn->connect_error);
	}
		
	
define("BASE_URL",$_SERVER['DOCUMENT_ROOT'].'/visual');//this is for PHP file's include
define("TEMPLATE_URL","/visual/resources/");//this is for html
define("NAMA_APLIKASI",'VISUAL | Real-time Covid-19 Event Stream Detection Using Big Data DAG Model');
//define("HOMEPAGE",'<img src="'.TEMPLATE_URL.'UoL.png" alt="Avatar" style="width:2%; height:5%; padding:0 5px 0 0" ><a href="/visual" target="_parent">'.NAMA_APLIKASI.'</a>');


define("HOMEPAGE",'<img src="'.TEMPLATE_URL.'UoL.png" alt="Avatar" style="width:2%; height:5%; padding:0 5px 0 0" ><a href="/visual" target="_parent">'.NAMA_APLIKASI.'</a><hr/>
<a href="/visual/plot.php">Anomaly Detection</a> | 
<a href="/visual/plot.bar.timing.php">Timing & Throughput</a> |
<a href="/visual/plot.bar.wordcount.php">Word Counts & TF-IDF (Frequency)</a> |
<a href="/visual/plot.scatter.clustering.php">Word2Vec & Clustering</a> |
<a href="/visual/map.php" target="_blank">Anomaly Map</a>
');


	
?> 

