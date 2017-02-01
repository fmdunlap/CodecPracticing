package com.fdunlap.codecs;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by forre on 1/31/2017.
 */

public class HuffmanCodec {

    final static private String FILE_EXT = ".fhc";

    private String originalExt;

    final static private int STX = 2;
    final static private int EOT = 4;
    final static private int ETB = 23;

    private FileInputStream is;
    private FileOutputStream os;
    private BitOutputStream bOS;
    private BitInputStream bIS;

    private int inputFileSize = 0;
    private int outputFileSize = 0;

    private File inputFile;
    private File outputFile;

    private String inputPath;
    private String outputPath;

    LinkedHashMap<Integer, String> HuffmanByteToCodeMap;
    LinkedHashMap<String, Integer> HuffmanCodeToByteMap;

    private int[] byteArray;


    public HuffmanCodec(String pathname) throws IOException{
        inputPath = pathname;
        inputFile = new File(pathname);
        outputFile = determineOutputFile();


        //If file not found, throw an IOException
        if(!inputFile.exists()){
            System.out.println("Input file not found.");
            throw new IOException();
        }


    }

    private File determineOutputFile() {
        String basePath = inputPath.substring(0, inputPath.lastIndexOf('.'));
        originalExt = inputPath.substring(inputPath.lastIndexOf('.') + 1);

        System.out.println("BASEPATH" + basePath+ " INPUTPATH: " + inputPath + " oEXT: " + originalExt);

        if(originalExt != "fhc")
        outputPath = basePath + FILE_EXT;

        File outFile = new File(outputPath);

        int count = 1;

        while(outFile.exists()){
            outputPath = basePath + Integer.toString(count) + FILE_EXT;
            outFile = new File(outputPath);
            count++;
        }

        return outFile;
    }


    public void encode() throws Exception{

        HashMap<Integer, Integer> histogram = new HashMap<>();

        try {
            if (inputFile != null)
                is = new FileInputStream(inputFile);

            if(outputFile != null) {
                bOS = new BitOutputStream(outputPath);
            }

            //For analytics purposes.
            inputFileSize = Math.toIntExact(inputFile.length());

            //used to reconstruct the outputfile (originalByte->huffmanByte, nextOriginalByte->huffmanCode)
            byteArray = new int[inputFileSize];

            int currentByte;
            int i = 0;

            /*
            Counts & records the number of occurrences of each byte.
             */
            while((currentByte = is.read()) != -1){
                byteArray[i] = currentByte;
                i++;

                if(histogram.containsKey(currentByte)){
                    int keyVal = histogram.get(currentByte);
                    keyVal++;
                    histogram.put(currentByte, keyVal);
                } else {
                    histogram.put(currentByte, 1);
                }
            }

            //orders the histogram into a map sorted by value
            Map<Integer,Integer> sortedMap = sortByValue(histogram);

            Iterator vIt = sortedMap.values().iterator();
            Iterator kIt = sortedMap.keySet().iterator();

            int[] values = new int[sortedMap.size()];
            int[] keys = new int[sortedMap.size()];

            i = 0;
            while(vIt.hasNext() && kIt.hasNext()){
                Integer v = (Integer) vIt.next();
                Integer k = (Integer) kIt.next();
                values[i] = v;
                keys[i] = k;
                i++;
            }


            createHuffmanMap(values, keys);

            //TESTING PURPOSES: shows the byte-code pairs
//            Iterator it = HuffmanByteToCodeMap.entrySet().iterator();
//            while(it.hasNext()){
//                Map.Entry pair = (Map.Entry) it.next();
//                System.out.println("" + pair.getKey() + ": " + pair.getValue());
//            }

            /*
            Current thought is this: 'SOH' BYTE 'ETX' code 'ETB' BYTE 'ETX' code 'ETB' ... code 'ETB' 'EOT'
            decoding will be harder than creating the header, tbh. Like...
             */
            writeHeader();

            //writes encoded PAYLOAD version bitwise to a file. (kinda cool, imho)
            String byteOut = "";
            for(int j = 0; j < byteArray.length; j++){
                String code = HuffmanByteToCodeMap.get(byteArray[j]);
//                byteOut += code;
                bOS.write(code.length(), Integer.parseInt(code, 2));
            }

            //TESTING PURPOSES - writes a string to the console of the bits of the file.
            //Then demos how decoding will take place on the payload.
//            System.out.println(byteOut);

//            String original = "";
//            while(byteOut != "" && byteOut.length() > 0){
//                String currKey = "";
//                while(!HuffmanCodeToByteMap.containsKey(currKey) && byteOut.length() > 0){
//                    currKey += byteOut.charAt(currKey.length());
//                }
//                if(byteOut.length() > 0) {
//                    byteOut = byteOut.substring(currKey.length());
//                    System.out.print(Integer.toBinaryString(HuffmanCodeToByteMap.get(currKey)));
//                }
//            }

        }finally{
            if(is != null) is.close();
            if(bOS != null) bOS.close();
        }
    }

