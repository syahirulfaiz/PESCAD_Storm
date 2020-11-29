<?php 
//error_reporting(0); 
include 'config.php';
?>

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8; width=device-width, initial-scale=1" name="viewport" />
<title><?php echo NAMA_APLIKASI; ?></title>
<link rel="stylesheet" href="<?php echo TEMPLATE_URL; ?>print.css" type="text/css" />
<script src="<?php echo TEMPLATE_URL; ?>jquery/jquery.min.js"></script>

<script src="https://cdn.jsdelivr.net/npm/chart.js@2.8.0"></script>


<style>
* {
  box-sizing: border-box;
}

/* Create four equal columns that floats next to each other */
.column {
  float: left;
  width: 20%;
  padding: 10px;
  height: 300px; /* Should be removed. Only for demonstration */
}

/* Clear floats after the columns */
.row:after {
  content: "";
  display: table;
  clear: both;
}
</style>


</head>
<body>

<h1><?php echo HOMEPAGE; ?></h1>
<hr>

<?php
//CLUSTER POINTS
$sql= "
SELECT 
CASE 
	WHEN cluster.no = '1' THEN '#ff0000'
	WHEN cluster.no = '2' THEN '#00ff00'
	WHEN cluster.no = '3' THEN '#0000ff'
	WHEN cluster.no = '4' THEN '#00FFFF'
	WHEN cluster.no = '5' THEN '#FF00FF'
	WHEN cluster.no = '6' THEN '#FFFF00'
END AS color,
CONCAT('Cluster-',cluster.no,':',kc.keyword) as label,
CONCAT('{x:',CAST(substring_index(kc.point_keyword,',',1) AS DECIMAL(3,3)),', y:',CAST(substring_index(kc.point_keyword,',',-1) AS DECIMAL(3,3)),'}') as coordinate
FROM
(
SELECT c.*,
(@rownum := @rownum +1) AS no
FROM (select distinct id_cluster FROM keyword_cluster) c,
(SELECT @rownum:=0) r
) AS cluster,
keyword_cluster kc
WHERE cluster.id_cluster = kc.id_cluster

";
$result = $conn->query($sql);
		$color_point = '';
		$label_point = '';
		$coordinate_point = '';
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$color_point = $color_point.'"'.$row["color"].'"'.',';
				$label_point = $label_point.'"'.$row["label"].'"'.',';
				$coordinate_point = $coordinate_point.$row["coordinate"].',';
			}
		}		
?>

<canvas id="chartjs-1" height="60%"></canvas>

<script>
new Chart(document.getElementById("chartjs-1"),
{"type":"scatter",
"data":{"labels":[<?php echo $label_point; ?>],
"datasets":
[{"label":"",
"data": [<?php echo $coordinate_point; ?>],
"fill":false,
"backgroundColor":[<?php echo $color_point; ?>],
"borderColor":[<?php echo $color_point; ?>],
"borderWidth":1}]},
"options": {
      "tooltips": {
         "callbacks": {
            "label": function(tooltipItem, data) {
               var label = data.labels[tooltipItem.index];
               return label + ': (' + tooltipItem.xLabel + ', ' + tooltipItem.yLabel + ')';
            }
         }
      },
	  "title": {
						"display": true,
						"text": 'Word Cluster'
					},
	"legend": {
            "display": false
         },
		 "scales": {
            "yAxes": [{
                "ticks": {
                    "suggestedMin": -0.01,
                    "suggestedMax": 0.01
                }
            }],
			"xAxes": [{
                "ticks": {
                    "suggestedMin": -1.25,
                    "suggestedMax": 1.25
                }
            }],
        }
   }
});

</script>


<!--==========================TABLE=====================-->

<?php
//CLUSTER_1
$sql="
SELECT 
kc.keyword as label, 
CAST(kcount.word_count AS UNSIGNED) as count
FROM
(
SELECT c.*,
(@rownum := @rownum +1) AS no
FROM (select distinct id_cluster FROM keyword_cluster) c,
(SELECT @rownum:=0) r
) AS cluster,
keyword_cluster kc, keyword_count kcount
WHERE cluster.id_cluster = kc.id_cluster AND kc.keyword = kcount.keyword 
AND cluster.no = '1'
ORDER BY CAST(kcount.word_count AS UNSIGNED) DESC, kc.keyword DESC
";

$result = $conn->query($sql);
		$label_point_cluster1 = '';
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$label_point_cluster1 = $label_point_cluster1.'<tr><td>'.$row["label"].'</td></tr>';
			}
		}		
?>


