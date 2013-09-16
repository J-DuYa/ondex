var genespreadsheet = new Array();
var genes;

function showSynonymTable(option){
$('.suggestorTable:visible').fadeOut(0,function(){
		$('.buttonSynonym_on').attr('class','buttonSynonym_off');
		$('#'+option).fadeIn();
		$('#'+option+'_buttonSynonym').attr('class','buttonSynonym_on');
	})
}

function activateButton(option){
$('.resultViewer:visible').fadeOut(0,function(){
		$('.button_off').attr('class','button_on');
		$('#'+option).fadeIn();
		$('#'+option+'_button').attr('class','button_off');
	})
}

function addKeyword(keyword, from, target){
	query = $('#'+target).val();
	newquery = query+' OR '+keyword;
	$('#'+target).val(newquery);
	$('#'+from).attr('onClick','addKeywordUndo(\''+keyword+'\',\''+from+'\',\''+target+'\')');
	$('#'+from).attr('class','addKeywordUndo');
}

function addKeywordUndo(keyword, from, target){
	query = $('#'+target).val();
	newquery = query.replace(' OR '+keyword, "");
	$('#'+target).val(newquery);
	$('#'+from).attr('onClick','addKeyword(\''+keyword+'\',\''+from+'\',\''+target+'\')');
	$('#'+from).attr('class','addKeyword');
}

function excludeKeyword(keyword, from, target){
	query = $('#'+target).val();
	newquery = query+' NOT '+keyword;
	$('#'+target).val(newquery);
	$('#'+from).attr('onClick','excludeKeywordUndo(\''+keyword+'\',\''+from+'\',\''+target+'\')');
	$('#'+from).attr('class','excludeKeywordUndo');
}

function excludeKeywordUndo(keyword, from, target){
	query = $('#'+target).val();
	newquery = query.replace(' NOT '+keyword, "");
	$('#'+target).val(newquery);
	$('#'+from).attr('onClick','excludeKeyword(\''+keyword+'\',\''+from+'\',\''+target+'\')');
	$('#'+from).attr('class','excludeKeyword');
}

function replaceKeyword(oldkeyword, newkeyword, from, target){
	query = $('#'+target).val();
	newquery = query.replace(oldkeyword,newkeyword);
	$('#'+target).val(newquery);
	$('#'+from).attr('onClick','replaceKeywordUndo(\''+oldkeyword+'\',\''+newkeyword+'\',\''+from+'\',\''+target+'\')');
	$('#'+from).attr('class','replaceKeywordUndo');
}

function replaceKeywordUndo(oldkeyword, newkeyword, from, target){
	query = $('#'+target).val();
	newquery = query.replace(newkeyword,oldkeyword);
	$('#'+target).val(newquery);
	$('#'+from).attr('onClick','replaceKeyword(\''+oldkeyword+'\',\''+newkeyword+'\',\''+from+'\',\''+target+'\')');
	$('#'+from).attr('class','replaceKeyword');
}

/*
* Document ready event executes when the HTML document is loaded
* 	- add/remove QTL regions
* 	- advanced search
* 	- tooltips
*/
	
