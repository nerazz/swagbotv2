package swagbot.util.impl;

import org.junit.Before;
import org.junit.Test;
import swagbot.Objects.Impl.UserDataImpl;
import swagbot.Objects.UserData;
import swagbot.util.DbLink;
import sx.blah.discord.api.internal.ShardImpl;
import sx.blah.discord.handle.impl.obj.Presence;
import sx.blah.discord.handle.impl.obj.User;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserCacheImplTest {
	private DbLink mockedDbLink = mock(DbLink.class);
	private UserCacheImpl userCache;

	@Before
	public void setUp() throws Exception {
		UserCacheImpl.setDbLink(mockedDbLink);
		userCache = UserCacheImpl.getInstance();
		System.out.println("setup");
	}

	//@Test
	public void getUserData() throws Exception {
		IUser u0 = mock(User.class);
		IUser u1 = mock(User.class);
		IUser u2 = mock(User.class);
		IUser u3 = mock(User.class);
		when(u0.getLongID()).thenReturn(0L);
		when(u1.getLongID()).thenReturn(1L);
		when(u2.getLongID()).thenReturn(2L);
		when(u3.getLongID()).thenReturn(3L);
		UserData ud0 = new UserDataImpl(u0, 12, 0L, 0, 0, 0, 0, 0, 0, 0, 0);
		UserData ud1 = new UserDataImpl(u1, 12, 0L, 0, 0, 0, 0, 0, 0, 0, 0);
		UserData ud2 = new UserDataImpl(u2, 12, 0L, 0, 0, 0, 0, 0, 0, 0, 0);
		UserData ud3 = new UserDataImpl(u3, 12, 0L, 0, 0, 0, 0, 0, 0, 0, 0);

		List<IUser> usersToLoad = new ArrayList<>();
		usersToLoad.add(u0);
		usersToLoad.add(u1);
		usersToLoad.add(u2);
		usersToLoad.add(u3);

		List<UserData> expectedResult = new ArrayList<>();
		expectedResult.add(ud0);
		expectedResult.add(ud1);
		expectedResult.add(ud2);
		expectedResult.add(ud3);

		when(mockedDbLink.loadUsers(usersToLoad)).thenReturn(expectedResult);
		verify(u0, atLeast(1)).getLongID();
		verify(u1, atLeast(1)).getLongID();
		verify(u2, atLeast(1)).getLongID();
		List<UserData> cachedUsers = userCache.getUserData(usersToLoad);
		assertTrue(cachedUsers.containsAll(expectedResult));
		userCache.clean();
		//assertEquals(result, userCache.getUserData(missingUsers));
		userCache.clean();
		assertTrue(cachedUsers.containsAll(expectedResult));
		//assertEquals(result, userCache.getUserData(missingUsers));


	}

	@Test
	public void clean() throws Exception {
	}

}