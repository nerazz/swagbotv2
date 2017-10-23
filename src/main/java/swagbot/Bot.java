package swagbot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import swagbot.util.impl.UserCacheImpl;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;

public enum Bot {
	INSTANCE;

	private static final Logger LOGGER = LogManager.getLogger(Bot.class);
	private IDiscordClient client;

	public IDiscordClient setClient(IDiscordClient client) {
		this.client = client;
		return client;
	}

	public IDiscordClient getClient() {
		return client;
	}

	public void logout() {
		UserCacheImpl.getInstance().uploadCachedUsers();
		client.logout();
		System.exit(0);
	}


}
