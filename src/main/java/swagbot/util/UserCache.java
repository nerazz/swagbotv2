package swagbot.util;

import swagbot.Objects.UserData;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public interface UserCache {
	UserData getUserData(IUser user);
	List<UserData> getUserData(List<IUser> userList);
	void clean();
}
