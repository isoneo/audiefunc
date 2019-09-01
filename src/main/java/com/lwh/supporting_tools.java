package com.lwh; /**
 * Created by william.lee on 8/21/2019.
 */


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class supporting_tools {

    public static void delete_out_files(String csv_file_full_path) throws IOException {
        File file_to_delete = new File(csv_file_full_path);

        if(file_to_delete.delete()){
            System.out.println(csv_file_full_path+" Successfully deleted!");
        } else {
            System.out.println("Failed to delete the "+csv_file_full_path);
        }
    }

    public static void delete_out_files_bulk(List<String> csv_file_chunk) throws IOException {
        for(String csv_file_full_path : csv_file_chunk){
            File file_to_delete = new File(csv_file_full_path);

            if(file_to_delete.delete()){
                System.out.println(csv_file_full_path+" Successfully deleted!");
            } else {
                if(file_to_delete.exists()){
                    for (int i=0; i < 5; i++){
                        System.gc();
                        if (file_to_delete.delete()){
                            break;
                        }
                    }
                    if(file_to_delete.exists()) {
                        System.out.println("Failed to delete the "+csv_file_full_path);
                    }
                } else {
                    System.out.println("Failed to delete the "+csv_file_full_path);
                }


            }
        }
    }

    public static List<String> grab_all_matching_files(String folder_loc, String file_ext){
        File bfp = new File(folder_loc +"\\");
        File[] raw_list_of_files = bfp.listFiles((dir, name) -> name.toLowerCase().endsWith("."+file_ext));
        ArrayList<String> matching_file_list = new ArrayList<String>();

        for (File file : raw_list_of_files) {
            if (file.isFile()){
//                System.out.println("Found matching file");
                matching_file_list.add(file.getAbsoluteFile().getPath());
            }
        }
        return matching_file_list;
    }
    public static void compressPdf(String src, String dest) throws IOException, DocumentException {

        File orig_file = new File(src);
        String orig_name = orig_file.getName();
        String cv_name = "cv_"+orig_name;
        System.out.println("Starting PDF Compression for "+orig_name);

        PdfReader reader = new PdfReader(src);
        reader.removeUnusedObjects();
        reader.removeFields();
        reader.removeAnnotations();
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest +'\\'+ cv_name), PdfWriter.VERSION_1_5);
        stamper.getWriter().setCompressionLevel(9);

        int total = reader.getNumberOfPages() + 1;
        for (int i = 1; i < total; i++) {
            reader.setPageContent(i, reader.getPageContent(i));
        }
        try {

            stamper.setFullCompression();
            stamper.close();
            reader.close();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public static void smartcopyPDF(String src, String dest) throws IOException, DocumentException {

        File orig_file = new File(src);
        String orig_name = orig_file.getName();
        String cv_name = "cv_" + orig_name;
        System.out.println("Starting PDF Compression for " + orig_name);


        Document document = new Document();
        PdfSmartCopy pdfSmartCopy = new PdfSmartCopy(document, new FileOutputStream(dest + '\\' + cv_name));

        document.open();

        PdfReader reader = new PdfReader(src);
        reader.removeUnusedObjects();
        reader.removeFields();
        reader.removeAnnotations();


        pdfSmartCopy.addDocument(reader);
        reader.close();
        document.close();
        pdfSmartCopy.setFullCompression();
        pdfSmartCopy.close();
    }






}
