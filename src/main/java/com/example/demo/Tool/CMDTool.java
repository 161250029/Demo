package com.example.demo.Tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CMDTool {
    public static void cmdC(String cmd) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("cmd.exe /c " +cmd);
        InputStream input = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String szline;
        while ((szline = reader.readLine())!= null) {
            System.out.println(szline);
        }
        reader.close();
        process.waitFor();
        process.destroy();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ArrayList<String> JarPaths = DirectoryTool.getJarFiles("D:\\ChromeCoreDownloads\\Juliet_Test_Suite_v1.3_for_Java\\Java\\src\\testcases");
//        cmdC("D: && cd D:\\ChromeCoreDownloads\\findsecbugs-cli-1.10.1 && findsecbugs.bat -progress -medium -xml -output " + "D:\\findsecbugResult\\" + "try.xml " + "D:\\ChromeCoreDownloads\\Juliet_Test_Suite_v1.3_for_Java\\Java\\src\\testcases\\CWE15_External_Control_of_System_or_Configuration_Setting.jar");
        for (String jarpath : JarPaths)
            cmdC("D: && cd D:\\ChromeCoreDownloads\\findsecbugs-cli-1.10.1 && findsecbugs.bat -progress -high -xml -output " + "D:\\findsecbugResult\\" +jarpath.split("\\\\")[jarpath.split("\\\\").length - 1].split("\\.")[0] + ".xml " + jarpath);
    }
}
