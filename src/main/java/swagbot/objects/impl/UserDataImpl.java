package swagbot.objects.impl;

import swagbot.objects.UserData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.handle.obj.IUser;

import java.text.DecimalFormat;

/**
 * @author Niklas Zd
 * @since 25.09.17
 */
public class UserDataImpl implements UserData {//TODO: atomics oder synchronized benutzen
	private static final Logger LOGGER = LogManager.getLogger(UserDataImpl.class);

	private final long ID;// IDEA: 28.09.17 feld entfernen und einfach von user durchreichen?
	private final IUser USER;
	private int ticks;
	private long lastSeen;
	private int gems;//TODO: getter exceptions?
	private int level;
	private int exp;
	private int swagLevel;
	private int swagPoints;
	private int reminder;
	private int expRate;//1000 == 100%
	private int potDuration;

	public UserDataImpl(IUser user,
						int ticks,
						long lastSeen,
						int gems,
						int level,
						int exp,
						int swagLevel,
						int swagPoints,
						int reminder,
						int expRate,
						int potDuration) {
		ID = user.getLongID();
		USER = user;
		this.ticks = ticks;
		this.lastSeen = lastSeen;
		this.gems = gems;
		this.level = level;
		this.exp = exp;
		this.swagLevel = swagLevel;
		this.swagPoints = swagPoints;
		this.reminder = reminder;
		this.expRate = expRate;
		this.potDuration = potDuration;
	}

	@Override
	public long getId() {
		return ID;
	}

	@Override
	public String getName() {
		return USER.getName();
	}

	@Override
	public IUser getUser() {
		return USER;
	}

	@Override
	public void tick() {
		ticks++;
		if (swagLevel > 0) {
			double sPoints = (double) swagPoints;
			addGems((int) Math.round(3.0 + sPoints / 5.0 * (sPoints / (sPoints + 5.0) + 1.0)));
		} else {
			addGems(3);
		}
		int exp = (int) ((Math.round(Math.random() * 3.0) + 4.0 + swagLevel) * expRate) / 1000;
		addExp(exp);
		reducePotDur();
	}

	@Override
	public int getTicks() {
		return ticks;
	}

	@Override
	public int getGems() {
		return gems;
	}

	@Override
	public void addGems(int gems) {
		this.gems += gems;
	}

	@Override
	public void subGems(int gems) {
		this.gems -= gems;// IDEA: 25.09.17 check if negativ -> exception?
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public int getExp() {
		return exp;
	}

	@Override
	public void addExp(int addedExp) {
		exp += addedExp;
		while (exp >= getLevelThreshold(level)) {
			exp -= getLevelThreshold(level);
			level++;
			//post(":tada: DING! " + name + " is now level " + level + "! :tada:", Statics.tempBotSpam);//TODO: nur auf guilds posten, auf denen der user ist!!!
			LOGGER.info("{} leveled to Level {}", getName(), level);
		}
	}

	@Override
	public void prestige() {
		if (level < 100) {
			LOGGER.info("{} Level ist nicht hoch genug zum prestigen", getName());
			//post(name + ", you have to be at least level 100.", Statics.tempBotSpam);//TODO: post
			return;
		}
		/*int swagPointGain = (int)Math.ceil(Math.sqrt((double)gems / 10000.0) * ((double)swagLevel + 2.0) / ((double)swagPoints + 2.0)) + level - 100;
		swagPoints += swagPointGain;//TODO: bei stats o.Ä. theoretische SP anzeigen + gems zum nächesten
		LOGGER.info("{} gained {} swagPoints by abandoning {} G", getName(), swagPointGain, gems);
		gems = 0;
		//TODO: ordentliches gem-abziehen, nicer post
		level = 1;
		swagLevel++;
		LOGGER.info("{} now is swagLevel {} with {} swagPoints", getName(), swagLevel, swagPoints);//TODO: wieviele gems wurden abgezogen?*/
	}

	@Override
	public int getSwagLevel() {
		return swagLevel;
	}

	@Override
	public int getSwagPoints() {
		return swagPoints;
	}

	@Override
	public int getReminder() {
		return reminder;
	}

	@Override
	public void addReminder(int anzahl) {

	}

	@Override
	public void negateReminder() {

	}

	@Override
	public int getExpRate() {
		return expRate;
	}

	@Override
	public void setExpRate(int expRate) {
		this.expRate = expRate;
	}

	@Override
	public int getPotDuration() {
		return potDuration;
	}

	@Override
	public void setPotDuration(int potDuration) {
		this.potDuration = potDuration;
	}

	@Override
	public void reducePotDur() {
		if (potDuration > 0) {
			potDuration -= 1;
			if (potDuration < 1) {
				setExpRate(1000);
				LOGGER.info("{} XPot empty", USER.getName());
				/*if (reminder > 0) {
					post("Hey, your XPot is empty...", user);//TODO: kauf und staffelung prüfen
					LOGGER.info("{} got reminded", name);
					reminder--;
				}*/
			}
		}
	}

	@Override
	public String getFormattedExpRate() {
		return new DecimalFormat("#.##").format(((double)expRate) / 1000);
	}

	@Override
	public boolean update() {
		return false;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		if (that == null) {
			return false;
		}
		if (getClass() != that.getClass()) {// IDEA: 27.09.17 oder instanceOf benutzen??
			return false;
		}
		return this.ID == ((UserDataImpl) that).ID;
	}

	public static int getLevelThreshold(int level) {//TODO: angucken; auslagern nach util
		//level--;
		if (level < 1) {
			LOGGER.error("level is < 1; getLevelThreshold({})", level);
			throw new IllegalArgumentException("Level darf nicht < 1 sein!");
		} else if (level < 100) {
			return level * 80 + 1000;
		} else {
			return 10000 + 7500 * (int)Math.round(Math.pow(level - 100, 1.5));
		}
	}
}
