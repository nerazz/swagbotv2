package swagbot.util.impl;

import swagbot.objects.UserData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import swagbot.util.DbLink;
import swagbot.util.UserCache;
import swagbot.util.exceptions.UserNotFoundException;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author Niklas Zd
 * @since 25.09.17
 */
public class UserCacheImpl implements UserCache {
	private static final Logger LOGGER = LogManager.getLogger(UserCacheImpl.class);
	private final ConcurrentMap<Long, UserCacheObject> USER_CACHE = new ConcurrentHashMap<>();// IDEA: 26.09.17 oder lieber Collection.synchronized(Hashmap);
	private static DbLink dbLink;
	private static UserCacheImpl instance = new UserCacheImpl();

	private UserCacheImpl() {}

	public static UserCacheImpl getInstance() {
		return instance;
	}

	public static void setDbLink(DbLink setDbLink) {
		dbLink = setDbLink;
	}

	/**
	 * gets user from cache or adds it if nonexistent
	 *
	 * @param id id of user to be retrieved
	 * @return retrieved userdata
	 */
	@Override
	public UserData getUserData(long id) {//TODO: throws userNotFoundException
		if(!USER_CACHE.containsKey(id)) {
			try {
				UserData userData = dbLink.loadUser(id);
				putInCache(userData);
				return userData;
			} catch(UserNotFoundException e) {
				LOGGER.catching(e);
			}
		}
		return USER_CACHE.get(id).setAccessed(true).getUserData();

			/*synchronized (USER_CACHE) {
			if (!USER_CACHE.containsKey(id)) {
				try {
					UserCacheObject uco = new UserCacheObject(loadUserFromDb(user));
					USER_CACHE.put(id, uco);
				} catch(UserNotFoundException e) {
					e.printStackTrace();//TODO: log
				}
			}
			UserCacheObject uco = USER_CACHE.get(id);
			uco.setAccessed(true);
			return uco.getUserData();
		}*/
	}

	/**
	 * gets user from cache or adds it if nonexistent
	 *
	 * @param user user to be retrieved
	 * @return retrieved userdata
	 */
	@Override
	public UserData getUserData(IUser user) {//TODO: add if nonexistent; putIfAbsent
		return getUserData(user.getLongID());
	}

	/**
	 * gets a list of users from cache and adds all nonexistent users
	 * @param userList requested users
	 * @return list of userdata
	 */
	@Override
	public List<UserData> getUserData(List<IUser> userList) {
		List<IUser> missingUsers = new ArrayList<>();
		for (IUser user : userList) {
			if (!USER_CACHE.containsKey(user.getLongID())) {
				missingUsers.add(user);
			}
		}
		putInCache(dbLink.loadUsers(missingUsers));

		List<UserData> userDataList = new ArrayList<>();
		Long id;
		UserData ud;
		for (IUser user : userList) {
			id = user.getLongID();
			ud = USER_CACHE.get(id).setAccessed(true).getUserData();
			userDataList.add(ud);
		}
		return userDataList;
	}

	/**
	 * adds userdata to cache
	 *
	 * @param userData userdata to be added
	 */
	private void putInCache(UserData userData) {//IDEA: return boolean success?
		USER_CACHE.put(userData.getId(), new UserCacheObject(userData));
		/*synchronized (USER_CACHE) {
			USER_CACHE.put(userData.getId(), new UserCacheObject(userData));
		}*/
	}

	private void putInCache(List<UserData> usersToPut) {
		for (UserData userData : usersToPut) {
			USER_CACHE.put(userData.getId(), new UserCacheObject(userData));
		}
	}

	public void uploadCachedUsers() {
		List<UserData> cachedUserData = USER_CACHE.values().stream().map(UserCacheObject::getUserData).collect(Collectors.toList());
		DbLink.getInstance().update(cachedUserData);
	}

	/**
	 * clears not accessed users since last call to this method and sets accessed to false otherwise
	 */
	@Override
	public void clean() {// TODO: 27.09.17 besser!!
		List<Long> keysToDelete = new ArrayList<>();// IDEA: 26.09.17 mit iterator machen und einfach removen
		List<UserData> usersToDelete = new ArrayList<>();
		for (Map.Entry<Long, UserCacheObject> entry : USER_CACHE.entrySet()) {
			if (!entry.getValue().isAccessed()) {
				keysToDelete.add(entry.getKey());
				usersToDelete.add(entry.getValue().getUserData());
			} else {
				entry.getValue().setAccessed(false);
			}
		}
		System.out.println("deleting " + keysToDelete.size() + " entries");
		if (keysToDelete.size() > 0) {
			DbLink.getInstance().update(usersToDelete);
			for (Long key : keysToDelete) {
				synchronized (USER_CACHE) {
					USER_CACHE.remove(key);
				}
			}
		}
	}

	private static class UserCacheObject {
		private final UserData USER_DATA;
		private boolean accessed = true;

		UserCacheObject(UserData userData) {
			USER_DATA = userData;
		}

		UserData getUserData() {
			return USER_DATA;
		}

		UserCacheObject setAccessed(boolean accessed) {
			this.accessed = accessed;
			return this;
		}

		boolean isAccessed() {
			return accessed;
		}
	}
}
