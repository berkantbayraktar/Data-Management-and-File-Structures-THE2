package ceng.ceng351.labdb;

import java.util.ArrayList;
import java.util.List;

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

    public void removeEntry(String entry){
        for(int i = 0 ; i < this.entries.size() ; i++){
            if(entry.equals(this.entries.get(i))){
                this.entries.remove(i);
                break;
            }
        }
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
