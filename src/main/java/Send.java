import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;

public class Send extends Thread {
	private int g_timeout = 200;
	public static String g_host = "127.0.0.1";
	public static int g_port = 12450;
	private static File g_file = null;

	private static Logger logger = new Logger();

	@Override
	public void run() {
		try {
			upload();
		} catch (Exception e) {
			logger.log("Issue with upload or thread was interrupted", 5);
			return;
		}
	}

	public Send() throws LocalhostException {
		if (g_host.equals("127.0.0.1"))
			throw new LocalhostException("User didn\'t supply a host and we cann\'t use the localhost as the host to connect to");
	}

	public Send(String host) throws LocalhostException {
		if (host.equals("127.0.0.1"))
			throw new LocalhostException("User supplied localhost, this is not allowed");
		else if (!g_host.equals("127.0.0.1")) {} //do nothing because it was set through the GUI
		else
			g_host = host;
	}

	public Send(String host, int port) throws InvalidPortException, LocalhostException {
		if (g_host.equals("127.0.0.1") && !host.equals("127.0.0.1"))
			g_host = host;
		else
			throw new InvalidPortException("User supplied localhost");

		boolean portApproved = false;

		for (int i = 0;i<FindHosts.allowedPorts.length;i++ ) {
			if (port == FindHosts.allowedPorts[i])
				portApproved = true;
		}

		if (!portApproved)
			throw new InvalidPortException("User specified a port that we do not use");

		g_port = port;
	}

	public Send(String host, int port, int timeout) throws InvalidPortException, LocalhostException {
		if (g_host.equals("127.0.0.1") && !host.equals("127.0.0.1"))
			g_host = host;
		else
			throw new InvalidPortException("User supplied localhost");

		boolean portApproved = false;

		for (int i = 0;i<FindHosts.allowedPorts.length;i++ ) {
			if (port == FindHosts.allowedPorts[i])
				portApproved = true;
		}

		if (!portApproved)
			throw new InvalidPortException("User specified a port that we do not use");

		g_port = port;
		g_timeout = timeout;
	}

	private static int upload() {
		logger.log("Starting upload", 1);

		Socket socket = null;
		BufferedWriter writer = null;
		InputStream input = null;

		if (g_host.equals("127.0.0.1") || (g_file == null)) {
			return 1;
		}

		try {
			input = new FileInputStream(g_file);
			logger.log("Getting file", 1);
			socket = new Socket(g_host, g_port);
			logger.log(("Connected to host at " + g_host + " on port " + g_port), 1);
		} catch (Exception e) {
			logger.log("Couldn\'t either connect to host or open file", 5);
			return 1;
		}

		try {
			for (int i = 0;i < input.available();i+=4096) {
				byte[] buffer = new byte[4096];
				logger.log("Reading file into buffer", 1);

				if (i > input.available())
					input.read(buffer);
				else
					input.read(buffer, i, 4096);

				logger.log("Writing buffer to socket", 1);
				socket.getOutputStream().write(buffer);
			}
		} catch (Exception e) {
			logger.log("File IO issue", 5);
			return 1;
		}

		try {
			logger.log("Closing socket", 1);
			socket.close();
			logger.log("Closing InputStream", 1);
			input.close();
		} catch (Exception e) {
			logger.log("Couldn\'t close socket or InputStream", 4);
			return 1;
		}
		return 0;
	}

	public static void setPortThroughGUI(int port) throws InvalidPortException {
		boolean portApproved = false;

		for (int i = 0;i<FindHosts.allowedPorts.length;i++ ) {
			if (port == FindHosts.allowedPorts[i])
				portApproved = true;
		}

		if (portApproved == false)
			throw new InvalidPortException("User specified a port that we do not use");

		g_port = port;
	}

	public static void setFileThroughGUI(String file) throws FileNotFoundException, FileIsDirectoryException {
		g_file = new File(file);

		if (!g_file.exists())
			throw new FileNotFoundException();
		else if(g_file.isDirectory())
			throw new FileIsDirectoryException();
	}

	public static boolean isFileSelected() {
		if (g_file != null)
			return true;

		return false;
	}
}
