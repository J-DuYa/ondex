package net.sourceforge.ondex.mapping.lowmemoryaccessionbased;

/**
 * ArgumentNames for the accession based mapping.
 * 
 * @author taubertj
 * 
 */
public interface ArgumentNames extends
		net.sourceforge.ondex.mapping.ArgumentNames {

	public final static String EQUIVALENT_CV_ARG = "equivalentCV";
	public final static String EQUIVALENT_CV_ARG_DESC = "This option should contain a pair of CVs seperated by a komma (,); for example \"TIGR,TAIR\"\n"
			+ "The usage of this setting is to explicitly tell that two accessions with different CVs are infact containing the same information.";

	public final static String IGNORE_AMBIGUOUS_ARG = "IgnoreAmbiguity";
	public final static String IGNORE_AMBIGUOUS_ARG_DESC = "When true it allows Ambiguous concept accessions to be mapped (use with care!).";

	public final static String RELATION_TYPE_ARG = "RelationType";
	public final static String RELATION_TYPE_ARG_DESC = "The relation type to create between matching concepts: default relation type is the equals relation type.";

	public final static String WITHIN_CV_ARG = "WithinCVMapping";
	public final static String WITHIN_CV_ARG_DESC = "Map within CVs";
	
	public static final String MAX_RELATIONS_ARG = "MaxRelations";
	public static final String MAX_RELATIONS_ARG_DESC = "The Max Number of Relations to create before aborting (usful for cyclical mapping and collapsing)";
	
	public static final String CV_ACCESSION_RESTRICTION_ARG = "CVRestriction";
	public static final String CV_ACCESSION_RESTRICTION_ARG_DESC = "Restriction on the mapping of CVs between two accessions";

}
