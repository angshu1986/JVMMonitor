package jvmmonitor.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jvmmonitor.constants.FileConstants;

public class FileUtil implements FileConstants {

	public static void writeToFile(String pid, String s) throws IOException {
		String outputDesktopDirectory = System.getProperty(USER_HOME_DIRECTORY).concat(File.separator).concat(DESKTOP)
				.concat(File.separator).concat(OUTPUT_DIRECTORY_NAME);
		System.out.println(outputDesktopDirectory);
		File dir = new File(outputDesktopDirectory);
		if (!dir.exists()) {
			dir.mkdir();
		}
		if (dir.exists() && !dir.isDirectory()) {
			throw new IllegalStateException(
					"A file exists by the directory name. Please change the name of the file or directory manually");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String fileName = sdf.format(new Date(System.currentTimeMillis())).concat(SEPERATOR).concat(pid)
				.concat(FILE_EXTENSION);
		File outputFile = new File(dir, fileName);
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}
		FileWriter wr = new FileWriter(outputFile);
		wr.write(s);
		wr.close();
	}
}
