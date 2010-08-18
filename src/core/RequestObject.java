package core;

import java.util.Map;

import org.osgi.service.event.Event;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangRef;

public class RequestObject extends Event {

	public OtpErlangPid pid;
	public OtpErlangRef ref;
	public OtpErlangObject msg;
	
	public RequestObject(String topic, Map properties) {
		super(topic, properties);
	}
	
	public RequestObject(String topic, Map properties, OtpErlangObject msg_obj, OtpErlangRef msg_ref, OtpErlangPid msg_pid) {
		super(topic, properties);
		pid = msg_pid;
		ref = msg_ref;
		msg = msg_obj;
	}

	protected class Request {
		
		private OtpErlangRef ref;
		
		private UserCtx userCtx;
		
		OtpErlangRef getRef() {
			return ref;
		}
		
		UserCtx getUserCtx() {
			return userCtx;
		}
	}
	
	protected class UserCtx {

		String db;
		String name;
		String roles;
		String cookie;
		String query;
		String peer;
		String path;
		String id;
	}
}
