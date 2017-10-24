package swagbot.util;

import swagbot.objects.UserData;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public interface UserCache {
	UserData getUserData(IUser user);
	List<UserData> getUserData(List<IUser> userList);
	UserData getUserData(long id);
	void clean();
}
