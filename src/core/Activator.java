package core;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import com.ericsson.otp.erlang.OtpNode;

public class Activator implements BundleActivator
{
	public static String LOCAL_NODE_NAME = "osgi@localhost";
	
	public static String OTHER_NODE_NAME = "couchext@localhost";
	
	private BundleContext context;
	
	private OtpNode node;
	private RequestBroker reqBroker;
	private ResponseBroker rspBroker;
	
    /**
     * @param context the framework context for the bundle.
    **/
    public void start(BundleContext bundleContext)
    {
    	context = bundleContext;
    	
    	try {
			node = new OtpNode(LOCAL_NODE_NAME);
			if (!node.ping(OTHER_NODE_NAME, 1000)) {
			//	System.out.println("could not connect to cmd node");
			}
			
			registerHandlers();
			createBrokers();
			
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }

    /**
     * @param context the framework context for the bundle.
    **/
    public void stop(BundleContext context) throws InterruptedException
    {
    	reqBroker.stopThread();
    	reqBroker.join();
    	
    	rspBroker.stopThread();
    	rspBroker.join();
    	
		node.close();
    }
 
    private void registerHandlers() {
    	String[] reqTopics = new String[] {"org/apache/couchdb/httpd/req/GENERATED"};
		Dictionary<String, String[]> reqProps = new Hashtable<String, String[]>();
		reqProps.put(EventConstants.EVENT_TOPIC, reqTopics);
		context.registerService(EventHandler.class.getName(), new RequestHandler(context), reqProps);
    	
		String[] rspTopics = new String[] {"org/apache/couchdb/httpd/rsp/GENERATED"};
		Dictionary<String, String[]> rspProps = new Hashtable<String, String[]>();
		rspProps.put(EventConstants.EVENT_TOPIC, rspTopics);
		context.registerService(EventHandler.class.getName(), new ResponseHandler(context), rspProps);
    }
    
    private void createBrokers() {
    	reqBroker = new RequestBroker(context, node);
    	reqBroker.start();
    	
    	context.registerService(RequestBroker.class.getName(), reqBroker, null);
    	
		rspBroker = new ResponseBroker(context, node);
		rspBroker.start();
		
		context.registerService(ResponseBroker.class.getName(), rspBroker, null);
    }
    
}