$(document).ready(
		function(){
			// Add QTL region
			$('#addRow').click(
					function() {
						var curMaxInput = $('#region_search_area table tr').length -1;
						$('#region_search_area tr:nth-child(2)')
							.clone()
							.insertAfter($('#region_search_area tr:last').prev())
							.find('td:eq(0)')
							.find('select:eq(0)')
							.attr({'id': 'chr' + (curMaxInput),
								   'name': 'chr' + (curMaxInput),
								   'onChange': 'findGenes(\'genes'+(curMaxInput)+'\', $(\'#chr'+(curMaxInput)+' option:selected\').val(), $(\'#start'+(curMaxInput)+'\').val(), $(\'#end'+(curMaxInput)+'\').val())',
								   'value': ''
								  })
							.parent().parent()	  
							.find('td:eq(1)')
							.find('input:text:eq(0)')
							.attr({'id': 'start' + (curMaxInput),
								   'name': 'start' + (curMaxInput),
								   'onKeyup': 'findGenes(\'genes'+(curMaxInput)+'\', $(\'#chr'+(curMaxInput)+' option:selected\').val(), $(\'#start'+(curMaxInput)+'\').val(), $(\'#end'+(curMaxInput)+'\').val())',
								   'value': ''
								  })
							.parent().parent()
							.find('td:eq(2)')
							.find('input:text:eq(0)')
							.attr({'id': 'end' + (curMaxInput),
									'name': 'end' + (curMaxInput),
									'onKeyup': 'findGenes(\'genes'+(curMaxInput)+'\', $(\'#chr'+(curMaxInput)+' option:selected\').val(), $(\'#start'+(curMaxInput)+'\').val(), $(\'#end'+(curMaxInput)+'\').val())',
									'value': ''
									})
							.parent().parent()
							.find('td:eq(3)')
							.find('input:text:eq(0)')
							.attr({
								'id': 'label' + (curMaxInput),
								'name': 'label' + (curMaxInput),
								'value': ''
								})
							.parent().parent()
							.find('td:eq(4)')
							.find('input:text:eq(0)')
							.attr({
								'id': 'genes' + (curMaxInput),
								'name': 'label' + (curMaxInput),
								'onFocus': 'findGenes(this.id, $(\'#chr'+(curMaxInput)+' option:selected\').val(), $(\'#start'+(curMaxInput)+'\').val(), $(\'#end'+(curMaxInput)+'\').val())',
								'value': ''
						});
						
						$('#removeRow').removeAttr('disabled');
						if ($('#region_search_area tr').length >= 7) {
							$('#addRow').attr('disabled', true);
						}
						return false;
					});
			// Remove QTL region
			$('#removeRow').click(
					function() {
						if ($('#region_search_area tr').length > 3) {
							$('#region_search_area tr:last').prev().remove();
						}
						if ($('#region_search_area tr').length <= 3) {
							$('#removeRow').attr('disabled', true);
						}
						else if ($('#rows tr').length < 7) {
							$('#addRow').removeAttr('disabled');								
						}
						return false;
					});
			// Region search
		     $('#region_search').click(
		    		 function() {				         
		    			 var src = ($(this).attr('src') === 'html/image/expand.gif')
		    	            ? 'html/image/collapse.gif'
		    	            : 'html/image/expand.gif';
		    	         $(this).attr('src', src);
		    	         $('#region_search_area').animate({
				               height: 'toggle'
				               }, 500
				          );
		    		 });
			// Advanced search
		     $('#advanced_search').click(
		    		 function() {				         
		    			 var src = ($(this).attr('src') === 'html/image/expand.gif')
		    	            ? 'html/image/collapse.gif'
		    	            : 'html/image/expand.gif';
		    	         $(this).attr('src', src);
		    	         $('#advanced_search_area').animate({
				               height: 'toggle'
				               }, 500
				          );
		    		 });
		     // Suggestor search
		     $('#suggestor_search').click(
		    		 function() {				         
		    			 var src = ($(this).attr('src') === 'html/image/expand.gif')
		    	            ? 'html/image/collapse.gif'
		    	            : 'html/image/expand.gif';
		    	         $(this).attr('src', src);
		    	         $('#suggestor_search_area').animate({
				               height: 'toggle'
				               }, 500
				          );
		    		 });
		 	// Tooltip	 	 	 		 		
	 		$('span#hint').live('mouseenter', function(event){
	 			target = event.target.id;
 				var message = "";
 				if(target == 'hintSearchQtlGenome'){
 					message = 'Select the "whole-genome" option to search the whole genome for potential candidate genes or  select the "within QTL" option to search for candidate genes within the QTL coordinates.';
 				}
 				else if(target == 'hintEnterGenes'){
 					message = 'Helpful hint about a list of genes.';
 				}
				else if(target == 'hintQuerySuggestor'){
 					message = 'Add, remove o replace term from your query using the list of suggested terms based on your search chriteria';
 				}
 				else if(target == 'hintSortableTable'){
 					message = 'This opens the Ondex Web java applet and displays a sub-network of the large Ondex knowledgebase that only contains the selected genes (light blue triangles) and the relevant evidence network.';
 					//message = 'Sort multiple columns simultaneously by holding down the shift key and clicking column headers! ';
 				}
				$('div.tooltip').remove();
				$('<div class="tooltip">'+message+'</div>').appendTo('body');
	 		});	 		
	 		$('span#hint').live('mousemove', function(event){
	 			var tooltipX = event.pageX - 8;
	     		var tooltipY = event.pageY + 8;
	     		$('div.tooltip').css({top: tooltipY, left: tooltipX});
	 		}); 	 		
	 		$('span#hint').live('mouseleave', function(event){
	 			$('div.tooltip').remove();
	 		});	 		
		});
  
