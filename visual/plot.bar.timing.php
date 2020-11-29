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

<canvas id="ChartStackedTimingThroughput" height="40%"></canvas>

<canvas id="ChartStackedTimingProcessedTuple" height="40%"></canvas>
<canvas id="ChartStackedTimingElapsedTime" height="40%"></canvas>



<?php
////MACHINE
// $sql= "
// SELECT from_machine_name
// FROM
// (SELECT distinct from_machine_name FROM anomaly
// UNION
// SELECT distinct from_machine_name FROM collected_tweets
// UNION
// SELECT distinct from_machine_name FROM keyword_cluster
// UNION
// SELECT distinct from_machine_name FROM keyword_count) AS all_machine 
// ORDER BY from_machine_name ASC 
// ";

$sql= "
SELECT DISTINCT from_machine_name
FROM collected_tweets
ORDER BY from_machine_name ASC 
";

$result = $conn->query($sql);
		$label_all_machine = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$label_all_machine = $label_all_machine."'".$row['from_machine_name']."'".",";
			}
		}

?>


<div id="PROCESSED_TUPLE">
<?php
//PROCESSED_TUPLE_PREPROCESSING_BOLT
$sql= "
SELECT COUNT(elapsed_time) as processed_tuple, from_machine_name 
FROM collected_tweets 
GROUP BY from_machine_name
ORDER BY from_machine_name ASC
";
$result = $conn->query($sql);
		$processed_tuple_PreprocessingBolt = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$processed_tuple_PreprocessingBolt = $processed_tuple_PreprocessingBolt."'".$row['processed_tuple']."'".",";
			}
		}

?>

<?php
//PROCESSED_TUPLE_TFIDFBolt
$sql= "
SELECT COUNT(elapsed_time) as processed_tuple, from_machine_name 
FROM keyword_count 
GROUP BY from_machine_name
ORDER BY from_machine_name ASC
";
$result = $conn->query($sql);
		$processed_tuple_TFIDFBolt = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$processed_tuple_TFIDFBolt = $processed_tuple_TFIDFBolt."'".$row['processed_tuple']."'".",";
			}
		}

?>

<?php
//PROCESSED_TUPLE_PESCADBolt
$sql= "
SELECT COUNT(elapsed_time) as processed_tuple, from_machine_name 
FROM anomaly 
GROUP BY from_machine_name
ORDER BY from_machine_name ASC
";
$result = $conn->query($sql);
		$processed_tuple_PESCADBolt = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$processed_tuple_PESCADBolt = $processed_tuple_PESCADBolt."'".$row['processed_tuple']."'".",";
			}
		}

?>

<?php
//PROCESSED_TUPLE_ClusteringBolt
$sql= "
SELECT COUNT(elapsed_time) as processed_tuple, from_machine_name 
FROM keyword_cluster 
GROUP BY from_machine_name
ORDER BY from_machine_name ASC
";
$result = $conn->query($sql);
		$processed_tuple_ClusteringBolt = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$processed_tuple_ClusteringBolt = $processed_tuple_ClusteringBolt."'".$row['processed_tuple']."'".",";
			}
		}

?>

</div>


<script>
//PROCESSED TUPLE======================================
		var barChartDataProcessedTuple = {
			labels: [<?php echo $label_all_machine; ?>],		
			datasets: [{
				label: 'PreprocessingBolt',
				backgroundColor: "#ff0000",
				data: [<?php echo $processed_tuple_PreprocessingBolt; ?>]
				}, 
				{
				label: 'TFIDFBolt',
				backgroundColor: "#00ff00",
				data: [<?php echo $processed_tuple_TFIDFBolt; ?>]
				}, 
				{
				label: 'PESCADBolt',
				backgroundColor: "#0000ff",
				data: [<?php echo $processed_tuple_PESCADBolt; ?>]
				},
				{
				label: 'ClusteringBolt',
				backgroundColor: "#ffff00",
				data: [<?php echo $processed_tuple_ClusteringBolt; ?>]
				}]

		};
			var ct_processed_tuple = document.getElementById('ChartStackedTimingProcessedTuple').getContext('2d');
			var chart_processed_tuple = new Chart(ct_processed_tuple, {
				type: 'bar',
				data: barChartDataProcessedTuple,
				options: {
					title: {
						display: true,
						text: 'Processed Tuples in Each Machine (tuples)'
					},
					tooltips: {
						mode: 'index',
						intersect: false
					},
					responsive: true,
					scales: {
						xAxes: [{
							stacked: true,
						}],
						yAxes: [{
							stacked: true
						}]
					}
				}
			});

</script>



<div id="ELAPSED_TIME">
<?php
//ELAPSED_TIME_PREPROCESSING_BOLT
$sql= "
SELECT SUM(elapsed_time) as elapsed_time, from_machine_name 
FROM collected_tweets 
GROUP BY from_machine_name
ORDER BY from_machine_name ASC
";
$result = $conn->query($sql);
		$elapsed_time_PreprocessingBolt = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$elapsed_time_PreprocessingBolt = $elapsed_time_PreprocessingBolt."'".$row['elapsed_time']."'".",";
			}
		}

?>


<?php
//ELAPSED_TIME_TFIDFBolt
$sql= "
SELECT SUM(elapsed_time) as elapsed_time, from_machine_name 
FROM keyword_count 
GROUP BY from_machine_name
ORDER BY from_machine_name ASC
";
$result = $conn->query($sql);
		$elapsed_time_TFIDFBolt = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$elapsed_time_TFIDFBolt = $elapsed_time_TFIDFBolt."'".$row['elapsed_time']."'".",";
			}
		}

?>


