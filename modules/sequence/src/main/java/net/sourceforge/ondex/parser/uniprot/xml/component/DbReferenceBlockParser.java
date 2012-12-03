package net.sourceforge.ondex.parser.uniprot.xml.component;

import net.sourceforge.ondex.parser.uniprot.sink.DbLink;
import net.sourceforge.ondex.parser.uniprot.sink.Protein;
import net.sourceforge.ondex.parser.uniprot.xml.filter.ValueFilter;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author peschr
 */
public class DbReferenceBlockParser extends AccessionBlockParser {

    private static final String TYPE = "type";
    private static final String ID = "id";
    private static final String VALUE = "value";


    public DbReferenceBlockParser(ValueFilter filter) {
        super(filter);
    }

    public void parseElement(XMLStreamReader staxXmlReader) throws XMLStreamException {

        String type = null;
        String id = null;
        for (int i = 0; i < staxXmlReader.getAttributeCount(); i++) {
            String name = staxXmlReader.getAttributeLocalName(i);
            if (name.equalsIgnoreCase(TYPE)) {
                type = staxXmlReader.getAttributeValue(i);
            } else if (name.equalsIgnoreCase(ID)) {
                id = staxXmlReader.getAttributeValue(i);
            }
        }

        DbLink dblink = new DbLink();
        dblink.setDbName(type.trim());
        dblink.setAccession(id.toUpperCase());

        while (staxXmlReader.hasNext()) {
            int event = staxXmlReader.next();
            if (event == XMLStreamConstants.START_ELEMENT
                    && staxXmlReader.getLocalName().equals("property")) {
                String property_value = null;
                String property_type = null;

                for (int i = 0; i < staxXmlReader.getAttributeCount(); i++) {
                    String name = staxXmlReader.getAttributeLocalName(i);
                    if (name.equalsIgnoreCase(VALUE)) {
                        property_value = staxXmlReader.getAttributeValue(i);
                    } else if (name.equalsIgnoreCase(TYPE)) {
                        property_type = staxXmlReader.getAttributeValue(i);
                    }
                }
                if (property_type != null && property_type.equalsIgnoreCase("evidence")) {
                    dblink.addEvidence(property_value);
                }

            } else if (event == XMLStreamConstants.END_ELEMENT
                    && staxXmlReader.getLocalName().equals("dbReference")) {
                break;
            }
        }

        Protein.getInstance().addDbReference(dblink);
        if (filter != null) {
            // Is DataSource really necessary?
//			filter.check(dblink.getDbName() + dblink.getAccession());
            filter.check(dblink.getAccession());
        }
    }
}