/*
 * Function to refresh GViewer
 * 
 */
function searchKeyword(){
	var searchMode = getRadioValue(document.gviewerForm.search_mode);
	var listMode = getRadioValue(document.gviewerForm.list_mode);
	var keyword = escape(trim($("#keywords").val()));
	var list = $("#list_of_genes").val();
	var regions = document.getElementById('regions_table').rows.length -2;
	var request = "keyword="+keyword+"&mode="+searchMode;
	if(list.length > 0){
		request = request+"&listMode="+listMode;
	}
	var counter = 0;
	
	for(i=1; i<=regions; i++){	
		var chr = $("#chr"+i+" option:selected").val();
		var start = trim($("#start"+i).val());
		var end = trim($("#end"+i).val());
		var label = trim($("#label"+i).val());
		if(chr.length>0 && start.length>0 && end.length>0 && parseInt(start)<parseInt(end)){
			if(countGenes(chr, start, end)>0) {
				counter++;
				request = request+"&qtl"+counter+"="+chr+":"+start+":"+end+":"+label;				
			}			
		}		
	}

	if(keyword.length < 2) {
		$("#loadingDiv").replaceWith('<div id="loadingDiv"><b>Please provide a keyword</b><br />e.g. '+warning+'</div>');
	}
	else if(list.length > 500000) {
		$("#loadingDiv").replaceWith('<div id="loadingDiv"><b>Please provide a valid list of genes.</b></div>');
	}
	else if(counter == 0 && searchMode == "qtl") {		
		$("#loadingDiv").replaceWith('<div id="loadingDiv"><b>Please define at least one QTL region.</b></div>');
	}
	else{
		$("#loadingDiv").replaceWith('<div id="loadingDiv"><img src="html/image/spinner.gif" alt="Loading, please wait..." /></div>');			
		
		$.ajax({
	        url:"OndexServlet?"+request,
	        type:'POST',
	        dataType:'text',
	        async: true,
	        timeout: 1000000,
	        data:{list : list},
	        error: function(){						  
	        },
	        success: function(response, textStatus){
				$("#loadingDiv").replaceWith('<div id="loadingDiv"></div>');	
				if(response.indexOf("NoFile:noGenesFound") !=-1 ||  !response.split(":")[4] > 0){
					var genomicViewTitle = '<div id="pGViewer_title">Sorry, no results were found.<br /></div>'
					var genomicView = '<div id="pGViewer" class="resultViewer">';
					var gviewer_html = '<center><object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,0,0" width="600" height="600" id="GViewer2" align="middle"><param name="wmode" value="transparent"><param name="allowScriptAccess" value="sameDomain" /><param name="movie" value="html/GViewer/GViewer2.swf" /><param name="quality" value="high" /><param name="bgcolor" value="#FFFFFF" /><param name="FlashVars" value="&lcId=1234567890&baseMapURL=html/data/basemap.xml&annotationURL=&dimmedChromosomeAlpha=40&bandDisplayColor=0x0099FF&wedgeDisplayColor=0xCC0000&browserURL=OndexServlet?position=Chr&" /><embed style="width:700px; height:550px;" id="embed" src="html/GViewer/GViewer2.swf" quality="high" bgcolor="#FFFFFF" width="600" height="600" name="GViewer2" align="middle" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" FlashVars="&lcId=1234567890&baseMapURL=html/data/basemap.xml&annotationURL=&dimmedChromosomeAlpha=40&bandDisplayColor=0x0099FF&wedgeDisplayColor=0xCC0000&titleBarText=&browserURL=OndexServlet?position=Chr&" pluginspage="http://www.macromedia.com/go/getflashplayer" /></object></center></div>';
					genomicView = genomicView + gviewer_html;
					$("#pGViewer_title").replaceWith(genomicViewTitle);
					$("#pGViewer").replaceWith(genomicView);
	        		document.getElementById('resultsTable').innerHTML = "";		        		
	        	}
				else {
					var splitedResponse = response.split(":");  
					var results = splitedResponse[5];
					var docSize = splitedResponse[6];
					var candidateGenes = parseInt(results);
					var genomicViewTitle = '<div id="pGViewer_title">In total '+results+' genes were found. Query was found in '+docSize+' documents<br /></div>'
					var genomicView = '<div id="pGViewer" class="resultViewer"><p class="margin_left">Shift+Click on a gene to see its knowledge network.</p>';
					if(candidateGenes > 100){
						candidateGenes = 100;
						var genomicViewTitle = '<div id="pGViewer_title">In total '+results+' genes were found.  Query was found in '+docSize+' documents. Top 100 genes are displayed.<br /></div>';
					}			
					gviewer_html = '<center><object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,0,0" width="600" height="600" id="GViewer2" align="middle"><param name="wmode" value="transparent"><param name="allowScriptAccess" value="sameDomain" /><param name="movie" value="html/GViewer/GViewer2.swf" /><param name="quality" value="high" /><param name="bgcolor" value="#FFFFFF" /><param name="FlashVars" value="&lcId=1234567890&baseMapURL=html/data/basemap.xml&annotationURL='+data_url+splitedResponse[1]+'&dimmedChromosomeAlpha=40&bandDisplayColor=0x0099FF&wedgeDisplayColor=0xCC0000&browserURL=OndexServlet?position=Chr&" /><embed style="width:700px; height:550px;" id="embed" src="html/GViewer/GViewer2.swf" quality="high" bgcolor="#FFFFFF" width="600" height="600" name="GViewer2" align="middle" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" FlashVars="&lcId=1234567890&baseMapURL=html/data/basemap.xml&annotationURL='+data_url+splitedResponse[1] +'&dimmedChromosomeAlpha=40&bandDisplayColor=0x0099FF&wedgeDisplayColor=0xCC0000&titleBarText=&browserURL=OndexServlet?position=Chr&"  pluginspage="http://www.macromedia.com/go/getflashplayer" /></object></center></div>';
					genomicView = genomicView + gviewer_html;
					$("#pGViewer_title").replaceWith(genomicViewTitle);
					$("#pGViewer").replaceWith(genomicView);	
					
					//Preloader for Synonym table
					$('#suggestor_terms').html('');
					$('#suggestor_tables').html('<div class="preloader_wrapper"><img src="html/image/preloader_bar.gif" alt="Loading, please wait..." /></div>');
					
					activateButton('resultsTable');
					createSynonymTable(data_url+splitedResponse[4]);
					createGenesTable(data_url+splitedResponse[2], keyword, candidateGenes);
					createEvidenceTable(data_url+splitedResponse[3]);
				}
	        }
		});
	}
}

