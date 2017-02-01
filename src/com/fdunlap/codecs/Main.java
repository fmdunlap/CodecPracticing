package com.fdunlap.codecs;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        if(args.length != 2){
	        System.out.println("Incorrect usage.");
	        System.out.println("Usage: ./HuffmanCodec C:/Path/To/File E(ncode)/D(ecode)");
	        return;
        }

        if(args[1].toLowerCase() != "e" && args[1].toLowerCase() != "d"){
            System.out.println("Incorrect usage.");
            System.out.println("Usage: ./HuffmanCodec C:/Path/To/File E(ncode)/D(ecode)");
            return;
        }
        HuffmanCodec hc;
        try {
            hc = new HuffmanCodec(args[0]);
        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
            return;
        }

        if(args[1].toLowerCase() == "e"){
            hc.encode();
        } else {
            hc.decode();
        }
    }
}
