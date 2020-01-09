package ceng.ceng351.labdb;

import java.util.ArrayList;

public class Bucket {
    private int localDepth;
    private ArrayList<String> entries;

    public Bucket(int localDepth) {
        this.localDepth = localDepth;
        this.entries = new ArrayList<>();
    }


    public int getLocalDepth() {
        return localDepth;
    }

    public void setLocalDepth(int localDepth) {
        this.localDepth = localDepth;
    }

    public ArrayList<String> getEntries() {
        return entries;
    }

    public void insertEntry(String entry) {
        this.entries.add(entry);
    }

    public String printEntries(){
        StringBuilder ret = new StringBuilder();
        for(String entry : entries){
            ret.append("<");
            ret.append(entry);
            ret.append(">");
        }

        return ret.toString();
    }

}