/*
 * Function
 * 
 */
function generateNetwork(url,list){
	//OndexServlet?mode=network&list=POPTR_0003s06140&keyword=acyltransferase
	$.post(url, list, function(response, textStatus){																							 
	var oxl = response.split(":")[1];
	var output ="<p class=margin_left>The Ondex knowledge network has been generated and is displayed in the Ondex Web applet." + 
        		"Alternatively it can be <a href="+graph_url + oxl +">downloaded</a> and opened in the <a href=http://www.ondex.org>Ondex desktop application</a>.</p>" +
        		"<applet CODE=net.sourceforge.ondex.ovtk2lite.Main ARCHIVE="+applet_url+"ovtk2lite-0.5.0-SNAPSHOT.jar WIDTH=760 HEIGHT=600></xmp>" +
	            "<PARAM NAME=CODE VALUE=net.sourceforge.ondex.ovtk2lite.Main>" +
	            "<PARAM NAME=ARCHIVE VALUE="+applet_url+"ovtk2lite-0.5.0-SNAPSHOT.jar>" +
	            "<param name=type value=application/x-java-applet;version=1.6>" +
	            "<param name=scriptable value=false>" +
	            "<PARAM NAME=ondex.dir VALUE="+applet_url+"data>" +
	            "<PARAM NAME=ovtk.dir VALUE="+applet_url+"config>" +
	            "<PARAM NAME=password VALUE=ovtk>" +
	            "<PARAM NAME=username VALUE=ovtk>" +
	            "<PARAM NAME=loadappearance VALUE=true>" +
	            "<PARAM NAME=antialiased VALUE=true>" +
	            "<PARAM NAME=nodes.labels VALUE=true>" +
	            "<PARAM NAME=edges.lables VALUE=true>" +
	            "<PARAM NAME=filename VALUE="+graph_url + oxl +">" +
	            "Your browser is completely ignoring the &lt;APPLET&gt; tag!" +
	            "</applet>"+
	            "<br>" +
	            "<div id=legend_picture><div id=legend_container>" +
	            "<img src=html/image/evidence_legend.png>" +
	            "</div></div>";
				$('#NetworkCanvas').html(output);
				activateButton('NetworkCanvas');
	});
}
/*
 * Function
 * 
 */
