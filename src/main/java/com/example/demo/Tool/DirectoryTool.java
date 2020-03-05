package com.example.demo.Tool;

import java.io.File;
import java.util.ArrayList;

public class DirectoryTool {
    public static ArrayList<String> getJarFiles(String directoryPath) {
        ArrayList<String> JarPath = new ArrayList<>();
        File file = new File(directoryPath);
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                JarPath.add(f.getAbsolutePath());
            }
        }
        return JarPath;
    }


    /**
     * 目录下的所有xml文件
     * @param directoryPath
     * @return
     */
    public static ArrayList<String> getXmlFiles(String directoryPath) {
        ArrayList<String> resultFiles = new ArrayList<>();
        File file = new File(directoryPath);
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isFile())
                resultFiles.add(f.getAbsolutePath());
        }
        return resultFiles;
    }

    public static void main(String[] args) {
        ArrayList<String> path = getJarFiles("D:\\ChromeCoreDownloads\\Juliet_Test_Suite_v1.3_for_Java\\Java\\src\\testcases");
        System.out.println(path.size());
        for (String p : path)
            System.out.println(p);

        ArrayList<String> resultPath = getXmlFiles("D:\\findsecbugResult");
        System.out.println(resultPath.size());
        for (String p : resultPath)
            System.out.println(p);
    }
}
