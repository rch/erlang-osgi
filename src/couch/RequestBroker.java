package core;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.ericsson.otp.erlang.OtpErlangExit;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangRef;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;
import com.ericsson.otp.erlang.OtpNode;

import core.service.MessageBroker;

public class RequestBroker extends MessageBroker {

	public static String REQ_GENERATED = "org/apache/couchdb/httpd/req/GENERATED";
	
	public static String MBOX_NAME = "req";
	
	public RequestBroker(BundleContext bundleContext, OtpNode node) {
		super(bundleContext, node, MBOX_NAME);
	}
	
	@Override
	protected void postMessage(OtpErlangPid pid, OtpErlangRef ref, OtpErlangObject msg) {
		
		ServiceReference srv = context.getServiceReference(EventAdmin.class.getName());
		EventAdmin eventAdmin = (EventAdmin) context.getService(srv);
	
		Map<String, String> properties = new Hashtable<String, String>();
		properties.put("ref", ref.toString());
		
		Event reqGeneratedEvent = new RequestObject(REQ_GENERATED, properties, msg, ref, pid);
		
		eventAdmin.postEvent(reqGeneratedEvent);
	}
}
