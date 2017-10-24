package swagbot.rpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Niklas Zd
 * @since 22.10.2017
 */
public class RequestData {
	private static final Logger LOGGER = LogManager.getLogger(RequestData.class);

	private final String ID;
	private final String METHOD;
	private final String[] ARGS;

	public RequestData(String id, String method, String[] args) {
		ID = id;
		METHOD = method;
		ARGS = args;
	}

	public int paramCount() {
		return ARGS.length;
	}

	public String getId() {
		return ID;
	}

	public String getMethod() {
		return METHOD;
	}

	public String[] getArgs() {
		return ARGS;
	}

}
