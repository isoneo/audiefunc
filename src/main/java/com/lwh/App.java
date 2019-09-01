package com.lwh;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author william.lee
 */
public class App {
    private static final Logger logger = LogManager.getLogger();
    static {
        SystemOutToLog4j.enableForPackage("com.lwh");
    }
    public static void main( String[] args ) throws Exception {
        // Start Logger
        logger.info("Starting PDF conversion Process");

        // Grab all Parameters
        String sav_folder = args[0];
        System.out.println( "Processing PDFs found in " +sav_folder);

        List<String> raw_pdf_file_list = supporting_tools.grab_all_matching_files(sav_folder,"pdf");
        System.out.println("Files matching search\n"+raw_pdf_file_list);

        parallelized_functions.parallelized_pdfsmart_copy(raw_pdf_file_list, sav_folder);

        logger.info("All Processes finished");
    }
}