<?php
//CLUSTER_2
$sql="
SELECT 
kc.keyword as label, 
CAST(kcount.word_count AS UNSIGNED) as count
FROM
(
SELECT c.*,
(@rownum := @rownum +1) AS no
FROM (select distinct id_cluster FROM keyword_cluster) c,
(SELECT @rownum:=0) r
) AS cluster,
keyword_cluster kc, keyword_count kcount
WHERE cluster.id_cluster = kc.id_cluster AND kc.keyword = kcount.keyword 
AND cluster.no = '2'
ORDER BY CAST(kcount.word_count AS UNSIGNED) DESC, kc.keyword DESC
";

$result = $conn->query($sql);
		$label_point_cluster2 = '';
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$label_point_cluster2 = $label_point_cluster2.'<tr><td>'.$row["label"].'</td></tr>';
			}
		}		
?>


<?php
//CLUSTER_3
$sql="
SELECT 
kc.keyword as label, 
CAST(kcount.word_count AS UNSIGNED) as count
FROM
(
SELECT c.*,
(@rownum := @rownum +1) AS no
FROM (select distinct id_cluster FROM keyword_cluster) c,
(SELECT @rownum:=0) r
) AS cluster,
keyword_cluster kc, keyword_count kcount
WHERE cluster.id_cluster = kc.id_cluster AND kc.keyword = kcount.keyword 
AND cluster.no = '3'
ORDER BY CAST(kcount.word_count AS UNSIGNED) DESC, kc.keyword DESC
";

$result = $conn->query($sql);
		$label_point_cluster3 = '';
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$label_point_cluster3 = $label_point_cluster3.'<tr><td>'.$row["label"].'</td></tr>';
			}
		}		
?>


<?php
//CLUSTER_4
$sql="
SELECT 
kc.keyword as label, 
CAST(kcount.word_count AS UNSIGNED) as count
FROM
(
SELECT c.*,
(@rownum := @rownum +1) AS no
FROM (select distinct id_cluster FROM keyword_cluster) c,
(SELECT @rownum:=0) r
) AS cluster,
keyword_cluster kc, keyword_count kcount
WHERE cluster.id_cluster = kc.id_cluster AND kc.keyword = kcount.keyword 
AND cluster.no = '4'
ORDER BY CAST(kcount.word_count AS UNSIGNED) DESC, kc.keyword DESC
";

$result = $conn->query($sql);
		$label_point_cluster4 = '';
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$label_point_cluster4 = $label_point_cluster4.'<tr><td>'.$row["label"].'</td></tr>';
			}
		}		
?>


<?php
//CLUSTER_5
$sql="
SELECT 
kc.keyword as label, 
CAST(kcount.word_count AS UNSIGNED) as count
FROM
(
SELECT c.*,
(@rownum := @rownum +1) AS no
FROM (select distinct id_cluster FROM keyword_cluster) c,
(SELECT @rownum:=0) r
) AS cluster,
keyword_cluster kc, keyword_count kcount
WHERE cluster.id_cluster = kc.id_cluster AND kc.keyword = kcount.keyword 
AND cluster.no = '5'
ORDER BY CAST(kcount.word_count AS UNSIGNED) DESC, kc.keyword DESC
";

$result = $conn->query($sql);
		$label_point_cluster5 = '';
		if ($result->num_rows > 0) {
			while($row = $result->fetch_assoc()) {
				$label_point_cluster5 = $label_point_cluster5.'<tr><td>'.$row["label"].'</td></tr>';
			}
		}		
?>



<div class="row">
  <div class="column" >
    <table class="data_grid">
			<thead>
				<tr>
				<th>cluster_1</th>
				</tr>
			</thead>
			<tbody>
				<?php echo $label_point_cluster1; ?>
			</tbody>
		</table>
  </div>
  <div class="column" >
    <table class="data_grid">
			<thead>
				<tr>
				<th>cluster_2</th>
				</tr>
			</thead>
			<tbody>
				<?php echo $label_point_cluster2; ?>
			</tbody>
		</table>
  </div>
  <div class="column" >
     <table class="data_grid">
			<thead>
				<tr>
				<th>cluster_3</th>
				</tr>
			</thead>
			<tbody>
				<?php echo $label_point_cluster3; ?>
			</tbody>
		</table>
  </div>
  <div class="column" >
    <table class="data_grid">
			<thead>
				<tr>
				<th>cluster_4</th>
				</tr>
			</thead>
			<tbody>
				<?php echo $label_point_cluster4; ?>
			</tbody>
		</table>
  </div>
   <div class="column" >
    <table class="data_grid">
			<thead>
				<tr>
				<th>cluster_5</th>
				</tr>
			</thead>
			<tbody>
				<?php echo $label_point_cluster5; ?>
			</tbody>
		</table>
  </div>
</div> 






