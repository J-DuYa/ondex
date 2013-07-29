<html>
<head>
<title>OndexWeb Test</title>
</head>
<body>
<h3>OndexWeb Test</h3>

<?php

	$webservice = $_REQUEST['webservice'];
	$graphname = $_REQUEST['graphname'];
	
	if ($webservice=="") 
	{ 
		echo "<p>I assume you know what you are doing, so enter the details below.</p>\n";
		echo "<form name=\"form\" method=\"post\" action=\"webservice.php\">\n";
		echo "WSDL URL: <input type=\"text\" name=\"webservice\" value=\"http://rpc274.cs.man.ac.uk:8080/ondex/services/ondex-graph?wsdl\">\n";
		echo "Graphname: <input type=\"text\" name=\"graphname\" value=\"phibase_Bcinerea_filtered\">\n";
		echo "<input type=\"submit\" name=\"Submit\" value=\"Submit\"><input type=\"reset\" value=\"Reset Form\">\n";
		echo "</form>\n";
	} else {
		print("
		<applet code=\"net.sourceforge.ondex.ovtk2lite.Main\" 
		        archive=\"http://ondex.rothamsted.ac.uk/OndexWeb/OndexWeb-0.5.0.jar\"
		        width=\"600\" height=\"400\">\n
		    \n
		    <!-- general configuration -->\n
		    <param name=\"ondex.dir\" value=\"http://ondex.rothamsted.ac.uk/applet/data\">\n
		    <param name=\"ovtk.dir\" value=\"http://ondex.rothamsted.ac.uk/applet/config\">\n
		    \n
		    <!-- appearance configuration -->\n
		    <param name=\"antialiased\" value=\"false\">\n
		    <param name=\"nodes.labels\" value=\"false\">\n
		    <param name=\"edges.lables\" value=\"false\">\n
		    \n
		    <!-- what to load -->\n
		    <param name=\"graphname\" value=\"$graphname\">\n
		    <param name=\"webservice\" value=\"$webservice\">\n
			\n
		Your browser is completely ignoring the &lt;APPLET&gt; tag!\n
		</applet>\n");
	}

?>

</body>
</html>