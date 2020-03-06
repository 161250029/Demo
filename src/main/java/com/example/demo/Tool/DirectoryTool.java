package com.example.demo.Tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DirectoryTool {
    /**
     * 目录下所有Jar包
     * @param directoryPath
     * @return
     */
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

    public static void CreateResultDir(String DirName) {
        File file = new File(DirName);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void main(String[] args) throws IOException {
//        ArrayList<String> path = getJarFiles("D:\\ChromeCoreDownloads\\Juliet_Test_Suite_v1.3_for_Java\\Java\\src\\testcases");
//        System.out.println(path.size());
//        for (String p : path)
//            System.out.println(p);

        ArrayList<String> resultPath = getXmlFiles("D:\\findsecbugResult");
        File file = new File("JarNames");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (String p : resultPath) {
            writer.write("\"" + p.split("\\\\")[p.split("\\\\").length - 1].split("\\.")[0] + "\"");
            writer.write(",");
            writer.newLine();
        }
        writer.close();

    }
}
