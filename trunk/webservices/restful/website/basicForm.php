<html>
<head>
<title>A BASIC HTML FORM</title>
</head>
<body>

<table width="800" style="border-style:solid; border-color:#000000" cellpadding="0" cellspacing="0">
<tr>
<td align="center">

<?PHP
if (isset($_POST['Submit'])) {

	$graph = $_POST['graph'];
	$query = $_POST['query'];
	$method = $_POST['method'];

	if ($query == "") {
		print ("You have entered a empty query");
	}
	else {
		$query = urlencode($query);
		$look_for = '%FILENAME%';
		if ($method == "neighbours") {
			$depth = $_POST['depth'];
			if ($depth == "")
				$depth = 1;
			$query = $query."/".$method."/".$depth;
		} else if ($method == "shortestpath") {
			$query = $query."/".$method;
		}
		$change_to = 'http://ondex.rothamsted.ac.uk/graphs/'.$graph.'/query/concepts/'.$query.'/oxl';
		$file_contents = file_get_contents("template.txt");
		print str_replace($look_for, $change_to, $file_contents);
	}
}
?>

</td></tr></table>

<?PHP
if (isset($change_to)) {
	print "<p><a href=\"".$change_to."\">".$change_to."</a>";
}
?>

</body>
</html>
