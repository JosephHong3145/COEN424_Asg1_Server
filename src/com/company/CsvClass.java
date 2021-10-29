package com.company;

import java.io.Serializable;

public class CsvClass implements Serializable {
    private String benchMarkType; //DVD or NDB
    private String dataType; //Training or Testing
    static final long serialVersionUID = 1;

    public int getCPUUtilization() {
        return CPUUtilization;
    }

    public float getNetworkIn() {
        return NetworkIn;
    }

    public float getNetworkOut() {
        return NetworkOut;
    }

    public double getMemoryUnit() {
        return MemoryUnit;
    }

    public double getFinalTarget() {
        return FinalTarget;
    }

    private int CPUUtilization;
    private float NetworkIn;
    private float NetworkOut;
    private double MemoryUnit;
    private double FinalTarget;

    public CsvClass(){
        this.benchMarkType = " ";
        this.dataType = " ";
        this.CPUUtilization = 0;
        this.NetworkIn = 0.0f;
        this.NetworkOut = 0.0f;
        this.MemoryUnit = 0.0;
        this.FinalTarget = 0.0;
    }

    public CsvClass(String benchMarkType, String dataType, int CPUUtilization, float NetworkIn, float NetworkOut, double MemoryUnit, double FinalTarget){
        this.benchMarkType = benchMarkType;
        this.dataType = dataType;
        this.CPUUtilization = CPUUtilization;
        this.NetworkIn = NetworkIn;
        this.NetworkOut = NetworkOut;
        this.MemoryUnit = MemoryUnit;
        this.FinalTarget = FinalTarget;
    }

    public void print(){
        System.out.println("benchMarkType: " + benchMarkType);
        System.out.println("dataType: " + dataType);
        System.out.println("CPUUtilization: " + CPUUtilization);
        System.out.println("NetworkIn: " + NetworkIn);
        System.out.println("NetworkOut: " + NetworkOut);
        System.out.println("MemoryUnit: " + MemoryUnit);
        System.out.println("FinalTarget: " + FinalTarget);
    }

    public String getBenchMarkType(){return this.benchMarkType;}
    public String getDataType(){return this.dataType;}
}
