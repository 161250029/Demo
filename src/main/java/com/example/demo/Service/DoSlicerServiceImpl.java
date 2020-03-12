package com.example.demo.Service;

import com.example.demo.Dao.BugInfoDao;
import com.example.demo.Entity.BugInfo;
import com.example.demo.Resources.JarLocationInfo;
import com.example.demo.ServiceInterface.DoSlicerService;
import com.example.demo.Tool.DirectoryTool;
import com.example.demo.Tool.FileTool;
import com.example.demo.Tool.WalaSlicerTool;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class DoSlicerServiceImpl implements DoSlicerService {
    @Autowired
    private BugInfoDao bugInfoDao;
    @Override
    public void WalaSlicer_Positive() {
        List<BugInfo> bugInfos = bugInfoDao.findBugsByJarLocation("CWE15_External_Control_of_System_or_Configuration_Setting");
        String JarPath = JarLocationInfo.Jar_Directory_prefix + "\\CWE15_External_Control_of_System_or_Configuration_Setting" + ".jar";
        for (BugInfo bugInfo : bugInfos) {
            try {
//                System.out.println(bugInfo.getSourceFile() + " " + bugInfo.getStart() + " "+ bugInfo.getMethodName());
                Set<Integer> source_line_numbers = new TreeSet<>();
                if (!bugInfo.getSourceFile().equals("CWE15_External_Control_of_System_or_Configuration_Setting__File_66b") && !bugInfo.getSourceFile().equals("CWE15_External_Control_of_System_or_Configuration_Setting__File_72b")) {
                    source_line_numbers = WalaSlicerTool.doSlicing(JarPath , bugInfo.getMethodName() , bugInfo.getSourceFile() , bugInfo.getStart());
                }
                else
                    source_line_numbers.add(bugInfo.getStart());
                String source_file_path = JarLocationInfo.Jar_Directory_prefix + "\\CWE15_External_Control_of_System_or_Configuration_Setting\\" + bugInfo.getSourceFile() + ".java";
                FileTool fileTool = new FileTool(source_file_path);
                DirectoryTool.CreateResultDir("CWE15_External_Control_of_System_or_Configuration_Setting");
                fileTool.writeWalaStatements("CWE15_External_Control_of_System_or_Configuration_Setting\\" + bugInfo.getSourceFile() + ".txt", source_line_numbers , bugInfo);
                Runtime.getRuntime().gc();
            } catch (WalaException e) {
                e.printStackTrace();
            } catch (CancelException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidClassFileException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void WalaSlicer_False_Positive() {

    }

    @Override
    public void TextSlicer_Positive()  {
        for (String jarName : JarLocationInfo.JARNames) {
            List<BugInfo> bugInfos = bugInfoDao.findBugsByJarLocation(jarName);
            for (BugInfo bugInfo : bugInfos) {
                String file_path_prefix = GenerateFilePathPrefix(bugInfo.getJarLocation());
                String file_path = JarLocationInfo.Jar_Directory_prefix + "\\" + file_path_prefix + "\\" + bugInfo.getSourceFile() + ".java";
                String out_path = "D:\\FileSlice\\" +file_path_prefix + "\\" + bugInfo.getSourceFile() + "\\" + bugInfo.getStart() + "\\" + bugInfo.getSourceFile() + ".java";
                DirectoryTool.CreateResultDir("D:\\FileSlice\\" + file_path_prefix + "\\" + bugInfo.getSourceFile() + "\\" + bugInfo.getStart());
                try {
                    FileTool fileTool = new FileTool(file_path);
                    fileTool.run(out_path , bugInfo.getStart());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


//        List<BugInfo> bugInfos = bugInfoDao.findBugsByJarLocation("CWE15_External_Control_of_System_or_Configuration_Setting");
//        for (BugInfo bugInfo : bugInfos) {
//            String file_path_prefix = GenerateFilePathPrefix(bugInfo.getJarLocation());
//            String file_path = JarLocationInfo.Jar_Directory_prefix + "\\" + file_path_prefix + "\\" + bugInfo.getSourceFile() + ".java";
//            String out_path = "FileSlice\\" +file_path_prefix + "\\" + bugInfo.getSourceFile() + "\\" + bugInfo.getStart() + "\\" + bugInfo.getSourceFile() + ".java";
//            DirectoryTool.CreateResultDir("FileSlice\\" + file_path_prefix + "\\" + bugInfo.getSourceFile() + "\\" + bugInfo.getStart());
//            try {
//                FileTool fileTool = new FileTool(file_path);
//                fileTool.run(out_path , bugInfo.getStart());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    }

    @Override
    public void TextSlicer_False_Postive() {
        for (String jarName : JarLocationInfo.JARNames) {
            List<BugInfo> bugInfos = bugInfoDao.findBugsByJarLocation(jarName);
            for (BugInfo bugInfo : bugInfos) {
                if (bugInfo.getSourceFile().equals(JarLocationInfo.BanSourceFiles[0]) || bugInfo.getSourceFile().equals(JarLocationInfo.BanSourceFiles[1]))
                    continue;
                String file_path_prefix = GenerateFilePathPrefix(bugInfo.getJarLocation());
                String file_path = JarLocationInfo.Jar_Directory_prefix + "\\" + file_path_prefix + "\\" + bugInfo.getSourceFile() + ".java";
                String out_method_path = "D:\\BugSlice\\" +file_path_prefix + "\\" + bugInfo.getSourceFile() + "\\" + bugInfo.getStart() + "\\" + bugInfo.getSourceFile() + ".java";
                String out_bug_info_path = "D:\\BugSlice\\" +file_path_prefix + "\\" + bugInfo.getSourceFile() + "\\" + bugInfo.getStart() + "\\" + bugInfo.getSourceFile() + ".txt";
                DirectoryTool.CreateResultDir("D:\\BugSlice\\" + file_path_prefix + "\\" + bugInfo.getSourceFile() + "\\" + bugInfo.getStart());
                try {
                    FileTool fileTool = new FileTool(file_path);
                    fileTool.GenerateMethodAndBugInfo(out_method_path , out_bug_info_path , bugInfo , bugInfo.getStart());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public String GenerateFilePathPrefix(String JarName) {
        int length = JarName.length();
        if (JarName.substring(length - 3 , length - 1).equals("s0")) {
            return  JarName.substring(0 , length - 4) + "\\" + JarName.substring(length - 3);
        }
        return JarName;
    }
}
