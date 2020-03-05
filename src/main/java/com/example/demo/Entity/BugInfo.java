package com.example.demo.Entity;

import javax.persistence.*;

@Entity
@Table(name = "BugInfo")
public class BugInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String ToolName;
    private String JarLocation;
    private String MethodName;
    private String SourceFile;
    private int level;
    private String author;
    private String date;
    private int start;
    private int end;
    private boolean isWarning;
    public BugInfo() {
    }

    public BugInfo(long id, String toolName, String jarLocation, String methodName, String sourceFile, int level, String author, String date, int start, int end, boolean isWarning) {
        this.id = id;
        ToolName = toolName;
        JarLocation = jarLocation;
        MethodName = methodName;
        SourceFile = sourceFile;
        this.level = level;
        this.author = author;
        this.date = date;
        this.start = start;
        this.end = end;
        this.isWarning = isWarning;
    }

    public String getToolName() {
        return ToolName;
    }

    public void setToolName(String toolName) {
        ToolName = toolName;
    }

    public String getSourceFile() {
        return SourceFile;
    }

    public void setSourceFile(String sourceFile) {
        SourceFile = sourceFile;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMethodName() {
        return MethodName;
    }

    public void setMethodName(String methodName) {
        MethodName = methodName;
    }

    public String getJarLocation() {
        return JarLocation;
    }

    public void setJarLocation(String jarLocation) {
        JarLocation = jarLocation;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isWarning() {
        return isWarning;
    }

    public void setWarning(boolean warning) {
        isWarning = warning;
    }
}
