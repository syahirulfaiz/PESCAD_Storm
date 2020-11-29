<?php 
//error_reporting(0); 
include 'config.php';
?>

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><?php echo NAMA_APLIKASI; ?></title>
<link rel="stylesheet" href="<?php echo TEMPLATE_URL; ?>print.css" type="text/css" />
<script src="<?php echo TEMPLATE_URL; ?>jquery/jquery.min.js"></script>

<script type="text/javascript">window.location.replace('plot.php')</script>
<meta http-equiv="Refresh" content="0;plot.php">
<link rel="canonical" href="plot.php">
<p><a href="plot.php">plot.php</a></p>


</head>
<body>

<h1><?php echo HOMEPAGE; ?></h1>
<hr>

<div id="mapid"></div>
<script>
var mymap = L.map('mapid').setView([51.505, -0.09], 13);

L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
    maxZoom: 18,
    id: 'mapbox/streets-v11',
    tileSize: 512,
    zoomOffset: -1,
     //COPY YOUR ACCESS TOKEN BELOW!
	accessToken: 'copy_your_access_token_here'
}).addTo(mymap);

var marker1 = L.marker([51.5, -0.09]).addTo(mymap);
marker1.bindPopup("<b>#coronavirusuk</b>").openPopup();

var marker2 = L.marker([52, -0.10]).addTo(mymap);
marker2.bindPopup("<b>52, -0.10</b>").openPopup();

<?php
$sql= "SELECT screen_name,
topic,
status,
latitude,
longitude
FROM collected_tweets
WHERE latitude<>''
and topic<>''
and latitude between '49.186288' and '57.186288' 
limit 10
";
$result = $conn->query($sql);
$data='';
$newline='\r\n';
$iterator=3;
	if ($result->num_rows > 0) {
	/////			output data of each row
				while($row = $result->fetch_assoc()) {
					echo 'var marker'.$iterator.' = L.marker(['.$row['latitude'].', '.$row['longitude'].']).addTo(mymap);
marker'.$iterator.'.bindPopup("<b>'.$row['screen_name'].'</b><br>'.$row['status'].'").openPopup();';
$iterator=$iterator+1;
echo "\r\n";
				}
			}
		
?>

</script>
</body>
</html> 