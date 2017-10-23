package swagbot.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import swagbot.Objects.Impl.UserDataImpl;
import swagbot.Objects.UserData;
import swagbot.util.impl.UserCacheImpl;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

/**
 * @author Niklas Zd
 * @since 29.09.17
 */
public class Posts {
	private static final Logger LOGGER = LogManager.getLogger(Posts.class);
	private static final String NUMBERS[] = {":zero:", ":one:", ":two:", ":three:", ":four:", ":five:", ":six:", ":seven:", ":eight:", ":nine:"};


	private Posts() {}

	public static void gems(IMessage message) {
		EmbedBuilder eb = new EmbedBuilder();
		IUser user = message.getAuthor();
		eb.withAuthorName(user.getName());
		//eb.withAuthorIcon();
		eb.withColor(40, 181, 224);
		eb.withDesc(":gem:" + numToString(UserCacheImpl.getInstance().getUserData(user).getGems()) + ":gem:");
		RequestBuffer.request(() -> message.getChannel().sendMessage(eb.build()));
	}

	public static void stats(IMessage message) {
		EmbedBuilder eb = new EmbedBuilder();
		IUser user = message.getAuthor();
		UserData userData = UserCacheImpl.getInstance().getUserData(user);
		eb.withAuthorName(user.getName());
		eb.withColor(255, 121, 25);
		eb.withDesc("Stats");
		eb.appendField("Level", numToString(userData.getLevel()), true);
		eb.appendField("Exp", userData.getExp() + " / " + UserDataImpl.getLevelThreshold(userData.getLevel()), true);
		RequestBuffer.request(() -> message.getChannel().sendMessage(eb.build()));
	}

	public static void error(IMessage message) {
		RequestBuffer.request(() -> message.getChannel().sendMessage("Command nicht gefunden :/"));
	}

	private static String numToString(int n) {
		StringBuilder builder = new StringBuilder();
		while (n > 0) {
			int digit = n % 10;
			n /= 10;
			builder.insert(0, NUMBERS[digit]);
		}
		return builder.toString();
	}

}
