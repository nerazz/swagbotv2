package swagbot.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import swagbot.Bot;
import swagbot.util.DbLink;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;

/**
 * @author Niklas Zd
 * @since 25.09.17
 */
public final class UserJoinListener implements IListener<UserJoinEvent> {
	private static final Logger LOGGER = LogManager.getLogger(UserJoinListener.class);


	@Override
	public void handle(UserJoinEvent event) {
		LOGGER.info(event);
		DbLink.getInstance().upsertUser(event.getUser());
		event.getUser().addRole(Bot.INSTANCE.getClient().getGuilds().get(0).getRolesByName("Newfags").get(0));
	}
}
