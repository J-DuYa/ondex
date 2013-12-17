Scripting console
=================

Copy and paste the following commands in the console (editing the location of test.tab as appropriate):

p = new PathParser(getActiveGraph(), new DelimitedFileReader("D:/Ondex_tutorial/Tutorial_files/Data_integration/Console/test.tab", "	"));
c1 = p.newConceptPrototype(defAccession(0, "UNIPROTKB"), defCC("Protein"), defName(2));
c2 = p.newConceptPrototype(defAccession(1, "UNIPROTKB"), defCC("Protein"), defName(3));
p.newRelationPrototype(c1, c2, defAttribute(4, "P-value", "NUMBER"));
p.parse();