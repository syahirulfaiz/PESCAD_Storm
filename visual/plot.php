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

<script src="https://cdn.jsdelivr.net/npm/chart.js@2.8.0"></script>

</head>
<body>

<h1><?php echo HOMEPAGE; ?></h1>
<hr>

<canvas id="myChart" height="90%"></canvas>


<?php
//ANOMALY
$sql= "
SELECT 	
DISTINCT	
count(a.id_status) as number_of_tweets,
ct.created_at as month	
FROM	
collected_tweets ct	
LEFT JOIN (SELECT DISTINCT id_status FROM anomaly) a	
ON ct.id_status = a.id_status	
GROUP BY UNIX_TIMESTAMP(created_at) DIV 60	
";
$result = $conn->query($sql);
		$label_anomaly = "";
		$data_anomaly = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$label_anomaly = $label_anomaly."'".$row['month']."'".",";
				$data_anomaly = $data_anomaly."'".$row['number_of_tweets']."'".",";
			}
		}		
?>

<?php
//EVENT
$sql= "
SELECT COUNT(anomaly.id_status) as number_of_tweets, normal.created_at as month
FROM (SELECT * FROM collected_tweets) AS normal
LEFT JOIN (SELECT * FROM collected_tweets WHERE is_event = '1') AS anomaly
ON normal.id_status =  anomaly.id_status
GROUP BY UNIX_TIMESTAMP(normal.created_at) DIV 60
";

$result = $conn->query($sql);
		$label_event = "";
		$data_event = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$label_event = $label_event."'".$row['month']."'".",";
				$data_event = $data_event."'".$row['number_of_tweets']."'".",";
			}
		}		
?>





<?php
//NORMAL
$sql= "
SELECT count(ct.id_status) as number_of_tweets,
ct.created_at as month
FROM collected_tweets ct
GROUP BY 
UNIX_TIMESTAMP(created_at) DIV 60
";
$result = $conn->query($sql);
		$label_normal = "";
		$data_normal = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$label_normal = $label_normal."'".$row['month']."'".",";
				$data_normal = $data_normal."'".$row['number_of_tweets']."'".",";
			}
		}		
?>



<script>
var ctx = document.getElementById('myChart').getContext('2d');
var chart = new Chart(ctx, {
    // The type of chart we want to create
    type: 'line',

    // The data for our dataset
    data: {
        labels: [<?php echo $label_anomaly; ?>],
        datasets: [{
            label: 'Anomaly',
            //backgroundColor: 'rgb(255, 99, 132)',
            borderColor: 'rgb(255, 99, 132)',
            data: [<?php echo $data_anomaly; ?>]
        },
		{
            label: 'Event',
            borderColor: 'rgb(120, 255, 99)',
            data: [<?php echo $data_event; ?>]
        },
		{
            label: 'Normal',
            borderColor: 'rgb(99, 132, 255)',
            data: [<?php echo $data_normal; ?>]
        }
		]
    },

    // Configuration options go here
    options: {}
});
</script>



<?php
//ANOMALY_TABLE
$sql= "
SELECT 	
DISTINCT	
ct.id_status id_status,
ct.screen_name screen_name,
ct.status status,
ct.created_at as time	
FROM	
collected_tweets ct	
INNER JOIN (SELECT DISTINCT id_status FROM anomaly) a	
ON ct.id_status = a.id_status
ORDER BY ct.created_at ASC	
";
$result = $conn->query($sql);
		$table_anomaly_collected_tweets = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$table_anomaly_collected_tweets = $table_anomaly_collected_tweets."<tr><td><a target=_blank href='https://twitter.com/".$row['screen_name']."/status/".$row['id_status']."'>".$row['time']."</a></td>
				<td>".$row['status']."</td></td>";
			}
		}		
?>

<table class="data_grid">
			<thead>
				<tr>
				<th>time</th>
				<th>status</th>
				</tr>
			</thead>
			<tbody>
				<?php echo $table_anomaly_collected_tweets; ?>
			</tbody>
</table>