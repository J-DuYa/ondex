package net.sourceforge.ondex.plugins.tab_parser_2.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Resources;

import net.sourceforge.ondex.core.Attribute;
import net.sourceforge.ondex.core.ConceptAccession;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXGraph;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.core.memory.MemoryONDEXGraph;
import net.sourceforge.ondex.tools.tab.importer.PathParser;

/**
 * Tests the parser with real use cases
 *
 * @author brandizi
 * <dl><dt>Date:</dt><dd>6 Jan 2017</dd></dl>
 *
 */
public class ParserTest
{
	@Test
	public void testTutorialGeneEx () throws Exception
	{
		Reader schemaReader = new InputStreamReader ( 
			Resources.getResource ( this.getClass (), "/ondex_tutorial_2016/gene_example_parser_cfg.xml" ).openStream (),
			"UTF-8"
		);
		ONDEXGraph graph = new MemoryONDEXGraph ( "default" );
	

		PathParser pp = ConfigParser.parseConfigXml ( 
			schemaReader, graph, "target/test-classes/ondex_tutorial_2016/gene_example.tsv" 
		);
		pp.parse ();
		
		
		Set<ONDEXConcept> concepts = graph.getConcepts ();
		final ONDEXConcept testConcept[] = new ONDEXConcept [ 1 ];
		assertFalse ( "No concepts found in the test file!", concepts.isEmpty () );
		assertTrue ( 
			"Expected protein not found in the test file!",
			FluentIterable
				.from ( concepts )
				.firstMatch ( new Predicate<ONDEXConcept> () 
				{
				  public boolean apply( ONDEXConcept concept ) 
				  {
				  	for ( ConceptAccession acc: concept.getConceptAccessions () )
				  		if ( "ENSG00000115317".equals ( acc.getAccession () ) )
				  		{
				  			testConcept [ 0 ] = concept;
				  			return true;
				  		}
				  	return false;
				  }
				}).isPresent ()
		);
		
		
		assertTrue ( 
			"Expected Chromosome attribute not found!",
			FluentIterable
				.from ( testConcept [ 0 ].getAttributes () )
				.firstMatch ( new Predicate<Attribute> () {
				  public boolean apply( Attribute attr) 
				  {
				  	return 
				  		"Chromosome".equals ( attr.getOfType ().getId () ) 
				  		&&  Integer.valueOf ( 2 ).equals ( attr.getValue () ); 
				  }
				}).isPresent ()
		);
		
		
		Set<ONDEXRelation> relations = graph.getRelations ();
		assertFalse ( "No relations found in the test file!", relations.isEmpty () );
		
		assertTrue ( 
			"Expected interaction not found in the test file!",
			FluentIterable
				.from ( relations )
				.firstMatch ( new Predicate<ONDEXRelation> () 
				{
				  public boolean apply( ONDEXRelation input) 
				  {
				  	ONDEXConcept from = input.getFromConcept ();
				  	ONDEXConcept to = input.getToConcept ();
				  	if ( !"ENSG00000143801".equals ( from.getConceptAccessions ().iterator ().next ().getAccession () ) )
				  		return false;
				  	if ( !"P49810".equals ( to.getConceptAccessions ().iterator ().next ().getAccession () ) )
				  		return false;
				  	return true;
				  }
				}).isPresent ()
		);		
	}
	
}