function generateMultiGeneNetwork(keyword) {	
	var candidatelist = "";
	var cb_list = document.checkbox_form.candidates;
	for (var i=0; i < cb_list.length; i++) {		
		if(cb_list[i].checked) {
			candidatelist += cb_list[i].value + "\n";
		}
	}
	if(candidatelist == "") {
		$("#loadingNetworkDiv").replaceWith('<div id="loadingNetworkDiv"><b>Please select candidate genes.</b></div>');
	} else {
			generateNetwork('OndexServlet?mode=network&keyword='+keyword, {list : candidatelist});				
	}
}
/*
 * jQuery.ajax - load the gene annotation data (under /html/data/reference/)
 * from server when the page is loading
 */
$.ajax({
        url: 'html/data/geneposition.txt',
        type: 'GET',
        dataType: 'text',
        timeout: 10000,
        error: function(){
  
        },
        success: function(text){
	        genes = text.split("\n");
	
			for(var i=0; i<genes.length; i++){
			    gene = genes[i].split("\t");
			    var chr = gene[0];
			    var beg = gene[1];
			    var end = gene[2];
			    if(beg > end){
					beg = gene[2];
					end = gene[1];
				}
			    genespreadsheet.push([gene[0], gene[1], gene[2]]);
	        }
        }
    });

/*
 * Function
 * 
 */
function countGenes(chr_name, start, end) {	   
	var temparray = new Array(); 
	for(var i=0; i<genes.length; i++) {
		if(genespreadsheet[i][0] == chr_name && parseInt(genespreadsheet[i][1]) >= start && parseInt(genespreadsheet[i][1]) <= end) {
			temparray.push(genespreadsheet[i]);
		}
	}
	return temparray.length;
}

/*
 * Function
 * 
 */
function findGenes(id, chr_name, start, end) {
	$("#"+id).val(countGenes(chr_name, start, end));
}

/*
 * Function
 * 
 */
function contactWindow() {
	window.open( "html/contact.html", "QTLNetMiner-Contact", "status=0, toolbar=0, location=0, menubar=0, height=200, width=400, resizable=0" );
}

/*
 * Function
 * 
 */
function getRadioValue(radio) {
	var radioValue;
	for (var i=0; i < radio.length; i++) {
		if (radio[i].checked) {
			radioValue = radio[i].value;
		}
	}
	return radioValue;
}

