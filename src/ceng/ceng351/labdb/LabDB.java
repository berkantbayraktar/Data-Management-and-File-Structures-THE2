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
        //System.out.println("depth : " + depth);
        String key = getNumeric(studentID);
        String binaryKey = convertToBinary(key);
        //System.out.println("BinaryKey : " + binaryKey);
        String buckeyKey = binaryKey.substring(binaryKey.length()-depth,binaryKey.length());
        //System.out.println("Sub : " + buckeyKey);

        return buckeyKey;

    }

    public void enter(String studentID) {
        String key = hash(studentID,globalDepth);
        Bucket value = buckets.get(key);

        if(value.getEntries().size() == this.bucketSize){
            if(globalDepth == value.getLocalDepth()){
                ArrayList<String> entries = value.getEntries();

                buckets.put("0" + key  , new Bucket(1 + value.getLocalDepth()));
                buckets.put("1" + key  , new Bucket(1 + value.getLocalDepth()));
                globalDepth++;
                buckets.remove(key);

                for(String entry : entries){
                    key = hash(entry,globalDepth);
                    buckets.get(key).insertEntry(entry);
                }

                key = hash(studentID,globalDepth);
                buckets.get(key).insertEntry(studentID);

                Set<String> kS = this.buckets.keySet();

                ArrayList<String> keySet = new ArrayList<>();
                for(String currentkey : kS){
                    keySet.add(currentkey);
                }

                for(String currentkey : keySet){
                    if(this.buckets.get(currentkey).getLocalDepth()< globalDepth){
                        Bucket bucket = this.buckets.remove(currentkey);
                        this.buckets.put("1" + currentkey, bucket);
                        this.buckets.put("0" + currentkey, bucket);
                    }
                }
            }

            else{
                ArrayList<String> entries = new ArrayList<>();

                for(int i = 0 ; i < value.getEntries().size() ; i++){
                    entries.add(value.getEntries().get(i));
                }


                if(key.startsWith("1")){
                    buckets.remove(key);
                    buckets.remove("0" + key.substring(1));

                    buckets.put( key  , new Bucket(globalDepth));
                    buckets.put("0" + key.substring(1), new Bucket(globalDepth));
                }
                else if(key.startsWith("0")){
                    buckets.remove(key);
                    buckets.remove("1" + key.substring(1));

                    buckets.put( key  , new Bucket(globalDepth));
                    buckets.put("1" + key.substring(1), new Bucket(globalDepth));
                }

                for(String entry : entries){
                    key = hash(entry,globalDepth);
                    buckets.get(key).insertEntry(entry);
                }

                key = hash(studentID,globalDepth);
                buckets.get(key).insertEntry(studentID);
            }
        }
        else{
            buckets.get(key).insertEntry(studentID);
        }
    }

    public void leave(String studentID) {
        
    }

    public String search(String studentID) {
        String key = hash(studentID,globalDepth);
        Set<String> keys = buckets.keySet();
        System.out.println(keys);
        Bucket bucket = this.buckets.get(key);
        ArrayList<String> entries = bucket.getEntries();

        for(String entry : entries){
            if (studentID.equals(entry)) {
                return key;
            }
        }

        return "-1";
    }

    public void printLab() {
        System.out.println("Global depth : " + this.globalDepth);
        TreeMap<String,Bucket> map = new TreeMap<>();
        /*
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
        }*/

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
