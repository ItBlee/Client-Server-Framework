package com.itblee.core.client.util;

import java.io.*;
import java.util.*;

public class FileUtil {

    public static String readLineThenDelete(String path) {
        LinkedList<String> lines = new LinkedList<>();
        try {
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            Scanner scanner = new Scanner(fileInputStream);
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            scanner.close();
            fileInputStream.close();
            String result = lines.poll();
            write(path, lines);
            return result;
        } catch (IOException ignore) {
            return null;
        }
    }

    public static void write(String path, Collection<String> lines) {
        write(path, lines, false);
    }

    public static void write(String path, Collection<String> lines, boolean append) {
        try {
            File file = new File(path);
            file.createNewFile();
            PrintStream write = new PrintStream(new FileOutputStream(file, append));
            for (String line : lines) {
                write.println(line);
            }
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
