package swagbot.util.impl;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import swagbot.objects.impl.UserDataImpl;
import swagbot.objects.UserData;
import swagbot.util.DbLink;
import sx.blah.discord.handle.impl.obj.User;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserCacheImplTest {
	private DbLink mockedDbLink;
	private UserCacheImpl userCache;

	@BeforeClass
	public static void setUpOnce() {

	}

	@Before
	public void setUp() throws Exception {
		mockedDbLink = mock(DbLink.class);
		UserCacheImpl.setDbLink(mockedDbLink);
		userCache = UserCacheImpl.getInstance();
	}

	//@Test
	public void getUserData() throws Exception {
		IUser u1 = mock(User.class);
		IUser u2 = mock(User.class);
		IUser u3 = mock(User.class);
		IUser u4 = mock(User.class);
		when(u1.getLongID()).thenReturn(111L);
		when(u2.getLongID()).thenReturn(222L);
		when(u3.getLongID()).thenReturn(333L);
		when(u4.getLongID()).thenReturn(444L);
		UserData ud1 = new UserDataImpl(u1, 12, 0L, 0, 0, 0, 0, 0, 0, 0, 0);
		UserData ud2 = new UserDataImpl(u2, 12, 0L, 0, 0, 0, 0, 0, 0, 0, 0);
		UserData ud3 = new UserDataImpl(u3, 12, 0L, 0, 0, 0, 0, 0, 0, 0, 0);
		UserData ud4 = new UserDataImpl(u4, 12, 0L, 0, 0, 0, 0, 0, 0, 0, 0);

		List<IUser> userList = new ArrayList<>();
		userList.add(u1);
		userList.add(u2);
		userList.add(u3);
		userList.add(u4);

		List<UserData> expectedResult = new ArrayList<>();
		expectedResult.add(ud1);
		expectedResult.add(ud2);
		expectedResult.add(ud3);
		expectedResult.add(ud4);

		when(mockedDbLink.loadUsers(userList)).thenReturn(expectedResult);
		verify(u1, atLeast(1)).getLongID();
		verify(u2, atLeast(1)).getLongID();
		verify(u3, atLeast(1)).getLongID();
		//verify(u4, never()).getLongID();
		List<UserData> cachedUsers = userCache.getUserData(userList);
		assertTrue(cachedUsers.containsAll(expectedResult));

	}

	//@Test
	public void clean() throws Exception {
		IUser u1 = mock(User.class);
		IUser u2 = mock(User.class);
		IUser u3 = mock(User.class);
		IUser u4 = mock(User.class);
		when(u1.getLongID()).thenReturn(111L);
		when(u2.getLongID()).thenReturn(222L);
		when(u3.getLongID()).thenReturn(333L);
		when(u4.getLongID()).thenReturn(444L);
		UserData ud1 = new UserDataImpl(u1, 12, 0L, 0, 0, 0, 0, 0, 0, 0, 0);
		UserData ud2 = new UserDataImpl(u2, 12, 0L, 0, 0, 0, 0, 0, 0, 0, 0);
		UserData ud3 = new UserDataImpl(u3, 12, 0L, 0, 0, 0, 0, 0, 0, 0, 0);
		UserData ud4 = new UserDataImpl(u4, 12, 0L, 0, 0, 0, 0, 0, 0, 0, 0);

		List<IUser> userList = new ArrayList<>();
		userList.add(u1);
		userList.add(u2);
		userList.add(u3);
		userList.add(u4);

		List<UserData> expectedResult = new ArrayList<>();
		expectedResult.add(ud1);
		expectedResult.add(ud2);
		expectedResult.add(ud3);
		expectedResult.add(ud4);

		when(mockedDbLink.loadUsers(userList)).thenReturn(expectedResult);
		verify(u1, atLeast(1)).getLongID();
		verify(u2, atLeast(1)).getLongID();
		verify(u3, atLeast(1)).getLongID();
		//verify(u4, never()).getLongID();
		List<UserData> cachedUsers = userCache.getUserData(userList);
		//doReturn(mockedDbLink).when(DbLink.getInstance());
		//doNothing().when(mockedDbLink).update(Matchers.anyList());
		userCache.clean();
		userCache.getUserData(u2);
		userCache.clean();
		//assertTrue(!cachedUsers.contains(ud1));
		assertTrue(cachedUsers.contains(ud2));
		//assertTrue(!cachedUsers.contains(ud3));
		//assertTrue(!cachedUsers.contains(ud4));
		//assertEquals(result, userCache.getUserData(missingUsers));
	}

}