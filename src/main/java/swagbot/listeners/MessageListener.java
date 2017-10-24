package swagbot.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import swagbot.Bot;
import swagbot.commands.Posts;
import swagbot.util.DbLink;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Niklas Zd
 * @since 25.09.17
 */
public final class MessageListener implements IListener<MessageReceivedEvent> {//TODO: refactor
	private static final Logger LOGGER = LogManager.getLogger(MessageListener.class);
	private static final long ADMIN_ID = 97092184821465088L;

	@Override
	public void handle(MessageReceivedEvent event) {//TODO: besseres handling!!!
		IMessage message = event.getMessage();
		String channelName = event.getChannel().getName().toLowerCase();
		if (channelName.equals("botspam") || message.getChannel().isPrivate()) {
			botspam(message);
		} else if (channelName.equals("general")) {
			general(message);
		}

	}

	private void botspam(IMessage message) {
		String content = message.getContent().toLowerCase();
		if (!content.startsWith("!")) {
			if (content.startsWith("§") && message.getAuthor().getLongID() == ADMIN_ID) {
				if (content.equals("§folo")) {
					Bot.INSTANCE.logout();
				}
			}
			return;
		}

		switch(content) {
			case "!gems":
				Posts.gems(message);
				break;
			case "!stats":
				Posts.stats(message);
				break;
			default:
				if (content.startsWith("!link")) {
					Pattern pattern = Pattern.compile("^!link (\\w{32})$");
					Matcher matcher = pattern.matcher(content);
					if (matcher.find()) {
						boolean success = DbLink.getInstance().addLinkedId(message.getAuthor().getLongID(), matcher.group(1));
						if (success) {
							RequestBuffer.request(() -> message.getChannel().sendMessage("Gelinkte ID geupdated!"));
						} else {
							RequestBuffer.request(() -> message.getChannel().sendMessage("Fehler beim linken!"));
						}
					}
				} else {
					Posts.error(message);
				}
		}
	}

	private void general(IMessage message) {
		String content = message.getContent().toLowerCase();
		Pattern pattern = Pattern.compile("www.|https?://");
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			IChannel linkChannel = Bot.INSTANCE.getClient().getGuilds().get(0).getChannelsByName("link-o-mat").get(0);
			RequestBuffer.request(() -> linkChannel.sendMessage("Repost von " + message.getAuthor() + ":\n" + message.getContent()));
			RequestBuffer.request(message::delete);
		}
	}


}