/*
 * Function
 * 
 */
function createGenesTable(tableUrl, keyword, rows){
	var table = "";
	$.ajax({
        url:tableUrl,
        type:'GET',
        dataType:'text',
        async: true,
        timeout: 1000000,
        error: function(){						  
        },
        success: function(text){
        	
    		var candidate_genes = text.split("\n");
    		var results = candidate_genes.length-2;
    		if(results >= 100){
    			results = 100;
    		}
    		if(candidate_genes.length > 2) {
		        table =  '';
				table = table + '<p class="margin_left">Download full results ('+(candidate_genes.length-2)+' genes) as a table from <a href="'+tableUrl+'" target="_blank">here</a>.<br />';
				table = table + 'Select gene(s) and click "Show Network" button to see the Ondex network.<span id="hint"><img id="hintSortableTable" src="html/image/hint.png" /></span></p>';
				table = table + '<form name="checkbox_form">';
				table = table + '<div id="selectAll"><input type="checkbox" name="chkall" />Select All</div>';			
				table = table + '<div class = "scrollTable">';
				table = table + '<table id = "tablesorter" class="tablesorter">';
				table = table + '<thead>';
				table = table + '<tr>';
				var values = candidate_genes[0].split("\t");
				table = table + '<th width="100">'+values[1]+'</th>';			
				table = table + '<th width="60">'+values[3]+'</th>';
				table = table + '<th width="70">'+values[4]+'</th>';
				//table = table + '<th width="70">'+values[5]+'</th>';
				table = table + '<th width="70">'+values[6]+'</th>';							
				table = table + '<th width="70">'+values[7]+'</th>';
				table = table + '<th width="70">'+values[8]+'</th>';
				table = table + '<th width="220">'+values[9]+'</th>';
				table = table + '<th width="90">Select</th>';							
				table = table + '</tr>';
				table = table + '</thead>';
				table = table + '<tbody class="scrollTable">';																				
				for(var i=1; i<=rows; i++) {
		        	table = table + '<tr>';
				    var values = candidate_genes[i].split("\t");
				    var appletQuery = 'OndexServlet?mode=network&list='+values[1]+'&keyword='+keyword;
				    var gene = '<td><a href = "javascript:;" onClick="generateNetwork(\''+appletQuery+'\',null);">'+values[1]+'</a></td>';
				    
				    var chr = '<td>'+values[3]+'</td>';
				    var start = '<td>'+values[4]+'</td>';
				    var end = '<td>'+values[5]+'</td>';
				    var score = '<td>'+values[6]+'</td>';
				    var withinQTL = '<td>'+values[7]+'</td>';
				    var usersList = '<td>'+values[8]+'</td>';
					
					// Foreach evidence show the images - start
					var evidence = '<td>';
					var values_evidence = values[9];
					var evidences = values_evidence.split("||");
					if(evidences.length >0){
						for (var count_i = 0; count_i < (evidences.length); count_i++) {
							//Shows the icons
							var evidence_elements = evidences[count_i].split("//");
							evidence = evidence+'<div class="evidence_item evidence_item_'+evidence_elements[0]+'" title="'+evidence_elements[0]+'" ><span onclick="$(\'#evidence_box_'+values[1]+evidence_elements[0]+'\').slideDown(300);" style="cursor:pointer;">'+((evidence_elements.length)-1)+'</span>';	
								//Builds the evidence box
								evidence = evidence+'<div id="evidence_box_'+values[1]+evidence_elements[0]+'" class="evidence_box" style="display:none"><a class="evidence_box_close" href="javascript:;" onclick="$(\'#evidence_box_'+values[1]+evidence_elements[0]+'\').slideUp(100);"></a>';
								evidence = evidence+'<p><div class="evidence_item evidence_item_'+evidence_elements[0]+'"></div> <span>'+evidence_elements[0]+'</span></p>';
								for (var count_eb = 1; count_eb < (evidence_elements.length); count_eb++) {
									evidence = evidence+'<p>'+evidence_elements[count_eb]+'</p>';
								}
								evidence = evidence+'</div>';
							evidence = evidence+'</div>';
									
							
										
						}
					}
					evidence = evidence+'</td>';
					// Foreach evidence show the images - end
					
				    var select = '<td><input type="checkbox" name= "candidates" value="'+values[1]+'"></td>';
				    //table = table + gene + chr + start + end + score + withinQTL + usersList + evidence + select;
					table = table + gene + chr + start + score + withinQTL + usersList + evidence + select;
				    table = table + '</tr>';
				}
				table = table+'</tbody>';	
		        table = table+'</table></div>';			        
		        table = table + '</form>';	        
    		}
    		document.getElementById('resultsTable').innerHTML = table+
    		'<div id="networkButton"><input class = "button" type = "button" value = "Show Network" onClick="generateMultiGeneNetwork(\''+keyword+'\');"></insert><div id="loadingNetworkDiv"></div></div><div id="legend_picture"><div id="legend_container"><img src="html/image/evidence_legend.png" /></div></div>';
    		$("#tablesorter").tablesorter({ 
    	        headers: { 
    	            // do not sort "select" column 
    	            8: {  sorter: false }
    	        } 
    	    }); 
    		$('input[name="chkall"]').click(function() {
    			$("#tablesorter :checkbox").attr('checked', $(this).attr('checked'));
    		});
        }
	});	
}

