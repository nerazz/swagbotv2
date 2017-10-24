package swagbot.util;

import swagbot.Bot;
import swagbot.objects.impl.UserDataImpl;
import swagbot.objects.UserData;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import swagbot.util.exceptions.UserNotFoundException;
import swagbot.util.impl.UserCacheImpl;
import sx.blah.discord.handle.obj.IUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niklas Zd
 * @since 26.09.17
 */
public class DbLink {
	private static final Logger LOGGER = LogManager.getLogger(DbLink.class);
	private static HikariDataSource dataSource;
	private static DbLink instance;

	private DbLink() {}

	public static void init(String hikari) {
		HikariConfig config = new HikariConfig(hikari);
		dataSource = new HikariDataSource(config);
		instance = new DbLink();
		UserCacheImpl.setDbLink(instance);
	}

	public static DbLink getInstance() {
		return instance;
	}


	public void upsertUsers(List<IUser> usersToUpsert) {
		String upsertQuery = "INSERT INTO SwagbotUsers (id, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE id = id";
		try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(upsertQuery)) {
			for (IUser user : usersToUpsert) {
				ps.setLong(1, user.getLongID());
				ps.setString(2, user.getName());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
	}

	public void upsertUser(IUser user) {
		String upsertQuery = "INSERT INTO SwagbotUsers (id, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE id = id";
		try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(upsertQuery)) {
			ps.setLong(1, user.getLongID());
			ps.setString(2, user.getName());
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
	}

	public UserData loadUser(long id) throws UserNotFoundException {
		String selectQuery = "SELECT id, ticks, lastSeen, gems, level, exp, expRate, potDuration, reminder, swagPoints, swagLevel FROM SwagbotUsers WHERE id = ?";
		try(Connection con = dataSource.getConnection(); PreparedStatement ps = con.prepareStatement(selectQuery)) {
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return new UserDataImpl(
						Bot.INSTANCE.getClient().getUserByID(rs.getLong("id")),//TODO: bestimmt besser möglich (user gab es vorher)
						rs.getInt("ticks"),
						rs.getLong("lastSeen"),
						rs.getInt("gems"),
						rs.getInt("level"),
						rs.getInt("exp"),
						rs.getInt("swagLevel"),
						rs.getInt("swagPoints"),
						rs.getInt("reminder"),
						rs.getInt("expRate"),
						rs.getInt("potDuration"));
			}
		} catch(SQLException e) {
			LOGGER.catching(e);
		}
		throw new UserNotFoundException();
	}


	public List<UserData> loadUsers(List<IUser> usersToLoad) {//IDEA: idsToLoad?
		if (usersToLoad.isEmpty()) {
			return new ArrayList<>();
		}
		String selectQuery = "SELECT id, ticks, lastSeen, gems, level, exp, expRate, potDuration, reminder, swagPoints, swagLevel FROM SwagbotUsers WHERE id IN (";
		StringBuilder sb = new StringBuilder();
		sb.append(selectQuery);
		for (IUser user : usersToLoad) {
			//selectQuery += user.getID() + ", ";
			sb.append(user.getLongID()).append(", ");
		}
		//selectQuery = selectQuery.substring(0, selectQuery.length() - 2).concat(")");
		sb.delete(sb.length() - 2, sb.length()).append(")");
		selectQuery = sb.toString();

		List<UserData> loadedUsers = new ArrayList<>(usersToLoad.size());
		try(Connection con = dataSource.getConnection(); PreparedStatement ps = con.prepareStatement(selectQuery)) {
			ResultSet rs = ps.executeQuery();
			UserDataImpl userData;
			while (rs.next()) {
				userData = new UserDataImpl(
						Bot.INSTANCE.getClient().getUserByID(rs.getLong("id")),//TODO: bestimmt besser möglich (user gab es vorher)
						rs.getInt("ticks"),
						rs.getLong("lastSeen"),
						rs.getInt("gems"),
						rs.getInt("level"),
						rs.getInt("exp"),
						rs.getInt("swagLevel"),
						rs.getInt("swagPoints"),
						rs.getInt("reminder"),
						rs.getInt("expRate"),
						rs.getInt("potDuration"));
				loadedUsers.add(userData);
			}
		} catch(SQLException e) {
			LOGGER.catching(e);
		}
		return loadedUsers;
	}

	public void update(List<UserData> usersToUpdate) {
		if (usersToUpdate.isEmpty()) {
			return;
		}
		String updateQuery =
				"UPDATE SwagbotUsers SET " +
						"ticks = ?, " +
						"lastSeen = ?, " +
						"gems = ?, " +
						"level = ?, " +
						"exp = ?, " +
						"expRate = ?, " +
						"potDuration = ?, " +
						"reminder = ?, " +
						"swagLevel = ?, " +
						"swagPoints = ? " +
				"WHERE id = ?";
		long currentTimestamp = System.currentTimeMillis();
		try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(updateQuery)) {
			for (UserData ud : usersToUpdate) {
				ps.setInt(1, ud.getTicks());
				ps.setLong(2, currentTimestamp);
				ps.setInt(3, ud.getGems());
				ps.setInt(4, ud.getLevel());
				ps.setInt(5, ud.getExp());
				ps.setInt(6, ud.getExpRate());
				ps.setInt(7, ud.getPotDuration());
				ps.setInt(8, ud.getReminder());
				ps.setInt(9, ud.getSwagLevel());
				ps.setInt(10, ud.getSwagPoints());
				ps.setLong(11, ud.getId());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
	}

	public boolean addLinkedId(long userId, String secret) {//TODO: für alle methoden boolean-return-successwerte einführen
		String updateQuery = "UPDATE Users SET linkedId = ? WHERE secret = ?;";// IDEA: 03.10.17 auch id zum schnelleren finden übergeben?
		try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(updateQuery)) {
			ps.setLong(1, userId);
			ps.setString(2, secret);
			int updatedRows = ps.executeUpdate();
			conn.commit();
			if (updatedRows > 0) {
				return true;
			}
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
		return false;
	}

}
