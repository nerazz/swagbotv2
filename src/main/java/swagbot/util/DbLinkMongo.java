package swagbot.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Niklas Zd
 * @since 18.11.2018
 */
public class DbLinkMongo {
	/* TODO: ideen
	//insert discid into _id
	//use gsons JsonObject
	//von mongo direkt in user parsen -> zum updaten nur geänderte felder ändern
										->(muss atomic sein, care mit gambles & co während update)
	 */

	private static final Logger LOGGER = LogManager.getLogger(DbLinkMongo.class);
	private static DbLinkMongo instance;

	private DbLinkMongo() {}


}
