package com.fdunlap.codecs;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        String filepath = args[0];
        filepath = filepath.toLowerCase();

        if(args.length != 1){
            printErrorMessage();
	        return;
        }


        HuffmanCodec hc;

        System.out.println(args[0]);

        try {
            hc = new HuffmanCodec(args[0]);
        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
            return;
        }


        if(args[0].endsWith(".fhc")){
            try {
                hc.decode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                hc.encode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void printErrorMessage(){
        System.out.println("Incorrect usage.");
        System.out.println("Usage: ./HuffmanCodec C:/Path/To/File");
        System.out.println("Program automatically detects file type, and encodes or decodes.");
    }
}
