package swagbot.rpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import swagbot.objects.UserData;

/**
 * @author Niklas Zd
 * @since 24.10.2017
 */
public class Xpot {
	private static final Logger LOGGER = LogManager.getLogger(Xpot.class);

	public static String use(UserData userData, int price, int rate, int duration) {
		//UserData uData = new UserData(user, 25);//gems, expRate, potDur
		if (userData.getGems() < price) {
			return "du hast nicht genug Gems.";
		}else if (userData.getPotDuration() > 0) {
			return String.format("Dein letzter XPot(x%s) wirkt noch für %d min!", userData.getFormattedExpRate(), userData.getPotDuration());
		} else {
			userData.subGems(price);
			userData.setExpRate(rate);
			userData.setPotDuration(duration);
			LOGGER.info("{} -> XPot for {} (x{})", userData.getName(), price, rate);
			return String.format("Der XPot erhöht deine Exprate auf x%s und scheint noch %d min zu wirken!", userData.getFormattedExpRate(), userData.getPotDuration());
			//userData.update();
		}
	}

}
