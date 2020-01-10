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

        if(value.getEntries().size() >= this.bucketSize){
            if(globalDepth == value.getLocalDepth()){
                ArrayList<String> entries = value.getEntries();
                boolean alreadyInserted = false;
                /// check already inserted ?


                for(String entry : entries){
                    if(entry.equals(studentID)){
                        alreadyInserted = true;
                    }
                }

                buckets.put("0" + key  , new Bucket(1 + value.getLocalDepth()));
                buckets.put("1" + key  , new Bucket(1 + value.getLocalDepth()));
                globalDepth++;
                buckets.remove(key);

                for(String entry : entries){
                    key = hash(entry,globalDepth);
                    buckets.get(key).insertEntry(entry);
                }

                if(!alreadyInserted){
                    key = hash(studentID,globalDepth);
                    buckets.get(key).insertEntry(studentID);
                }


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

                key = hash(studentID,globalDepth);
                if(buckets.get(key).getEntries().size() > this.bucketSize){
                    enter(studentID);
                }
            }


            else{
                ArrayList<String> entries = new ArrayList<>();


                for(int i = 0 ; i < value.getEntries().size() ; i++){
                    entries.add(value.getEntries().get(i));
                }

                boolean alreadyInserted = false;
                /// check already inserted ?


                for(String entry : entries){
                    if(entry.equals(studentID)){
                        alreadyInserted = true;
                    }
                }

                if(!alreadyInserted){
                    entries.add(studentID);
                }



                int ldepth = buckets.get(key).getLocalDepth();
                //System.out.println("local depth : " + ldepth);
                //System.out.println("key : " +  key);

                for(int i = 0 ; i < Math.pow(2,globalDepth -  ldepth) ; i++){
                    String bin = toBinary(i,globalDepth-ldepth);
                    //System.out.println("ahaha : " + bin );
                    buckets.remove(bin + key.substring(bin.length()));
                    buckets.put(bin + key.substring(bin.length()), new Bucket(ldepth + 1));
                }

                ldepth++;

                //System.out.println("after local depth : " + ldepth);


                if(globalDepth == ldepth){
                    for(String entry : entries){
                        key = hash(entry,globalDepth);
                        buckets.get(key).insertEntry(entry);
                    }
                }
                else {
                    for(String entry : entries){
                        for(int i = 0 ; i < Math.pow(2,globalDepth -  ldepth) ; i++){
                            String bin = toBinary(i,globalDepth-ldepth);
                            key = hash(entry,ldepth);
                            key = bin + key;
                            buckets.get(key).insertEntry(entry);
                        }
                    }

                }
                if(buckets.get(key).getEntries().size() > this.bucketSize){
                    enter(studentID);
                }


            }
        }
        else{
            ArrayList<String> entries = value.getEntries();
            for(String entry : entries){
                if(entry.equals(studentID)){
                    return;
                }
            }

            buckets.get(key).insertEntry(studentID);
        }
    }

    public void leave(String studentID) {
        String key = hash(studentID,globalDepth);
        Bucket value = buckets.get(key);
        int ldepth = value.getLocalDepth();

        if(ldepth < globalDepth){
            this.buckets.get(key).getEntries().remove(studentID);
        }
        else if (ldepth == globalDepth){
            buckets.get(key).getEntries().remove(studentID);


            if(value.getEntries().size() == 0){
                if(key.startsWith("1") && buckets.get("0" + key.substring(1)).getLocalDepth() == ldepth){
                    value = buckets.get("0" + key.substring(1));
                    value.setLocalDepth(value.getLocalDepth()-1);
                    buckets.remove(key);
                    buckets.put("1" + key.substring(1), value);

                }
                else if(key.startsWith("0") && buckets.get("1" + key.substring(1)).getLocalDepth() == ldepth){
                    value = buckets.get("1" + key.substring(1));
                    value.setLocalDepth(value.getLocalDepth()-1);
                    buckets.remove(key);
                    buckets.put("0" + key.substring(1), value);

                }
            }
        }

        Set<String> keyS = buckets.keySet();
        boolean shouldDecrease = true;

        for(String currentKey : keyS){
            if (buckets.get(currentKey).getLocalDepth() == globalDepth){
                shouldDecrease = false;
                break;
            }
        }

        if(shouldDecrease){
           globalDepth--;

            ArrayList<String> keySet = new ArrayList<>();
            HashMap<String,Bucket> tempbuckets = new HashMap<>();

            for(String currentkey : keyS){
                keySet.add(currentkey);
            }

            for(String currentkey: keySet){
                value = buckets.get(currentkey);
                if(!tempbuckets.containsKey(currentkey.substring(1))){
                    tempbuckets.put(currentkey.substring(1),value);
                }
            }

            this.buckets = tempbuckets;

        }

        /// CHECK STILL CAN WE MERGE ????

        //merge();

        ArrayList<String> keySet = new ArrayList<>();
        keyS = buckets.keySet();
        keySet.clear();
        for(String currentkey : keyS){
            keySet.add(currentkey);
        }

        int count = 0 ;
        for(String currentKey : keySet){
            count = count + buckets.get(currentKey).getEntries().size();
        }
        if(count == 0){
            this.globalDepth = 1;
            this.buckets = new HashMap<> ();

            buckets.put("0", new Bucket(1));
            buckets.put("1", new Bucket(1));
        }

    }

    public String search(String studentID) {
        String key = hash(studentID,globalDepth);
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
        map.putAll(this.buckets);
        map.forEach((key,value) -> System.out.println(key + " : " + "[Local depth:" + value.getLocalDepth() + "]" + value.printEntries()));
    }

    public String convertToBinary(String key){
        int keyInt = Integer.parseInt(key);
        String ret = Integer.toBinaryString(keyInt);
        for(int i = ret.length() ; i < 32 ; i++){
            ret = "0" + ret;
        }
        return ret;
    }

    public String toBinary(int x , int len){
        if (len > 0) {
            return String.format("%" + len + "s",
                    Integer.toBinaryString(x)).replaceAll(" ", "0");
        }

        return null;

    }

    public String getNumeric(String studentID){
        return studentID.replaceAll("\\D+","");
    }

    public void merge(){
        Set<String> keyS = buckets.keySet();
        ArrayList<String> keySet = new ArrayList<>();
        HashMap<String,Bucket> tempbuckets = new HashMap<>();
        Bucket value;

        for(String currentkey : keyS){
            keySet.add(currentkey);
        }

        for(String currentkey: keySet){
            value = buckets.get(currentkey);
            if(!tempbuckets.containsKey(currentkey.substring(1))){
                tempbuckets.put(currentkey.substring(1),value);
            }
        }

        this.buckets = tempbuckets;

    }

}
