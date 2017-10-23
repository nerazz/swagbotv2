package swagbot.Objects;

import sx.blah.discord.handle.obj.IUser;

public interface UserData {// TODO: 28.09.17 aufräumen; was brauche ich wirklich?
	long getId();
	String getName();
	IUser getUser();
	void tick();
	int getTicks();
	int getGems();
	void addGems(int gems);
	void subGems(int gems);
	int getLevel();
	int getExp();
	void addExp(int exp);//addExp return level oder boolean ob levelup?
	void prestige();
	int getSwagLevel();
	int getSwagPoints();
	int getReminder();
	void addReminder(int anzahl);
	void negateReminder();
	int getExpRate();
	void setExpRate(int expRate);
	int getPotDuration();
	void setPotDuration(int potDuration);
	void reducePotDur();
	String getFormattedExpRate();
	boolean update();
}
