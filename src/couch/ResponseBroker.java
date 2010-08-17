package core;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpNode;

import core.service.MessageBroker;

public class ResponseBroker extends MessageBroker {

	public static String MBOX_NAME = "rsp";
	
	public String RSP_GENERATED = "org/apache/couchdb/httpd/rsp/GENERATED";

	public ResponseBroker(BundleContext bundleContext, OtpNode node) {
		super(bundleContext, node, MBOX_NAME);
	}
	
	@Override
	public void run() {
		while (active) {
			// may not need to listen for reply
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void responseGenerated(ResponseObject rsp, BundleContext context)
    {
        ServiceReference ref = context.getServiceReference(EventAdmin.class.getName());
        if (ref != null)
        {
            EventAdmin eventAdmin = (EventAdmin) context.getService(ref);
            
            Map<String, String> properties = new Hashtable<String, String>();
            //properties.put("rsp", rsp.getRequestReference());
            
            Event rspGeneratedEvent = new Event("org/apache/couchdb/httpd/rsp/GENERATED", properties);
            
            /*
             *  to send synchronously, use the EventAdmin.sendEvent) method
             */
            eventAdmin.postEvent(rspGeneratedEvent);
            
        }
    }
	
	
	
}
