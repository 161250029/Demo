package com.example.demo.test;

import com.example.demo.ServiceInterface.DoSlicerService;
import com.example.demo.ServiceInterface.MarkBugService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DoSlicer {
    @Autowired
    private MarkBugService markBugService;
    @Autowired
    DoSlicerService doSlicerService;

    @Test
    public void test1() {
        markBugService.MarkBugs();
    }

    @Test
    public void  test2() {
        doSlicerService.WalaSlicer_Positive();
    }


}
