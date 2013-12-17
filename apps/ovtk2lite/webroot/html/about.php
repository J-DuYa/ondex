<script>
$(document).ready(function(){
	 
	   // jQuery methods go here...
	   $(".TagLine").css("font-family","Impact, trebuchet");
	   $(".TagLine").css("font-size","2em");
	   $(".TagLine").css("color","#ccc");
	   $(".imgL").css("width","50%");
	   $("#tryit").button();
$("#tryit").css("font-size","1.5em");
				$("#tryit").css("font-weight","200");
				$("#tryit").css("color","#336633");
				$("#tryit").css("background-color","#d3e6d9");
				$("#tryit").css("background-image","none");
				$("#tryit").css("width","399");
				$("#tryit").css("margin-top","18px");
				$("#tryit").css("margin-left","16px");
$("#tryit").click(function() {
	
	$("[href='#tabs-2']").click();
	
} );

$("#KeyFeats").accordion(295);
$("#KeyFeats").css("width", "400");
//$("#KeyFeats > p").css("min-height", "195px");
$("#KeyFeats").css("float", "right");
	 });

</script>


<p class="TagLine">Interactive Delivery of Heterogeneous Biological Networks</p>

<img  class="imgL" src="logos/OW1.JPG">

<div id="KeyFeats">

<h2>Interactive exploration</h2>
<p>Knowledge is power but for interrelated data, knowledge is often hidden in massive links in 
heterogeneous information networks (Sun and Han, 2012). Heterogeneous biological networks 
consist of multi-typed nodes (e.g. gene, pathway, literature) and links (e.g. encodes, interacts, 
published). Context-aware right-click menus of Ondex Web allow to explore large networks interactively. 
The complete list of information on a node or edge is accessible using the "Item Info" (View-&gt;Item Info).
</p>

<h2>Function rich</h2>
<p>Ondex Web has been built with the idea in mind of reusing proven functions and plugins from the Ondex desktop 
application. This provides a rich repertoire of tools for manipulating heterogeneous biological 
networks (see Tools-&gt;Annotators). Annotators make use of semantic information and general attribute 
values on nodes and edges to change the appearance of the network.
</p>

<h2>Customized visualisation</h2>
<p>The semantic information on nodes and edges is used to automatically map visual attributes like node shape, 
node color or edge color. The appearance of any node or edge can be customised using right-click menus or it can be
pre-calculated, stored in special attribute values and loaded via  "Load Appearance".
Different layout algorithms (Appearance-&gt;Layouts) allow to change the overall layout of your network. 
</p>

<h2>Advanced search</h2>
<p>Enter any keyword or regular expression to search all the information held in the network (e.g. name, accession, title) or external sources. 
The search results are displayed in a separate frame in a tabular format. Selecting one or more rows in the table 
propagates to selection of the corresponding nodes in the network, immediately highlighting the search results in 
the visualization.
</p>

<h2>Embed on your website </h2>
<p>Ondex Web can easily be embedded on websites using only few lines of code (see Download). Ondex Web supports loading data from a variety of network formats, such as OXL, XGMML, NWB and Pajek. A real-world 
application of Ondex Web can be seen in <a href="http://ondex.rothamsted.ac.uk/QTLNetMiner/">QTLNetMiner</a> - 
a tool for visualizing evidence networks of candidate genes in plant and animal genomes. </p>
</div>

<button id="tryit" >Try It!</button>

<p>How to cite Ondex Web: An Ondex Web applications note has been submitted and is currently under review.</p>
