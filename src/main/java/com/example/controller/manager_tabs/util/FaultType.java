package com.example.controller.manager_tabs.util;

public class FaultType {
    private String repairType;
    private int count;

    public FaultType(String repairType, int count) {
        this.repairType = repairType;
        this.count = count;
    }

    public String getRepairType() {
        return repairType;
    }

    public int getCount() {
        return count;
    }
}
