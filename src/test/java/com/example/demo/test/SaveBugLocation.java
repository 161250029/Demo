package com.example.demo.test;

import com.example.demo.Dao.BugLocationDao;
import com.example.demo.Service.CollectBugLocationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SaveBugLocation {

    @Autowired
    private BugLocationDao bugLocationDao;
    @Autowired
    private CollectBugLocationService service;

//    public SaveBugLocation(CollectBugLocationService service) {
//        this.service = service;
//    }

    @Test
    public void test1() throws IOException {
        this.service.init("D:\\ChromeCoreDownloads\\Juliet_Test_Suite_v1.3_for_Java\\Java\\src\\testcases\\CWE15_External_Control_of_System_or_Configuration_Setting");
        this.service.SaveBugLocations();
   }
}
