package net.sourceforge.ondex.restful.util;

import org.codehaus.stax2.XMLOutputFactory2;

import com.ctc.wstx.api.WstxOutputProperties;
import com.ctc.wstx.stax.WstxOutputFactory;

public class WstxOutputFactoryProvider {

	public static WstxOutputFactory xmlw;

	static {
		// configure XML output factory
		xmlw = (WstxOutputFactory) WstxOutputFactory.newInstance();
		xmlw.configureForRobustness();

		xmlw.setProperty(XMLOutputFactory2.IS_REPAIRING_NAMESPACES, false);
		xmlw.setProperty(WstxOutputProperties.P_OUTPUT_FIX_CONTENT, true);
		xmlw.setProperty(WstxOutputProperties.P_OUTPUT_VALIDATE_CONTENT, true);
	}
}
