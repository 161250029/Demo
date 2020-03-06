package com.example.demo.test;

import com.example.demo.Dao.BugInfoDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BugInfoDaoTest {
    @Autowired
    private BugInfoDao bugInfoDao;

    @Test
    public void test1() {
        System.out.println(bugInfoDao.findBugsByToolName("find_sec_bug").size());
        System.out.println(bugInfoDao.findBugsByToolName("spot_bug").size());
    }

    @Test
    public void test2() {
        System.out.println(bugInfoDao.findBugsByWarning(true).size());
        System.out.println(bugInfoDao.findBugsByWarning(false).size());
    }

    @Test
    public void test3() {
        System.out.println(bugInfoDao.findBugsByJarLocation("CWE15_External_Control_of_System_or_Configuration_Setting").size());
    }
}
