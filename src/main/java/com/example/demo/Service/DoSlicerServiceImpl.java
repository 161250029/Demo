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
                Set<Integer> source_line_numbers = WalaSlicerTool.doSlicing(JarPath , bugInfo.getMethodName() , bugInfo.getSourceFile() , bugInfo.getStart());
                String source_file_path = JarLocationInfo.Jar_Directory_prefix + "\\CWE15_External_Control_of_System_or_Configuration_Setting\\" + bugInfo.getSourceFile() + ".java";
                FileTool fileTool = new FileTool(source_file_path);
                DirectoryTool.CreateResultDir("CWE15_External_Control_of_System_or_Configuration_Setting");
                fileTool.writeWalaStatements("CWE15_External_Control_of_System_or_Configuration_Setting\\" + bugInfo.getSourceFile() + ".txt", bugInfo.getMethodName() , source_line_numbers , bugInfo.getStart() ,bugInfo.isWarning());
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
    public void TextSlicer_Positive() {

    }

    @Override
    public void TextSlicer_False_Postive() {

    }
}
