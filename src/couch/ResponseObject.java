package core;

import java.util.Map;

import org.osgi.service.event.Event;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangRef;

import core.RequestObject.UserCtx;

public class ResponseObject extends Event {

	public OtpErlangPid pid;
	public OtpErlangRef ref;
	public OtpErlangObject msg;
	
	private Response response;
	
	public ResponseObject(String topic, Map properties, OtpErlangObject msg_obj, OtpErlangRef msg_ref, OtpErlangPid msg_pid) {
		super(topic, properties);
		pid = msg_pid;
		ref = msg_ref;
		msg = msg_obj;
	}
	
	protected class Response {
		
		private OtpErlangRef ref;
		
		private String headers;
		
		private int code;
		
	}

}