    private void writeHeader() {
        Iterator headerIterator = HuffmanByteToCodeMap.entrySet().iterator();

        for(int i = 0; i < originalExt.length(); i++){
            try {
                bOS.write((int)originalExt.charAt(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            bOS.write(STX);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(headerIterator.hasNext()){
            Map.Entry pair = (Map.Entry) headerIterator.next();
            int b = (int) pair.getKey();
            String c = (String) pair.getValue();

            bOS.write(8, b);
            bOS.write(c.length(), Integer.parseInt(c, 2));
            bOS.write(8, ETB);
        }
        bOS.write(8, EOT);
    }

    public void createHuffmanMap(int frequencies[], int keys[]){
        HuffmanByteToCodeMap = new LinkedHashMap<>();
        HuffmanCodeToByteMap = new LinkedHashMap<>();
        Node root = Node.makeHuffmanTree(frequencies, keys);
        recurseAndCreateEncodingScheme(root);
    }

    public void recurseAndCreateEncodingScheme(Node n) {
        // print with colon if leaf node
        if (n.val != -1) {
            String b = "";
            Node other = n;
            while(other != null){
                b = "" + other.bin + b;
                other = other.parent;
            }
            b = b.substring(1);
            HuffmanByteToCodeMap.put(n.val, b);
            HuffmanCodeToByteMap.put(b, n.val);
        }

        // Start recursive on left child then right
        if (n.left != null) {
            recurseAndCreateEncodingScheme(n.left);
        }
        if (n.right != null) {
            recurseAndCreateEncodingScheme(n.right);
        }

    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public void decode() throws Exception{

        try{
            bIS = new BitInputStream(inputPath);

            constructMapsFromHeader();


            //TODO cleanup the outputPath handling.
            outputPath = inputPath.substring(0, inputPath.lastIndexOf('.')) + originalExt;
            os = new FileOutputStream(outputPath);

            //            while(byteOut != "" && byteOut.length() > 0){
//                String currKey = "";
//                while(!HuffmanCodeToByteMap.containsKey(currKey) && byteOut.length() > 0){
//                    currKey += byteOut.charAt(currKey.length());
//                }
//                if(byteOut.length() > 0) {
//                    byteOut = byteOut.substring(currKey.length());
//                    System.out.print(Integer.toBinaryString(HuffmanCodeToByteMap.get(currKey)));
//                }
//            }
            int c;
            while((c = bIS.readBits(1)) != -1){
                String currKey = "" + c;
                while(!HuffmanCodeToByteMap.containsKey(currKey) && (c = bIS.readBits(1)) != -1){
                    currKey += c;
                }
                if(currKey != "")
                os.write(HuffmanCodeToByteMap.get(currKey));
            }
        } finally {
            if(os != null) os.close();
            if(bIS != null) bIS.close();
        }


    }
        //HEADER FORMAT: EXT_BYTE|EXT_BYTE|EXT_BYTE|STX|BYTE|code|ETB|BYTE|code|ETB|BYTE|code|ETB|EOT|PAYLOAD...
    private void constructMapsFromHeader() throws IOException {
        boolean complete = false;
        HuffmanByteToCodeMap = new LinkedHashMap<>();
        HuffmanCodeToByteMap = new LinkedHashMap<>();

        int currByte;
        originalExt = ".";
        while((currByte = bIS.read()) != STX){
            originalExt += (char)currByte;
        }


        while(!complete){
            currByte = bIS.read();
            if(currByte == EOT) break;
            String bString = "";
            for(int i = 0; i < 9; i++){
                bString += bIS.readBits(1);
            }
            while(Integer.parseInt(bString.substring(bString.length() - 8),2) != ETB){
                bString += bIS.readBits(1);
            }
            String code = bString.substring(0, bString.length() - 8);
            HuffmanCodeToByteMap.put(code, currByte);
            HuffmanByteToCodeMap.put(currByte, code);
        }

    }


    public int getOutputFileSize() {
        return outputFileSize;
    }

    public int getInputFileSize() {
        return inputFileSize;
    }
}