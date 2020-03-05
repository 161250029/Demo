package com.example.demo.Service;

import com.example.demo.Dao.BugInfoDao;
import com.example.demo.Entity.BugInfo;
import com.example.demo.ServiceInterface.SaveBugInfosService;
import com.example.demo.Tool.DirectoryTool;
import com.example.demo.Tool.DomTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaveBugInfosServiceImpl implements SaveBugInfosService {
    @Autowired
    private BugInfoDao bugInfoDao;
    @Override
    public void save_find_sec_bugs() {
        ArrayList<String> resultPaths = DirectoryTool.getXmlFiles("D:\\findsecbugResult");
        resultPaths.forEach(resultPath -> {
            try {
                List<BugInfo> bugInfos = DomTool.getBugs(resultPath);
                bugInfos.forEach(bugInfo -> bugInfo.setToolName("find_sec_bug"));
                bugInfoDao.saveAll(bugInfos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void save_spot_bugs() {
        ArrayList<String> resultPaths = DirectoryTool.getXmlFiles("D:\\SpotsBugResult");
        resultPaths.forEach(resultPath -> {
            try {
                List<BugInfo> bugInfos = DomTool.getBugs(resultPath);
                bugInfos.forEach(bugInfo -> bugInfo.setToolName("spot_bug"));
                bugInfoDao.saveAll(bugInfos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
