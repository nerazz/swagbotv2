package swagbot.rpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Niklas Zd
 * @since 22.10.2017
 */
public class RpcSocket implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(RpcSocket.class);
	private ServerSocket server;

	public RpcSocket() {
		try {
			server = new ServerSocket(8085);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		System.out.println("Listening...");
		for(;;) {//TODO: multithreaded client handling
			//ExecutorService executor = Executors.newFixedThreadPool(5);
			ExecutorService executor = Executors.newSingleThreadExecutor();
			try {
				Socket client = server.accept();
				executor.execute(new RpcClientHandler(client));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
