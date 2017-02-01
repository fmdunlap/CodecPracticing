package com.fdunlap.codecs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by forre on 1/31/2017.
 */

public class HuffmanCodec {

    final static private String FILE_EXT = ".FHC";

    boolean inputIsEncoded = false;
    InputStream is;
    OutputStream os;
    File inputFile;
    File outputFile;


    public HuffmanCodec(String pathname) throws IOException{
        inputFile = new File(pathname);


        //If file not found, throw an IOException
        if(!inputFile.exists()){
            System.out.println("Input file not found.");
            throw new IOException();
        }


    }
}
