package swagbot.api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.stream.Collectors;

import swagbot.api.handler.HandlerHelper;
import swagbot.api.json.Request;

/**
 * @author Niklas Zd
 * @since 28.10.2018
 */
public class SwagbotInfoHandler implements HttpHandler {
	private static final Logger LOGGER = LogManager.getLogger(SwagbotInfoHandler.class);

	@Override
	public void handle(HttpExchange t) {
		try {
			InputStream is = t.getRequestBody();
			String content = HandlerHelper.getContent(is);

			//TODO: auth!
			switch (t.getRequestMethod()) {
				case "GET":
					//ignore body
					System.out.println("get");
					String params = t.getRequestURI().getRawQuery();
					System.out.printf("params: %s", params);
					break;
				case "POST":
					System.out.println("post");
					//Request r = new Gson().fromJson(content, Request.class);
					//if (!r.key.equals("key")) TODO: error zurückschicken (mit 403 oder so forbidden)
					//TODO: enum mit allen feldern in user zum checken?

					//System.out.println("content: " + content);
					break;
				default:
					System.out.println("error: weder get noch post method");
			}


			String response = "{\"data\": \"This is the response\"}";
			t.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:3000");
			//TODO: change to neraz.skyleo.de, muss vielleicht noch angepasst werden für alle sub-seiten
			//t.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
			t.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST");
			t.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
			t.getResponseHeaders().add("content-type", "application/json; charset=UTF-8");
			t.sendResponseHeaders(200, response.getBytes().length);

			System.out.println("sending headers: " + t.getResponseHeaders().keySet() + ":::" + t.getResponseHeaders().values());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
			System.out.println("-------------------------");

		} catch (IOException e) {
			LOGGER.catching(e);
		}
		t.close();
	}

	private void checkPostParam(String param) {//TODO: kein void return
		switch (param) {
			case "gems":
				return;
		}
	}


}
