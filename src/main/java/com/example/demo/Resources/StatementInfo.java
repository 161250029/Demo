package com.example.demo.Resources;

import com.ibm.wala.ipa.slicer.Statement;

public class StatementInfo {
        private Statement seedStatement;
        private SlicerType slicerType;

    public StatementInfo() {
    }

    public StatementInfo(Statement seedStatement, SlicerType slicerType) {
        this.seedStatement = seedStatement;
        this.slicerType = slicerType;
    }

    public Statement getSeedStatement() {
        return seedStatement;
    }

    public void setSeedStatement(Statement seedStatement) {
        this.seedStatement = seedStatement;
    }

    public SlicerType getSlicerType() {
        return slicerType;
    }

    public void setSlicerType(SlicerType slicerType) {
        this.slicerType = slicerType;
    }
}
