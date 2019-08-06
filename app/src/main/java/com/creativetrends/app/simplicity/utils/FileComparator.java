package com.creativetrends.app.simplicity.utils;

import java.io.File;
import java.util.Comparator;


public class FileComparator implements Comparator<File> {
    @Override
    public int compare(File f1, File f2) {
        if(f1 == f2) {
            return 0;
        }
        if(f1.isDirectory() && f2.isFile()) {
            return -1;
        }
        if(f1.isFile() && f2.isDirectory()) {
            return 1;
        }
        return f1.getName().compareToIgnoreCase(f2.getName());
    }
}
