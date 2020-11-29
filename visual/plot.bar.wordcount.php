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

<canvas id="ChartWordCount" height="60%"></canvas>
<!--<canvas id="ChartTFIDF" height="60%"></canvas>-->


<?php
//WORDCOUNT
$sql= "
select distinct keyword, CAST(word_count AS UNSIGNED) as count, tf_idf
FROM keyword_count
ORDER BY CAST(word_count AS UNSIGNED) DESC, keyword DESC
LIMIT 20
";


$result = $conn->query($sql);
		$label_keyword_word_count = "";
		$data_word_count = "";
		$data_word_count_tf_idf = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$label_keyword_word_count = $label_keyword_word_count."'".$row['keyword']."'".",";
				$data_word_count = $data_word_count."'".$row['count']."'".",";
				$data_word_count_tf_idf = $data_word_count_tf_idf."'".$row['tf_idf']."'".",";
			}
		}		
?>


<?php
//TF-IDF
$sql= "
select distinct keyword, (tf_idf+0.0) as tf_idf, word_count as count
FROM keyword_count
ORDER BY (tf_idf+0.0) DESC, keyword DESC
LIMIT 20
";
$result = $conn->query($sql);
		$label_keyword_tf_idf = "";
		$data_keyword_tf_idf = "";
		$data_keyword_tf_idf_word_count = "";
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$label_keyword_tf_idf = $label_keyword_tf_idf."'".$row['keyword']."'".",";
				$data_keyword_tf_idf = $data_keyword_tf_idf."'".$row['tf_idf']."'".",";
				$data_keyword_tf_idf_word_count = $data_keyword_tf_idf_word_count."'".$row['count']."'".",";
			}
		}		
?>



<script>
//===========WORDCOUNT===================
var ctx = document.getElementById('ChartWordCount');

var chart = new Chart(ctx, {
    // The type of chart we want to create
    type: 'bar',

    // The data for our dataset
    data: {
        labels: [<?php echo $label_keyword_word_count; ?>],
        datasets: [{
            label: 'Word Count',
			backgroundColor: 'rgb(255, 99, 132, 0.6)',
            borderColor: 'rgb(120, 100, 99)',
			borderWidth:2,			
            data: [<?php echo $data_word_count; ?>]
        },
		{
            label: 'TF-IDF',
			backgroundColor: 'rgb(23, 120, 255, 0.6)',
            borderColor: 'rgb(23, 120, 255)',
			borderWidth:2,
            data: [<?php echo $data_word_count_tf_idf; ?>]
        }
		]
    },

    // Configuration options go here
    options: {}
});


//===========TFIDF===================
var ct_tf_idf = document.getElementById('ChartTFIDF');

var chart_tf_idf = new Chart(ct_tf_idf, {
    // The type of chart we want to create
    type: 'bar',

    // The data for our dataset
    data: {
        labels: [<?php echo $label_keyword_tf_idf; ?>],
        datasets: [{
            label: 'TF-IDF',
			backgroundColor: 'rgb(23, 120, 255, 0.6)',
            borderColor: 'rgb(23, 120, 255)',
			borderWidth:2,
            data: [<?php echo $data_keyword_tf_idf; ?>],
			tooltipEvents: ['mousemove', 'touchstart', 'touchmove', 'click']
        },
		{
            label: 'Word Count',
			backgroundColor: 'rgb(255, 99, 132, 0.6)',
            borderColor: 'rgb(120, 100, 99)',
			borderWidth:2,
            data: [<?php echo $data_keyword_tf_idf_word_count; ?>],
			tooltipEvents: ['mousemove', 'touchstart', 'touchmove', 'click']
        }
		]
    },

    // Configuration options go here
    options: {}
});

</script>
