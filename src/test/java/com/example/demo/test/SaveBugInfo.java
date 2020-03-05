package com.example.demo.test;

import com.example.demo.ServiceInterface.SaveBugInfosService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SaveBugInfo {
    @Autowired
    private SaveBugInfosService saveBugInfosService;
    @Test
    public void test1() {
        this.saveBugInfosService.save_find_sec_bugs();
        this.saveBugInfosService.save_spot_bugs();
    }
}
