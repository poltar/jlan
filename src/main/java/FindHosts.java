import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;

public class FindHosts extends Thread {
	private static int g_timeout = 200;

	public static HashMap<String,Integer> g_openhosts = null;

	public static final int[] allowedPorts = {12450, 12451, 12452, 12453, 12454, 12455};

	public FindHosts() {
		//probably don't need any default behavior, but we need this in case the user doesn't care about the default timeout value
	}

	public FindHosts(int timeout) {
		this();
		g_timeout = timeout;
	}

	@Override
	public void run() {
		try {
			g_openhosts = getOpenHosts();
			return;
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static HashMap<String, Integer> getOpenHosts() throws UnresolvableErrorException {
		HashMap<String, Integer> openhosts = new HashMap<String, Integer>();
		String subnetmask = "";
		String iip = "";

		try {
			iip = InetAddress.getLocalHost().getHostAddress();
			subnetmask = getSubnetMask();
		} catch (Exception e) {
			throw new UnresolvableErrorException("Program hit an error that it shouldn\'t have");
		}

		String[] hostRange = getHostRange(iip.split("\\."), subnetmask.split("\\."));

		for (String host : hostRange) {
			for (int i = 0; i < allowedPorts.length;i++) {
				if (isAvail(host, allowedPorts[i]))
					openhosts.put(host, allowedPorts[i]);
			}
		}

		return openhosts;
	}

	private static String getSubnetMask() throws Exception {
		InetAddress localhost = InetAddress.getLocalHost();
		NetworkInterface netinf = NetworkInterface.getByInetAddress(localhost);
		int subnetaddress = netinf.getInterfaceAddresses().get(0).getNetworkPrefixLength();

		switch (subnetaddress) {
			case 2:
				return "255.255.255.254";

			case 4:
				return "255.255.255.252";

			case 8:
				return "255.255.255.248";

			case 16:
				return "255.255.255.240";

			case 32:
				return "255.255.255.224";

			case 64:
				return "255.255.255.192";

			case 128:
				return "255.255.255.128";

			case 256:
				return "255.255.255.0";

			case 512:
				return "255.255.254.0";

			case 1024:
				return "255.255.252.0";

			case 2048:
				return "255.255.248.0";

			case 4096:
				return "255.255.240.0";

			case 8192:
				return "255.255.224.0";

			case 16384:
				return "255.255.192.0";

			case 32786:
				return "255.255.128.0";

			case 65536:
				return "255.255.0.0";

			case 131072:
				return "255.254.0.0";

			case 262144:
				return "255.252.0.0";

			case 524288:
				return "255.248.0.0";

			case 1048576:
				return "255.240.0.0";

			case 2097152:
				return "255.224.0.0";

			case 4194304:
				return "255.192.0.0";

			case 8388608:
				return "255.128.0.0";

			case 16777216:
				return "255.0.0.0";

			default:
				throw new Exception();

		}
	}

    private static boolean isAvail(String ip, int port) {
		try {
			Socket sock = new Socket();
			sock.connect(new InetSocketAddress(ip, port), g_timeout);
			sock.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static String[] getHostRange(String[] ip, String[] sub) {
		ArrayList<String> hostrange = new ArrayList<String>();
		int i;

		for (i = 0;i < sub.length;i++) {
			if (!sub[i].equals("255"))
				break;
		}

		switch(i) {
			case 3:
				for (i = Integer.parseInt(sub[3]);i <= 255;i++) {
					String host = ip[0] + "." + ip[1] + "." + ip[2] + "." + i;
					hostrange.add(host);
				}
				break;
			case 2:
				for (i = Integer.parseInt(sub[3]);i <= 255;i++) {
					for (int j = Integer.parseInt(sub[2]); j <= 255; j++) {
						String host = ip[0] + "." + ip[1] + "." + j + "." + i;
						hostrange.add(host);
					}
				}
				break;
			case 1:
				for (i = Integer.parseInt(sub[3]);i <= 255;i++) {
					for (int j = Integer.parseInt(sub[2]); j <= 255; j++) {
						for (int p = Integer.parseInt(sub[1]); p <= 255; p++) {
							String host = ip[0] + "." + p + "." + j + "." + i;
							hostrange.add(host);
						}
					}
				}
				break;
		}


		return hostrange.toArray(new String[hostrange.size()]);
	}
}
