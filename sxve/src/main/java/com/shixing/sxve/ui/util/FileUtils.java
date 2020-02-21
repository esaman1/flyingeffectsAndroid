package com.shixing.sxve.ui.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {
    public static String readJsonFromFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);

        String ret = convertStreamToString(fis);

        fis.close();

        return ret;
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line.trim());
        }
        reader.close();
        return sb.toString();
    }
}
