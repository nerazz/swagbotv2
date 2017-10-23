package swagbot.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import swagbot.Bot;
import swagbot.Objects.UserData;
import swagbot.util.impl.UserCacheImpl;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Niklas Zd
 * @since 26.09.17
 */
public class TickTimer implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(TickTimer.class);
	private static final IDiscordClient BOT_CLIENT = Bot.INSTANCE.getClient();

	/** minutes since bot start */
	private static int minutesOnline = 0;
	/** hours since bot start */
	private static int hoursOnline = 0;
	/** days since bot start */
	private static int daysOnline = 0;

	/**
	 * sets status text
	 */
	public TickTimer() {
		BOT_CLIENT.changePlayingText("fresh online");
	}

	/**
	 * handles updates on online timer and users
	 */
	@Override
	public void run() {
		System.out.println("tick");
		try {
			minutesOnline++;
			if ((minutesOnline % 5) == 0) {
				if ((minutesOnline % 60) == 0) {
					UserCacheImpl.getInstance().uploadCachedUsers();
					hoursOnline++;
					minutesOnline = 0;
					if ((hoursOnline % 24) == 0) {
						daysOnline++;
						hoursOnline = 0;//TODO: day++
						//SERVER_DATA.addDay();
					}
				}

				if (daysOnline != 0) {
					BOT_CLIENT.changePlayingText(String.format("seit %dd %dh online", daysOnline, hoursOnline));
				} else if (hoursOnline != 0) {
					BOT_CLIENT.changePlayingText(String.format("seit %dh %dm online", hoursOnline, minutesOnline));
				} else {
					BOT_CLIENT.changePlayingText(String.format("seit %dm online", minutesOnline));
				}
			}

			List<IUser> onlineUsers = BOT_CLIENT.getUsers().stream().filter(u -> u.getPresence().getStatus().equals(StatusType.ONLINE)).collect(Collectors.toList());
			System.out.println(onlineUsers.size() + " users online");

			List<UserData> onlineUserData = UserCacheImpl.getInstance().getUserData(onlineUsers);
			for (UserData ud : onlineUserData) {
				ud.tick();
			}

			UserCacheImpl.getInstance().clean();//TODO: nur jede Stunde oder so
		} catch(Exception e) {
			LOGGER.catching(e);//TODO: notwendig hier? testen (exception wird auch um Timer schon gefangen!)
		}
	}
}
