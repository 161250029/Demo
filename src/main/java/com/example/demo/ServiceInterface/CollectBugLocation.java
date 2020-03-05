package com.example.demo.ServiceInterface;


import java.io.IOException;

public interface CollectBugLocation {
    public void init(String DircetoryPath) throws IOException;
    public void SaveBugLocations();
}
