package net.sourceforge.ondex.server;

import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.service.invoker.BeanInvoker;

/**
 * Setup for test webservice server.
 * @deprecated 
 * @author David Withers
 */
public class Server {

	public static void main(String args[]) throws Exception {
		new Server();
		System.out.println("Server ready...");

		System.in.read();
		System.out.println("Server exiting");
		System.exit(0);
	}

	private Server() {
		String address = "http://localhost:9000/Ondex";
		ONDEXServiceWS implementor = new ONDEXServiceWS();

		AegisDatabinding binding = new AegisDatabinding();
				
		ServerFactoryBean sf = new ServerFactoryBean();
		sf.setServiceClass(ONDEXServiceWS.class);
		sf.setDataBinding(binding);
		sf.setAddress(address);
		sf.setInvoker(new BeanInvoker(implementor));
		sf.create();
	}
	
}
