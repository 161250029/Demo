package com.example.demo.Service;

import com.example.demo.Dao.BugLocationDao;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.Entity.FileBugLocation;
import com.example.demo.ServiceInterface.CollectBugLocation;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

@Service
public class CollectBugLocationService implements CollectBugLocation {
    @Autowired
    private BugLocationDao bugLocationDao;

    private ArrayList<String> FilePath = new ArrayList<>();
    private ArrayList<String> Lines = new ArrayList<>();
    private ArrayList<FileBugLocation> bugLocations = new ArrayList<>();


    public void init(String DircetoryPath) throws IOException {
        getAllFiles(DircetoryPath);
//        read(FilePath.get(0));
////        findLocations(FilePath.get(0));
////        System.out.println(bugLocations.get(0).getLocations().size());
        for (String path : FilePath) {
            read(path);
            findLocations(path);
        }
    }
    public void getAllFiles(String DirectoryPath) {
        File diretory = new File(DirectoryPath);
        File[] files =  diretory.listFiles();
        for (File file : files) {
            if (file.isFile() && file.getAbsolutePath().split("\\.")[2].equals("java"))
                 FilePath.add(file.getAbsolutePath());
        }
    }

    public void read(String path) throws IOException {
        Lines = new ArrayList<>();
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        while((line = br.readLine())!= null) {
            Lines.add(line);
        }
        br.close();
    }

    public void findLocations(String FilePath) {
        FileBugLocation bugLocation = new FileBugLocation();
        for (int  i = 0 ; i < Lines.size() ; i ++) {
            String[] word = Lines.get(i).trim().split(" ");
//            if (word[0].equals("/*"))
//                System.out.println(10);
            if (word[0].equals("/*") && word.length > 1 && word[1].equals("POTENTIAL")) {
                while (true) {
                    String[] senctence = Lines.get(++ i).trim().split(" ");
                    System.out.println(senctence[0]);
                    if (!senctence[0].equals("*")) {
                        bugLocation.getLocations().add(i + 1);
                        System.out.println(i + 1);
                        break;
                    }
                }
            }
        }
        bugLocation.setFileName(FilePath.split("\\\\")[FilePath.split("\\\\").length - 1].split("\\.")[0]);
        bugLocations.add(bugLocation);
    }
    public void SaveBugLocations() {
        for (FileBugLocation fileBugLocation : bugLocations)
            bugLocationDao.saveAndFlush(fileBugLocation);
    }

    public static void main(String[] args) throws IOException {
//        CollectBugLocationService service = new CollectBugLocationService(bugLocationDao);
//        service.init("D:\\ChromeCoreDownloads\\Juliet_Test_Suite_v1.3_for_Java\\Java\\src\\testcases\\CWE15_External_Control_of_System_or_Configuration_Setting");
//        System.out.println(service.bugLocations.size());
    }
}
