package swagbot.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import swagbot.Bot;
import swagbot.util.TickTimer;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ReadyListener implements IListener<ReadyEvent> {
	private static final Logger LOGGER = LogManager.getLogger(ReadyListener.class);//TODO: package dazu?
	private static final int TICK_RATE = 60;//in seconds TODO: load from properties

	@Override
	public void handle(ReadyEvent event) {
		LOGGER.info(event);
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		TickTimer tickTimer = new TickTimer();
		try {
			executor.scheduleAtFixedRate(tickTimer, 5, TICK_RATE, TimeUnit.SECONDS);
		} catch(Exception e) {
			LOGGER.catching(e);
		}
	}

}
