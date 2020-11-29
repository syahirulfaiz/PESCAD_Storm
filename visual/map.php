<?php 
//error_reporting(0); 
include 'config.php';
set_time_limit(500); // 
?>

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><?php echo NAMA_APLIKASI; ?></title>
<link rel="stylesheet" href="<?php echo TEMPLATE_URL; ?>print.css" type="text/css" />
<script src="<?php echo TEMPLATE_URL; ?>jquery/jquery.min.js"></script>

<link rel="stylesheet" href="https://unpkg.com/leaflet@1.6.0/dist/leaflet.css"
   integrity="sha512-xwE/Az9zrjBIphAcBb3F6JVqxf46+CDLwfLMHloNu6KEQCAWi6HcDUbeOfBIptF7tcCzusKFjFw2yuvEpDL9wQ=="
   crossorigin=""/>
<script src="https://unpkg.com/leaflet@1.6.0/dist/leaflet.js"
   integrity="sha512-gZwIG9x3wUXg2hdXF6+rVkLF/0Vi9U8D2Ntg4Ga5I5BZpVkVxlJWbSQtXPSiUTtC0TjtGOmxa1AJPuV0CPthew=="
   crossorigin=""></script>
<style>
#mapid { height: 512px; }
</style>


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

<?php

function forwardGeocoding($place, $latitude, $longitude) {
  $latitude_longitude = "0,0";
  if ($latitude!=""){
	  $latitude_longitude = $latitude.', '.$longitude;
  }else{
	  $data = array(
		  'access_key' => 'COPY_YOUR_ACCESS_KEY_HERE',
		  'query' => $place,
		  'output' => 'json',
		  'limit' => 1,
		);
		$queryString = http_build_query($data);
		$ch = curl_init(sprintf('%s?%s', 'http://api.positionstack.com/v1/forward', $queryString));
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
		$json = curl_exec($ch);
		curl_close($ch);
		$apiResult = json_decode($json, true);
		
		if(isset($apiResult['data'][0]['latitude'])){
			$latitude = $apiResult['data'][0]['latitude'];
			$longitude = $apiResult['data'][0]['longitude'];
			$latitude_longitude = $latitude.', '.$longitude;
		}
  }
  return $latitude_longitude;
}


$sql="
SELECT DISTINCT 
ct.id_status as id_status,
ct.screen_name as screen_name,
ct.topic as topic,
ct.status as status,
ct.latitude as latitude,
ct.longitude as longitude,
ct.place as place
FROM collected_tweets ct, 
anomaly a
WHERE ct.id_status = a.id_status AND place <>'null' 
";

$result = $conn->query($sql);
$data='';
$newline='\r\n';
$iterator=1;
	if ($result->num_rows > 0) {
	/////			output data of each row
				while($row = $result->fetch_assoc()) {
					
					//echo 'var marker'.$iterator.' = L.marker(['.$row['latitude'].', '.$row['longitude'].']).addTo(mymap);
					
					echo 'var marker'.$iterator.' = L.marker(['.forwardGeocoding($row['place'], $row['latitude'], $row['longitude']).']).addTo(mymap);
marker'.$iterator.'.bindPopup("<b><a href=http://twitter.com/'.$row['screen_name'].'/status/'.$row['id_status'].' target=_blank>'.$row['screen_name'].'</a></b><br>'.$row['status'].'").openPopup();';
$iterator=$iterator+1;
echo "\r\n";
				}
			}
		
?>

</script>
</body>
</html> 