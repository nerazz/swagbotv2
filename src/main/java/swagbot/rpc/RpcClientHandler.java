package swagbot.rpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import swagbot.Bot;
import swagbot.objects.UserData;
import swagbot.util.impl.UserCacheImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Niklas Zd
 * @since 23.10.2017
 */
public class RpcClientHandler implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(RpcClientHandler.class);

	private final Socket CLIENT;

	RpcClientHandler(Socket client) {
		this.CLIENT = client;
	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(CLIENT.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String content = getContent(br);
			System.out.println(content);
			RequestData rd = getRequestData(content);


			//TestUser user = new TestUser();
			//System.out.println(user.toString());

			String result = callMethod(rd);
			//System.out.println(user.toString());

			String response = buildResponse(rd.getId(), result);

			System.out.println("sending:\n" + response);
			CLIENT.getOutputStream().write(response.getBytes("UTF-8"));

			CLIENT.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private String getContent(BufferedReader br) {
		try {
			String line;
			int contentLength = -1;
			while (!(line = br.readLine()).isEmpty()) {//until end of head
				System.out.println(line);
				if (line.startsWith("Content-Length:")) {
					contentLength = Integer.parseInt(line.substring(16));
				}
			}

			StringBuilder sb = new StringBuilder();
			while (contentLength-- > 0) {//read body
				sb.append((char) br.read());
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private RequestData getRequestData(String content) {
		Pattern p = Pattern.compile("\\{\"jsonrpc\":\"2.0\",\"method\":\"(\\w+)\",\"id\":(\\d+),\"params\":(.*)}");
		Matcher m = p.matcher(content);
		if (!m.matches()) {
			System.out.println("error beim parsen der json");
			return new RequestData(null, null, new String[0]);
		}
		String method = m.group(1);
		String id = m.group(2);
		String args = m.group(3);

		p = Pattern.compile("(\\w+)");
		m = p.matcher(args);
		List<String> argList = new ArrayList<>();
		while (m.find()) {
			argList.add(m.group(1));
		}
		System.out.println("paramListLen = " + argList.size());
		String[] argArray = argList.toArray(new String[argList.size()]);
		return new RequestData(id, method, argArray);
	}

	private String callMethod(RequestData rd) {
		String result = "error";
		UserData userData = UserCacheImpl.getInstance().getUserData(Long.valueOf(rd.getArgs()[0]));
		switch(rd.getMethod()) {
			case "getGems":
				result = String.valueOf(userData.getGems());
				break;
			case "buyXpot":
				result = "\"" + Xpot.use(userData, Integer.parseInt(rd.getArgs()[1]), Integer.parseInt(rd.getArgs()[2]), Integer.parseInt(rd.getArgs()[3])) + "\"";
				break;
		}
		return result;
	}

	private String buildResponse(String id, String result) {
		String response = "HTTP/1.1 200 OK\n";
		//response += "Content-Length: " + content.length();//TODO: benötigt?
		response += "Connection: close\n";
		response += "Content-Type: application/json\n";
		response += "\r\n";
		//if (result.equals("error"))
		response += "{\"jsonrpc\": \"2.0\", \"result\": " + result + ", \"id\": " + id + "}";//TODO: jsonrpc specs einhalten
		return response;
	}
}
