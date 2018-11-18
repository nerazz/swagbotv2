package swagbot.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;

import swagbot.api.handler.SwagbotInfoHandler;

/**
 * @author Niklas Zd
 * @since 27.10.2018
 *
 * TODO: content-type
 * TODO: keep-alive timeout?
 */
public class ApiServer implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(ApiServer.class);
	private static final SwagbotInfoHandler swagbotInfoHandler = new SwagbotInfoHandler();


	@Override
	public void run() {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(8085), 100);
			server.createContext("/swagbot/info", swagbotInfoHandler);
			server.start();
		} catch (IOException e) {
			LOGGER.catching(e);
		}


		/*try {
			HttpServer server = HttpServer.create(new InetSocketAddress(8085), 100);
			HttpHandler handler = exchange -> {
				InputStream is = exchange.getRequestBody();
				//System.out.println("handling: " + is.read());
				String content;
				try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
					content = br.lines().collect(Collectors.joining(System.lineSeparator()));
				}
				System.out.println(content);
				System.out.println(exchange.getRequestURI().getRawQuery());
				System.out.println(exchange.getRequestMethod());

				String response = "This is the response";
				exchange.sendResponseHeaders(201, response.getBytes().length);
				OutputStream os = exchange.getResponseBody();
				System.out.println(exchange.getResponseHeaders().values());
				os.write(response.getBytes());
				os.close();

				exchange.close();
			};
			server.createContext("/test", handler);
			server.createContext("/swagbot/info", swagbotInfoHandler);
			server.start();
		} catch (IOException e) {
			LOGGER.catching(e);
		}*/
	}

}
