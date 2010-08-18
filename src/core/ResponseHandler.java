package core;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class ResponseHandler implements EventHandler
{
	protected BundleContext context;
	
	public ResponseHandler(BundleContext bundleContext) {
		super();
		context = bundleContext;
	}

	public void handleEvent(ResponseObject response) {
		ServiceReference response_broker = context.getServiceReference(ResponseBroker.class.getName());
		ResponseBroker rspBroker = (ResponseBroker) context.getService(response_broker);
		
		rspBroker.sendMessage(response.pid, response.ref, response.msg);
	}

	@Override
	public void handleEvent(Event event) {
		handleEvent((ResponseObject)event);
		
	}
	
}