<?php
//ELAPSED_TIME_PESCADBolt
$sql= "
SELECT SUM(elapsed_time) as elapsed_time, from_machine_name 
FROM anomaly 
GROUP BY from_machine_name
ORDER BY from_machine_name ASC
";
$result = $conn->query($sql);
		$elapsed_time_PESCADBolt = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$elapsed_time_PESCADBolt = $elapsed_time_PESCADBolt."'".$row['elapsed_time']."'".",";
			}
		}

?>

<?php
//ELAPSED_TIME_ClusteringBolt
$sql= "
SELECT SUM(elapsed_time) as elapsed_time, from_machine_name 
FROM keyword_cluster
GROUP BY from_machine_name
ORDER BY from_machine_name ASC
";
$result = $conn->query($sql);
		$elapsed_time_ClusteringBolt = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$elapsed_time_ClusteringBolt = $elapsed_time_ClusteringBolt."'".$row['elapsed_time']."'".",";
			}
		}

?>
</div>



<script>
//ELAPSED TIME======================================

		var barChartDataElapsedTime = {
			labels: [<?php echo $label_all_machine; ?>],		
			datasets: [{
				label: 'PreprocessingBolt',
				backgroundColor: "#ff0000",
				data: [<?php echo $elapsed_time_PreprocessingBolt; ?>]
				}, 
				{
				label: 'TFIDFBolt',
				backgroundColor: "#00ff00",
				data: [<?php echo $elapsed_time_TFIDFBolt; ?>]
				}, 
				{
				label: 'PESCADBolt',
				backgroundColor: "#0000ff",
				data: [<?php echo $elapsed_time_PESCADBolt; ?>]
				},
				{
				label: 'ClusteringBolt',
				backgroundColor: "#ffff00",
				data: [<?php echo $elapsed_time_ClusteringBolt; ?>]
				}]

		};
		
			var ct_elapsed_time = document.getElementById('ChartStackedTimingElapsedTime').getContext('2d');
			var chart_elapsed_time = new Chart(ct_elapsed_time, {
				type: 'bar',
				data: barChartDataElapsedTime,
				options: {
					title: {
						display: true,
						text: 'Elapsed Time Tweet Processing in Each Machine (seconds)'
					},
					tooltips: {
						mode: 'index',
						intersect: false
					},
					responsive: true,
					scales: {
						xAxes: [{
							stacked: true,
						}],
						yAxes: [{
							stacked: true
						}]
					}
				}
			});
</script>



<div id="THROUGHPUT">
<?php
//THROUGHPUT_PREPROCESSING_BOLT
$sql= "
SELECT COUNT(elapsed_time) / SUM(elapsed_time) as throughput, from_machine_name 
FROM collected_tweets 
GROUP BY from_machine_name
ORDER BY from_machine_name ASC
";
$result = $conn->query($sql);
		$throughput_PreprocessingBolt = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$throughput_PreprocessingBolt = $throughput_PreprocessingBolt."'".$row['throughput']."'".",";
			}
		}

?>


<?php
//THROUGHPUT_TFIDFBOLT
$sql= "
SELECT COUNT(elapsed_time) / SUM(elapsed_time) as throughput, from_machine_name 
FROM keyword_count 
GROUP BY from_machine_name
ORDER BY from_machine_name ASC
";
$result = $conn->query($sql);
		$throughput_TFIDFBolt = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$throughput_TFIDFBolt = $throughput_TFIDFBolt."'".$row['throughput']."'".",";
			}
		}

?>



<?php
//THROUGHPUT_PESCADBOLT
$sql= "
SELECT COUNT(elapsed_time) / SUM(elapsed_time) as throughput, from_machine_name 
FROM anomaly
GROUP BY from_machine_name
ORDER BY from_machine_name ASC
";
$result = $conn->query($sql);
		$throughput_PESCADBolt = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$throughput_PESCADBolt = $throughput_PESCADBolt."'".$row['throughput']."'".",";
			}
		}

?>

<?php
//THROUGHPUT_CLUSTERINGBOLT
$sql= "
SELECT COUNT(elapsed_time) / SUM(elapsed_time) as throughput, from_machine_name 
FROM keyword_cluster
GROUP BY from_machine_name
ORDER BY from_machine_name ASC
";
$result = $conn->query($sql);
		$throughput_ClusteringBolt = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$throughput_ClusteringBolt = $throughput_ClusteringBolt."'".$row['throughput']."'".",";
			}
		}

?>
</div>


<script>
//THROUGPUT======================================

		var barChartDataThroughput = {
			labels: [<?php echo $label_all_machine; ?>],		
			datasets: [{
				label: 'PreprocessingBolt',
				backgroundColor: "#ff0000",
				data: [<?php echo $throughput_PreprocessingBolt; ?>]
				}, 
				{
				label: 'TFIDFBolt',
				backgroundColor: "#00ff00",
				data: [<?php echo $throughput_TFIDFBolt; ?>]
				}, 
				{
				label: 'PESCADBolt',
				backgroundColor: "#0000ff",
				data: [<?php echo $throughput_PESCADBolt; ?>]
				},
				{
				label: 'ClusteringBolt',
				backgroundColor: "#ffff00",
				data: [<?php echo $throughput_ClusteringBolt; ?>]
				}]

		};
		
			var ct_throughput = document.getElementById('ChartStackedTimingThroughput').getContext('2d');
			var chart_throughput = new Chart(ct_throughput, {
				type: 'bar',
				data: barChartDataThroughput,
				options: {
					title: {
						display: true,
						text: 'Throughput in Each Machine (tuples/seconds)'
					},
					tooltips: {
						mode: 'index',
						intersect: false
					},
					responsive: true,
					scales: {
						xAxes: [{
							stacked: true,
						}],
						yAxes: [{
							stacked: true
						}]
					}
				}
			});
</script>