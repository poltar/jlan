import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.time.LocalDateTime;

public class Recieve extends Thread {
	public static int g_portToBindTo = 12450;

	private static DataInputStream g_reader = null;
	private static ServerSocket g_socket;

	private static Logger logger = new Logger();

	@Override
	public void run() {
		try {
			logger.log("Starting server", 1);
			startServer();
		} catch (Exception e) {
			logger.log("Hit exception during thread start and killing server", 5);
			killServer();
		}
	}

	public static String getIP() throws UnresolvableErrorException {
		String ip = "";

		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			throw new UnresolvableErrorException();
		}

		return ip;
	}

	private static void startServer() {
		File file = null;
		String date = LocalDateTime.now().toString();
		Path path = Paths.get("recieved" + date).toAbsolutePath();

		try {
			g_socket = new ServerSocket(g_portToBindTo);
			logger.log(("Binding to port " + g_portToBindTo), 1);
			g_socket.bind(new InetSocketAddress(getIP(), g_portToBindTo));
			Socket socket = g_socket.accept();
			logger.log("Accepting connection", 1);
			g_reader = new DataInputStream(socket.getInputStream());
			logger.log("Opening input stream on socket", 1);
			Files.createFile(path);
		} catch (Exception e) {
			logger.log("Error during init, killing server", 5);
			logger.log("Waiting for 1000 ms and restarting", 2);
			restartServer();
			return;
		}

		try {
			for (int i = 0;i < g_reader.available();i+=4096) {
				byte[] buffer = new byte[4096];
				logger.log("Reading connection into buffer", 1);

				if (i > g_reader.available())
					g_reader.read(buffer);
				else
					g_reader.read(buffer, i, 4096);

				logger.log(("Writing buffer to " + path.toString()), 1);
				Files.write(path, buffer);
			}
		} catch (Exception e) {
			logger.log("Can\'t read stream into buffer", 5);
			killServer();
			return;
		}
	}

	private static void killServer() {
		try {
			g_socket.close();
			g_reader.close();
		} catch (Exception e) {
			return;
		}
	}

	public static void setPortThroughGUI(int port) throws InvalidPortException {
		boolean portApproved = false;

		for (int i = 0;i<FindHosts.allowedPorts.length;i++ ) {
			if (port == FindHosts.allowedPorts[i])
				portApproved = true;
		}

		if (!portApproved)
			throw new InvalidPortException("User specified a port that we do not use");

		g_portToBindTo = port;
	}

	private static void restartServer() {
		try {
			Thread.sleep(1000);
			startServer();
		} catch (InterruptedException e) {
			logger.log("Server was interrupted during restart, killing", 4);
			killServer();
		}
	}
}
