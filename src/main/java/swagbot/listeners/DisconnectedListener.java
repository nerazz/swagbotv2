package swagbot.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent;

/**
 * @author Niklas Zd
 * @since 25.09.17
 */
public final class DisconnectedListener implements IListener<DisconnectedEvent> {
	private static final Logger LOGGER = LogManager.getLogger(DisconnectedListener.class);

	@Override
	public void handle(DisconnectedEvent event) {
		LOGGER.warn("swagbot.Bot disconnected: {}", event.getReason());
	}
}
