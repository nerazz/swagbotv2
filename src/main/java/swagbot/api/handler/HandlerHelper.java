package swagbot.api.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.client.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author Niklas Zd
 * @since 28.10.2018
 */
public final class HandlerHelper {
	private static final Logger LOGGER = LogManager.getLogger(HandlerHelper.class);

	public static String getContent(InputStream is) {
		String content = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			content = br.lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException e) {
			LOGGER.catching(e);
		}
		System.out.println("content: " + content);
		return content;
	}

}
