package ceng.ceng351.labdb;


import java.util.*;

public class LabDB {

    private int bucketSize;
    private int globalDepth;
    private HashMap<String,Bucket> buckets;


    public LabDB(int bucketSize) {
        this.bucketSize = bucketSize;
        this.globalDepth = 1;
        this.buckets = new HashMap<> ();

        buckets.put("0", new Bucket(1));
        buckets.put("1", new Bucket(1));

    }



    public String hash(String studentID, int depth){
        System.out.println("depth : " + depth);
        String key = getNumeric(studentID);
        String binaryKey = convertToBinary(key);
        System.out.println("BinaryKey : " + binaryKey);
        String buckeyKey = binaryKey.substring(binaryKey.length()-depth,binaryKey.length());
        System.out.println("Sub : " + buckeyKey);

        return buckeyKey;

    }

    public void enter(String studentID) {
        String key = hash(studentID,globalDepth);
        Bucket value = buckets.get(key);

        if(value == null){
            key = hash(studentID,globalDepth-1);
            value = buckets.get(key);
            System.out.println("hereeeeeeeeeeeeee");
        }



        if(value.getEntries().size() == this.bucketSize){
            ArrayList<String> entries = value.getEntries();

            buckets.put("0" + key  , new Bucket(1 + value.getLocalDepth()));
            buckets.put("1" + key  , new Bucket(1 + value.getLocalDepth()));

            if(globalDepth == value.getLocalDepth())
                globalDepth++;

            buckets.remove(key);

            for(String entry : entries){
                key = hash(entry,globalDepth);
                buckets.get(key).insertEntry(entry);
            }

            key = hash(studentID,globalDepth);
            buckets.get(key).insertEntry(studentID);

        }
        else{
            buckets.get(key).insertEntry(studentID);
        }


    }

    public void leave(String studentID) {
        
    }

    public String search(String studentID) {
        return "";
    }

    public void printLab() {
        System.out.println("Global depth : " + this.globalDepth);
        TreeMap<String,Bucket> map = new TreeMap<>();
        Set<String> kS = this.buckets.keySet();

        ArrayList<String> keySet = new ArrayList<>();
        for(String key : kS){
            keySet.add(key);
        }

        for(String key : keySet){
            if(this.buckets.get(key).getLocalDepth()< globalDepth){
                Bucket bucket = this.buckets.remove(key);
                this.buckets.put("1" + key, bucket);
                this.buckets.put("0" + key, bucket);
            }
        }

        map.putAll(this.buckets);
        map.forEach((key,value) -> System.out.println(key + " : " + "[Local depth:" + value.getLocalDepth() + "]" + value.printEntries()));
    }

    public String convertToBinary(String key){
        int keyInt = Integer.parseInt(key);
        String ret = Integer.toBinaryString(keyInt);
        for(int i = ret.length() ; i < 7 ; i++){
            ret = "0" + ret;
        }
        return ret;
    }

    public String getNumeric(String studentID){
        return studentID.replaceAll("\\D+","");
    }
}
