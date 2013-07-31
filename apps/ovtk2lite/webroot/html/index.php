<!doctype html> 
<html lang="us">
<head>
	<meta charset="utf-8">
	<title>OndexWeb: Ondex for the Web!</title>
	<link href="css/jquery-ui-1.10.3.custom.css" rel="stylesheet">
	<link href="css/main.css" rel="stylesheet">
	<script src="js/jquery-1.9.1.js"></script>
	<script src="js/jquery-ui-1.10.3.custom.js"></script>
	<link href="css/blue.css" media="screen" rel="stylesheet" type="text/css" />

	<script>

	function ondexweb () {
		var attributes = {
				width : 800, 
				height : 500
			};
			var parameters = {
				jnlp_href : 'OndexWeb.jnlp',
				filename : 'config/examples/GeneNetwork.oxl',
				loadappearance: 'true',
			};
			deployJava.runApplet(attributes, parameters, '1.6');
	}
	
	$(function() {
		$("html").css("overflow-y","scroll");
		$( "#accordion" ).accordion();
		$( "#accFAQ" ).accordion();
		//$( "#tabs-7" ).accordion();	
		var availableTags = [
			"ActionScript",
			"AppleScript",
			"Asp",
			"BASIC",
			"C",
			"C++",
			"Clojure",
			"COBOL",
			"ColdFusion",
			"Erlang",
			"Fortran",
			"Groovy",
			"Haskell",
			"Java",
			"JavaScript",
			"Lisp",
			"Perl",
			"PHP",
			"Python",
			"Ruby",
			"Scala",
			"Scheme"
		];
		$( "#autocomplete" ).autocomplete({
			source: availableTags
		});
		

		
		$( "#button" ).button();
		$( "#radioset" ).buttonset();
		

		
		$( "#tabs" ).tabs();
		

		
		$( "#usage" ).dialog({
			autoOpen: false,
			width: 400,
			buttons: [
				{
					text: "Ok",
					click: function() {
						$( this ).dialog( "close" );
					}
				},
				
			]
		});

		// Link to open the dialog
		$( "#dialog-link" ).click(function( event ) {
			
			//$( "#usage" ).dialog( "open" );
			
			//event.preventDefault();
			alert( $("#usage2").html() );
			
		});
		

		
		$( "#datepicker" ).datepicker({
			inline: true
		});
		

		
		$( "#slider" ).slider({
			range: true,
			values: [ 17, 67 ]
		});
		
		
		$( "#progressbar" ).progressbar({
			value: 20
		});
		//$('#header').css('background-image', 'url(includes/back3.gif)');
		//$('#header').css('background-position-x', '400px');
		//$('#header').css('background-repeat', 'no-repeat');
		//$('#header').css('opacity', '0.4');
		// Hover states on the static widgets
		$( "#dialog-link, #icons li" ).hover(
			function() {
				$( this ).addClass( "ui-state-hover" );
			},
			function() {
				$( this ).removeClass( "ui-state-hover" );
			}
		);
	});
	</script>
	<style>
	
	</style>
<script src="js/deployJava.js"></script>
</head>
<body>
<div id="wrapper">

	<div id="header">
    	<span id="OndexTitle">
    	<a href=""><img hspace="5" vspace="5" align="left" src="logos/ondex_graphic_small.png"  /> </a>
    	<span id="sub1">OndexWeb</span><br />
    	<span id="sub2">&nbsp;Ondex Networks For Your Website!
    	</span>
        
     <div id="RResLogo"><a href="http://www.rothamsted.ac.uk"><img src="logos/RResLogo.jpg"  /> </a></div>     
    </div>
   <div id="content">
   
   <div id="tabs">
			<ul>
			<li><a href="#tabs-1">OndexWeb</a></li>
			<li><a href="#tabs-2">Demo</a></li>
			<li><a href="#tabs-5">Tutorial</a></li>
			<li><a href="#tabs-3">Download</a></li>
			<li><a href="#tabs-7">FAQ</a></li>			
			<li><a href="#tabs-6">Contact</a></li>
			<!--<li><a href="#tabs-4">Credits</a></li>-->
			</ul>
	
			<div id="tabs-1">
					<?php 
					include('html/about.php');
					?>	
			</div>
		
			<div id="tabs-2">
			
								<?php 
					include('html/demo.php');
					?>	
				
			</div>

			<div id="tabs-3">
				<?php 
					include('html/readme.html');
					?>	
			
			</div>
			<!--
				<div id="tabs-4">
					<?php 
					include('html/credits.php');
					?>	
			</div>	
			-->

			<div id="tabs-6">

							<?php 
					include('html/contact.php');
					?>	
		
			</div>
				<div id="tabs-5">
					<?php 
					include('html/documentation.php');
					?>	
		
		
			</div>
			<div id="tabs-7">

							<?php 
					include('html/FAQ.php');
					?>	
		
			</div>			

	
	
	</div>
	
	
<div id="footer">
	<div id="left_footer">
<img src="images/Ondex_logo.png" />
<br />Developed at Rothamsted Research (<a href="http://www.rothamsted.ac.uk/csys/bioinf.php">Applied Bioinformatics Group</a>)<br />            
        
    </div>
    <div id="right_footer">
    	<img src="logos/BBSRCLogo.jpg" /><br />
       This work was supported by BBSRC SABR award BB/F006039/1 and <br />
       TSB project TP 5082-33372.<br />
    </div>
	
</div>

</div>
</body>
</html>
	