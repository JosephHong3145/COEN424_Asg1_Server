package com.company;
import java.io.Serializable;
import java.util.Vector;
import com.google.gson.Gson;

public class CsvClass_Batch implements Serializable {
    private int batchID;
    private Vector<CsvClass> csvBatch;
    private String workloadMetric;
    static final long serialVersionUID = 1;

    CsvClass_Batch(){
        this.batchID = 0;
        this.csvBatch = new Vector<CsvClass>();
        this.workloadMetric = " ";
    }

    public void setBatchID(int batchID){
        this.batchID = batchID;
    }

    public void addCsvClass(CsvClass c){
        csvBatch.add(c);
    }

    public void setWorkloadMetric(String workloadMetric){
        this.workloadMetric = workloadMetric;
    }

    public int getBatchID(){return this.batchID;}

    public int getBatchCount(){
        return this.csvBatch.size();
    }

    public String getWorkloadMetric(){
        return workloadMetric;
    }

    public void print(){
        if(workloadMetric.equals("cpuutil")){
            for(int i = 0; i < csvBatch.size(); i++){
                System.out.println("[BATCH " + batchID + "]" + csvBatch.get(i).getCPUUtilization());
            }
        }

        if(workloadMetric.equals("networkin")){
            for(int i = 0; i < csvBatch.size(); i++){
                System.out.println("[BATCH " + batchID + "]" + csvBatch.get(i).getNetworkIn());
            }
        }

        if(workloadMetric.equals("networkout")){
            for(int i = 0; i < csvBatch.size(); i++){
                System.out.println("[BATCH " + batchID + "]" + csvBatch.get(i).getNetworkOut());
            }
        }

        if(workloadMetric.equals("memoryunit")){
            for(int i = 0; i < csvBatch.size(); i++){
                System.out.println("[BATCH " + batchID + "]" + csvBatch.get(i).getMemoryUnit());
            }
        }

        if(workloadMetric.equals("finaltarget")){
            for(int i = 0; i < csvBatch.size(); i++){
                System.out.println("[BATCH " + batchID + "]" + csvBatch.get(i).getFinalTarget());
            }
        }
    }
}