/*
 * Function
 * 
 */
function containsKey(keyToTest, array){
	result = false;
	for(key in array) { 
		if(key == keyToTest){
			result = true;	
		}
	}
	return result;
}

/*
 * Function
 * 
 */
function createEvidenceTable(tableUrl){
	var table = "";
	$.ajax({
        url:tableUrl,
        type:'GET',
        dataType:'text',
        async: true,
        timeout: 1000000,
        error: function(){						  
        },
        success: function(text){
			var summaryArr = new Array();
			var summaryText = '';
    		var evidenceTable = text.split("\n");
			if(evidenceTable.length > 2) {
				table = '';
				table = table + '<p></p>';
				table = table + '<div id="evidenceSummary"></div>';
				table = table + '<div class = "scrollTable">';
				table = table + '<table id = "tablesorterEvidence" class="tablesorter">';
				table = table + '<thead>';
				table = table + '<tr>';
				var header = evidenceTable[0].split("\t");				
				table = table + '<th width="100">'+header[0]+'</th>';
				table = table + '<th width="212">'+header[1]+'</th>'
				table = table + '<th width="78">'+header[2]+'</th>';			
				table = table + '<th width="60">'+header[3]+'</th>';
				table = table + '<th width="103">'+header[4]+'</th>';
				table = table + '<th width="50">'+header[5]+'</th>';							
				table = table + '</tr>';
				table = table + '</thead>';
				table = table + '<tbody class="scrollTable">';
				for(var ev_i=1; ev_i < (evidenceTable.length-1); ev_i++) {
					values = evidenceTable[ev_i].split("\t");
					table = table + '<tr>';
					table = table + '<td>'+values[0]+'</td>';
					table = table + '<td>'+values[1]+'</td>';
					table = table + '<td>'+values[2]+'</td>';
					table = table + '<td>'+values[3]+'</td>';
					table = table + '<td>'+values[4]+'</td>';
					table = table + '<td>'+values[5]+'</td>';
					table = table + '</tr>';
					//Calculates the summary box
					if (containsKey(values[0],summaryArr)){
						summaryArr[values[0]] = summaryArr[values[0]]+1;					
					} else {
						summaryArr[values[0]] = 1;	
					}
				}
				table = table + '</tbody>';
				table = table + '</table>';
				table = table + '</div>';
				table = table + '<div id="legend_picture"><div id="legend_container"><img src="html/image/evidence_legend.png" /></div></div>';
				
				$('#evidenceTable').html(table);
				$("#tablesorterEvidence").tablesorter({sortList: [[2,1], [0,0]]}); 
				//Shows the summary box
				for(key in summaryArr){
					summaryText = summaryText+'<div class="evidenceSummaryItem"><div class="evidence_item evidence_item_'+key+'" title="'+key+'"></div>'+summaryArr[key]+'</div>';	
				}
				$('#evidenceSummary').html(summaryText);
			}
		}
	})
}

