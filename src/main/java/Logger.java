import java.io.*;
import java.util.*;
import java.lang.*;
import java.nio.file.*;
import java.time.*;

public class Logger {
	private static Path path = null;
	private static File file = null;

	public Logger() {
		path = Paths.get(LocalDateTime.now().toString() + ".log").toAbsolutePath();

		if (new File(path.toString()).exists()) {
			try {
				Files.delete(path);
				Files.createFile(path);
			} catch (IOException e) {
				return;
			}
		}
		else {
			try {
				Files.createFile(path);
			} catch (IOException e) {
				return;
			}
		}

		file = new File(path.toString());
	}

	public void log(String message, int severity) {
		String level = "";
		switch(severity) {
			case 1:
				level = "DEBUG";
				break;
			case 2:
				level = "INFO";
				break;
			case 3:
				level = "WARN";
				break;
			case 4:
				level = "ERROR";
				break;
			case 5:
				level = "CRIT";
				break;
			default:
				level = "DEBUG";
				break;
		}

		try {
			Files.write(path, (LocalDateTime.now().toString() + " - " + message + " - " + level + "\n").getBytes(), StandardOpenOption.APPEND);
		} catch (Exception e) {
			return;
		}
	}

	public static void readLog() { //misleading, reads most recent log
		String[] files;
		try {
			File tfolder = new File(Paths.get("").toAbsolutePath().toString());
			File[] tfiles = tfolder.listFiles();
			files = new String[tfiles.length];

			for (int i = 0;i < tfiles.length;i++) {
				files[i] = tfiles[i].toString();
			}
		} catch (Exception e) {
			return;
		}

		String[] logfiles = new String[files.length];
		for (int i = 0;i < files.length;i++) {
			String[] temp = files[i].split("\\.");
			if (temp[1].equals("log")) {
				logfiles[i] = files[i];
			}
		}

		String mostrecentlog = sortByDate(logfiles);

		File log = new File(Paths.get("" + mostrecentlog).toAbsolutePath().toString());
		if (!log.exists())
			return;
		Scanner readlog = null;
		try {
			readlog = new Scanner(log);
		} catch (Exception e) {
			return;
		}

		while (readlog.hasNextLine()) {
			System.out.println(readlog.nextLine());
		}
	}

	private static String sortByDate(String[] array) { //returns most recent log
		String finished = "";

		for (int i = 0;i < array.length;i++) {

		}

		boolean sorted = false;

		while (!sorted) {
			ArrayList<String> temp = new ArrayList<String>();

			for (int i = 0;i < array.length;i++) {
				if (array[i] != null) {
					temp.add(array[i]);
				}
			}

			array = temp.toArray(new String[temp.size()]); //down to date.log

			temp = new ArrayList<String>();
			for (int i = 0;i < array.length;i++) {
				String[] t = array[i].split("\\.");
				temp.add(t[0]);
			}

			array = temp.toArray(new String[temp.size()]);

			LocalDateTime[] temp2 = new LocalDateTime[temp.size()];

			for (int i = 0;i < temp2.length;i++) {
				temp2[i] = LocalDateTime.parse(temp.get(i));
			}

			for (int i = 0;i < temp2.length;i++) {
				for (int j = i+1;j < temp2.length;j++) {
					if (temp2[i].isBefore(temp2[j])) {
						LocalDateTime t = temp2[i];
						temp2[i] = temp2[j];
						temp2[j] = t;
						i = 0;
					}
				}
			}
			finished = temp2[0].toString() + ".log";
			sorted = true;
		}

		return finished;
	}
}
