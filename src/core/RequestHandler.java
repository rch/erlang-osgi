package core;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

import com.ericsson.otp.erlang.OtpErlangRef;

public class RequestHandler implements EventHandler {
	
	protected BundleContext context;
	
	public RequestHandler(BundleContext bundleContext) {
		super();
		context = bundleContext;
	}
	
    public void handleEvent(RequestObject request)
    {	
        ServiceReference event_admin = context.getServiceReference(EventAdmin.class.getName());
		EventAdmin eventAdmin = (EventAdmin) context.getService(event_admin);
		
		ServiceReference response_broker = context.getServiceReference(ResponseBroker.class.getName());
		ResponseBroker rspBroker = (ResponseBroker) context.getService(response_broker);
		
		Map<String, String> properties = new Hashtable<String, String>();
		properties.put("ref", "reference");
		
		Event rspGeneratedEvent = new ResponseObject(
				rspBroker.RSP_GENERATED, properties, request.msg, request.ref, request.pid);
		
		eventAdmin.postEvent(rspGeneratedEvent);
    }

	@Override
	public void handleEvent(Event event) {
		handleEvent((RequestObject)event);		
	}
}