/*
 * Function
 * 
 */
function createSynonymTable(tableUrl){
	var table = "";
	$.ajax({
        url:tableUrl,
        type:'GET',
        dataType:'text',
        async: true,
        timeout: 1000000,
        error: function(){						  
        },
        success: function(text){
			var summaryArr = new Array();
			var summaryText = '';
    		var evidenceTable = text.split("\n");
			var countSynonyms = 0;
			if(evidenceTable.length > 3) {
				terms = '';
				table = '';								
				for(var ev_i=0; ev_i < (evidenceTable.length-1); ev_i++) {
					if(evidenceTable[ev_i].substr(0,2) == '</'){
						table = table + '</tbody>';
						table = table + '</table>';
					}else if(evidenceTable[ev_i][0] == '<'){
						if(ev_i == 0){
							divstyle = "buttonSynonym_on";	
							tablevisibility = "";
						}else{
							divstyle = "buttonSynonym_off";
							tablevisibility = 'style="display:none;"';		
						}
						termName = evidenceTable[ev_i].replace("<","");
						var originalTermName = termName.replace(">","");
						termName = originalTermName.replace(" ","_");
						terms = terms + '<a href="javascript:;" onclick="showSynonymTable(\'tablesorterSynonym'+termName+'\')"><div class="'+divstyle+'" id="tablesorterSynonym'+termName+'_buttonSynonym">'+termName+'</div></a>';	
							
						table = table + '<table id="tablesorterSynonym'+termName+'" class="suggestorTable" '+tablevisibility+'>';
						table = table + '<thead>';
						table = table + '<tr>';				
						table = table + '<th width="100">Actions</th>';
						table = table + '<th width="212">Term</th>'
						table = table + '<th width="78">Document</th>';			
						table = table + '<th width="60">Score</th>';
						table = table + '</tr>';
						table = table + '</thead>';
						table = table + '<tbody class="scrollTable">';
					}else{
						countSynonyms++;
						values = evidenceTable[ev_i].split("\t");
						table = table + '<tr>';				
						table = table + '<th width="100"><a id="synonymstable_add_'+ev_i+'" class="addKeyword" href="javascript:;" onclick="addKeyword(\''+values[0]+'\', \'synonymstable_add_'+ev_i+'\', \'keywords\')">Add</a>|<a id="synonymstable_exclude_'+ev_i+'" class="excludeKeyword" href="javascript:;" onclick="excludeKeyword(\''+values[0]+'\', \'synonymstable_exclude_'+ev_i+'\', \'keywords\')">Exclude</a>|<a id="synonymstable_replace_'+ev_i+'" class="replaceKeyword" href="javascript:;" onclick="replaceKeyword(\''+originalTermName+'\',\''+values[0]+'\', \'synonymstable_replace_'+ev_i+'\', \'keywords\')">Replace</a></th>';
						table = table + '<th width="212">'+values[0]+'</th>'
						table = table + '<th width="78"><div class="evidence_item evidence_item_'+values[1]+'" title="'+values[1]+'"></div></th>';			
						table = table + '<th width="60">'+values[2]+'</th>';
						table = table + '</tr>';
					}
				}				
				$('#suggestor_invite').html(countSynonyms+' synonyms found');
				$('#suggestor_terms').html(terms);
				$('#suggestor_tables').html(table);
			}
		}
	})
}

/*
 * Function
 * 
 */
function trim(text) {
    return text.replace(/^\s+|\s+$/g, "");
}

/*
 * Google Analytics
 * 
 */
var _gaq = _gaq || [];
 _gaq.push(['_setAccount', 'UA-26111300-1']);
 _gaq.push(['_trackPageview']);

(function() {
	var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
