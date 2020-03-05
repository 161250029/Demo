package com.example.demo.Entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "FileBugLocation")
public class FileBugLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String fileName;
    @ElementCollection(targetClass = Integer.class)
    private List<Integer> locations = new ArrayList<>();

    public FileBugLocation(String fileName, ArrayList<Integer> locations) {
        this.fileName = fileName;
        this.locations = locations;
    }

    public FileBugLocation() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Integer> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Integer> locations) {
        this.locations = locations;
    }
}
