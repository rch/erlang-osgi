package core.service;

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

import core.RequestObject;


public class MessageBroker extends Thread
{
	public static String MBOX_NAME = "msg";
	
	public static String MSG_GENERATED = "org/apache/couchdb/httpd/msg/GENERATED";
	
	protected volatile boolean active = true;

	protected BundleContext context;
	
	protected OtpNode node;
	
	protected OtpMbox mbox;

	public MessageBroker(BundleContext bundleContext, OtpNode node, String name) {
		context = bundleContext;
		if (name != null) {
			mbox = node.createMbox(name);
		} else {
			mbox = node.createMbox(MBOX_NAME);
		}
	}
	
	public void run() {
		OtpErlangObject obj;
		OtpErlangTuple msg;
		OtpErlangPid from;
		OtpErlangRef ref;
		while (active) {
			try {
				synchronized(this) {
					obj = mbox.receive(100);
				}
				if (obj instanceof OtpErlangTuple) {
					msg = (OtpErlangTuple)obj;
					from = (OtpErlangPid)(msg.elementAt(0));
					ref = (OtpErlangRef)(msg.elementAt(1));
					postMessage(from, ref, msg.elementAt(2));
				}			
			} catch (OtpErlangExit e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (OtpErlangDecodeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public void resumeThread() {
		active = true;
		start();
	}
	
	public void pauseThread() {
		active = false;
	}
	
	public void stopThread() {
		active = false;
		mbox.close();
	}
	
	public void sendMessage(OtpErlangPid pid, OtpErlangRef ref, OtpErlangObject msg) {
		OtpErlangTuple tuple = new OtpErlangTuple(new OtpErlangObject[] {ref, msg});
		mbox.send(pid, tuple);
	}
	
	protected void postMessage(OtpErlangPid pid, OtpErlangRef ref, OtpErlangObject msg) {
		
		ServiceReference srv = context.getServiceReference(EventAdmin.class.getName());
		EventAdmin eventAdmin = (EventAdmin) context.getService(srv);
	
		Map<String, OtpErlangObject> properties = new Hashtable<String, OtpErlangObject>();
		properties.put("pid", pid);
		properties.put("ref", ref);
		properties.put("msg", msg);
		
		Event reqGeneratedEvent = new Event(MSG_GENERATED, properties);
		
		eventAdmin.postEvent(reqGeneratedEvent);
	}
    
}
