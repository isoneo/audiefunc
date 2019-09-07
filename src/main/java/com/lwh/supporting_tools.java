package com.lwh; /**
 * Created by william.lee on 8/21/2019.
 */


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;


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

    public static void smartcopypdf_lettersize(String src, String dest) throws IOException, DocumentException {

        File orig_file = new File(src);
        String orig_name = orig_file.getName();
        String cv_name = "cv_" + orig_name;
        System.out.println("Starting PDF Compression for " + orig_name);

        // Create reader to read in PDF file
        PdfReader reader = new PdfReader(src);
        reader.removeUnusedObjects();
        reader.removeFields();
        reader.removeAnnotations();

        Document document = new Document(PageSize.LETTER);
        PdfSmartCopy pdfSmartCopy = new PdfSmartCopy(document, new FileOutputStream(dest + '\\' + cv_name));
        pdfSmartCopy.setPageSize(PageSize.LETTER);

        int total = reader.getNumberOfPages() + 1;
        for (int i = 1; i < total; i++) {
            PdfImportedPage pdfImportedPage = pdfSmartCopy.getImportedPage(reader, i);
            document.open();
            pdfSmartCopy.addPage(pdfImportedPage);
            document.close();
        }


        reader.close();

        pdfSmartCopy.setFullCompression();

        pdfSmartCopy.close();

    }



    public static void smartcopyPDF(String src, String dest) throws IOException, DocumentException {

        File orig_file = new File(src);
        String orig_name = orig_file.getName();
        String cv_name = "cv_" + orig_name;
        System.out.println("Starting PDF Compression for " + orig_name);


        Document document = new Document(PageSize.LETTER);
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

    public static PdfArray scaleDown(PdfArray original, float scale) {
        if (original == null)
            return null;
//        float width = original.getAsNumber(2).floatValue()
//                - original.getAsNumber(0).floatValue();
//        float height = original.getAsNumber(3).floatValue()
//                - original.getAsNumber(1).floatValue();
//        return new PdfRectangle(width * scale, height * scale);
        return new PdfRectangle(612,792);
    }


    public static class ScaleEvent extends PdfPageEventHelper {

        protected float scale = 1;
        protected PdfDictionary pageDict;

        public ScaleEvent(float scale) {
            this.scale = scale;
        }

        public void setPageDict(PdfDictionary pageDict) {
            this.pageDict = pageDict;
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            writer.addPageDictEntry(PdfName.ROTATE, pageDict.getAsNumber(PdfName.ROTATE));
            writer.addPageDictEntry(PdfName.MEDIABOX, scaleDown(pageDict.getAsArray(PdfName.MEDIABOX), scale));
            writer.addPageDictEntry(PdfName.CROPBOX, scaleDown(pageDict.getAsArray(PdfName.CROPBOX), scale));
        }
    }
    public static float generate_auto_scale(String orig_file) throws Exception, IOException {
        PdfReader reader = new PdfReader(orig_file);

        PdfDictionary first_page = reader.getPageN(1);
        Rectangle pagesize = reader.getPageSize(first_page);
        float orig_height = pagesize.getHeight();
        float orig_width = pagesize.getWidth();
        String page_orientation = "";
        float orig_length = 0;

        // Protrait
        if ( orig_height > orig_width ) {
            orig_length = orig_height;
            page_orientation = "Portrait";
        } else {
            // Landscape
            orig_length = orig_width;
            page_orientation = "Landscape";
        }

        float calc_scale = 792 / orig_length;
        System.out.println("Scale for PDF "+ orig_file);
        System.out.println("   Original PDF orientation is"+ page_orientation);

        System.out.println("   Orig pdf length "+ orig_length);
        System.out.println("   Calculated Scaling "+ calc_scale);
        return calc_scale;
    }
    public static Rectangle get_page_orientation(PdfImportedPage read_page){
        float orig_height = read_page.getHeight();
        float orig_width = read_page.getWidth();

        Rectangle output = null;
        // Portrait
        if ( orig_height > orig_width ) {
            output = PageSize.LETTER;
        } else {
            // Landscape
            output = PageSize.LETTER.rotate();
        }
        return output;
    }
    public static void manipulatePdf(String src, String dest) throws Exception {

        File orig_file = new File(src);
        String orig_name = orig_file.getName();
        String cv_name = "cv_" + orig_name;
        String new_sav_location = dest +"\\"+"converted_pdfs";
        File dest_directory = new File(new_sav_location);
        if (! dest_directory.exists()){
            dest_directory.mkdirs();
        }

        PdfReader reader = new PdfReader(src);
        float scale = supporting_tools.generate_auto_scale(src);
        ScaleEvent event = new ScaleEvent(scale);
        event.setPageDict(reader.getPageN(1));

        int n = reader.getNumberOfPages();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new_sav_location + '\\' + orig_name));
        writer.setPageEvent(event);

        Image page;
        for (int p = 1; p <= n; p++) {
            PdfImportedPage read_in_page = writer.getImportedPage(reader, p);
            // get orientation of original page
            Rectangle orig_page_orientation_box = get_page_orientation(read_in_page);

            // If first page => Open document set orientation
            if ( p ==1 ){
                document.open();
                document.setPageSize(orig_page_orientation_box);
            } else {
                // If not first page set page size and then add new page
                document.setPageSize(orig_page_orientation_box);
                document.newPage();
            }

            page = Image.getInstance(read_in_page);
            page.setAbsolutePosition(0, 0);
            page.scalePercent(scale * 100);
            document.add(page);
            if (p < n) {
                event.setPageDict(reader.getPageN(p + 1));
            }

        }
        document.close();
    }

}
