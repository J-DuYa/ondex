package net.sourceforge.ondex.wsapi.result;

import net.sourceforge.ondex.core.AttributeName;
import net.sourceforge.ondex.core.Attribute;
import net.sourceforge.ondex.marshal.Marshaller;

/**
 *
 * @author Christian Brenninkmeijer
 */
public class WSAttribute {

    private static Marshaller marshaller = Marshaller.getMarshaller();

    private String valueAsXML;

    private String typeOf;

    private boolean doIndex;

    public WSAttribute(){
    }

    public WSAttribute(Attribute Attribute){
        if (Attribute == null) {
            valueAsXML = marshaller.toXML(null);
        } else {
            AttributeName attributeName = Attribute.getOfType();
            if (attributeName != null) {
                typeOf = attributeName.getId();
            }
            valueAsXML = marshaller.toXML(Attribute.getValue());
            doIndex = Attribute.isDoIndex();
        }
    }

    public String getTypeOf() {
        return typeOf;
    }

    public void setTypeOf(String typeof) {
        this.typeOf = typeof;
    }

    public Object unmarshall() {
        return marshaller.fromXML(valueAsXML);
    }

    public void marshall(Object value) {
        valueAsXML = marshaller.toXML(value);
    }

    public boolean isDoIndex() {
        return doIndex;
    }

    public void setDoIndex(boolean doIndex) {
        this.doIndex = doIndex;
    }

    public String getValueAsXML(){
        return valueAsXML;
    }

    public void setValueAsXML(String valueAsXML){
        this.valueAsXML = valueAsXML;
    }
}
