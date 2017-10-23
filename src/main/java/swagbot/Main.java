package swagbot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import swagbot.listeners.*;
import swagbot.util.DbLink;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	private static final Logger LOGGER = LogManager.getLogger(Main.class);
	private static final int BOT = 0;//0 == Testbot; 1 == Swagbot

	public static void main(String[] args) {//TODO: log4j2 wieder umstellen
		String config;
		if (BOT == 0) {
			config = "/testbot.properties";
		} else {
			config = "/swagbot.properties";
		}
		//InputStream Main.class.getContextClassLoader().getResourceAsStream(config);
		LOGGER.info("starting bot...");
		String token;
		try {
			Properties prop = new Properties();
			try (InputStream is = Main.class.getResourceAsStream(config)) {
				prop.load(is);
				token = prop.getProperty("token");
				String hikari = prop.getProperty("hikari");
				DbLink.init(hikari);
			} catch(IOException e) {
				LOGGER.catching(e);
				return;
			}
			IDiscordClient bot = Bot.INSTANCE.setClient(new ClientBuilder().withToken(token).setMaxReconnectAttempts(5).login());
			EventDispatcher dispatcher = bot.getDispatcher();
			dispatcher.registerListener(new ReadyListener());
			dispatcher.registerListener(new DisconnectedListener());
			dispatcher.registerListener(new GuildCreateListener());
			dispatcher.registerListener(new UserJoinListener());
			dispatcher.registerListener(new MessageListener());
		} catch(DiscordException e) {
			LOGGER.catching(e);
		}
	}

}
