package swagbot.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import swagbot.Bot;
import swagbot.util.DbLink;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.obj.IChannel;

/**
 *
 * @author Niklas Zd
 * @since 25.09.2017
 */
public final class GuildCreateListener implements IListener<GuildCreateEvent> {
	private static final Logger LOGGER = LogManager.getLogger(GuildCreateListener.class);

	@Override
	public void handle(GuildCreateEvent event) {
		LOGGER.info("joined {}!", event.getGuild().getName());
		IChannel botSpam = event.getGuild().getChannelsByName("botspam").get(0);
		if (botSpam == null) {// IDEA: 27.09.17 neuen createn?
			LOGGER.error("kein botSpam gefunden!");
			Bot.INSTANCE.logout();
			System.exit(1);
		}
		DbLink.getInstance().upsertUsers(Bot.INSTANCE.getClient().getUsers());
	}
}
