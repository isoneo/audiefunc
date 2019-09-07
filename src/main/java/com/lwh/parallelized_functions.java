package com.lwh;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import static com.google.common.collect.Lists.partition;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 *
 * @author william.lee
 */
public class parallelized_functions {

    // Number of available Processors
    static final int iCPU = Runtime.getRuntime().availableProcessors();

    public interface LoopBody<T> {

        void run(T i);
    }

    public static <T> void ForEach(Iterable<T> parameters,
            final LoopBody<T> loopBody) {
        ExecutorService executor = Executors.newFixedThreadPool(iCPU);
        List<Future<?>> futures = new LinkedList<Future<?>>();

        for (final T param : parameters) {
            Future<?> future = executor.submit(new Runnable() {
                public void run() {
                    loopBody.run(param);
                }
            });

            futures.add(future);
        }

        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }

        executor.shutdown();
    }

    public static void For(int start,
            int stop,
            final LoopBody<Integer> loopBody) {
        ExecutorService executor = Executors.newFixedThreadPool(iCPU);
        List<Future<?>> futures = new LinkedList<Future<?>>();

        for (int i = start; i < stop; i++) {
            final Integer k = i;
            Future<?> future = executor.submit(new Runnable() {
                public void run() {
                    loopBody.run(k);
                }
            });
            futures.add(future);
        }

        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }

        executor.shutdown();
    }

    public static List<String> parallelized_pdfsmart_copy(List<String> list_of_files_to_process, String output_saveloc)
            throws InterruptedException, ExecutionException, IOException, Exception {

        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(threads);

        List<List<String>> chunk_list = partition(list_of_files_to_process, 20);

        List<Future<String>> futures = new ArrayList<Future<String>>();
        for (final List<String> indv_input_list : chunk_list) {
            Callable<String> callable = new Callable<String>() {
                public String call() throws Exception {
                    String output = new String();
                    for (String i :indv_input_list) {
                        supporting_tools.smartcopyPDF(i, output_saveloc);
                    }
                    Thread.sleep(1500);

                    return output;
                }
            };
            futures.add(service.submit(callable));
        }

        service.shutdown();

        List<String> outputs = new ArrayList<String>();
        for (Future<String> future : futures) {
            //outputs.add(future.get());
            try {
                outputs.add(future.get());
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }
        return outputs;
    }

    public static List<String> parallelized_pdfsmart_copy_lettersize(List<String> list_of_files_to_process, String output_saveloc)
            throws InterruptedException, ExecutionException, IOException, Exception {

        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(threads);

        List<List<String>> chunk_list = partition(list_of_files_to_process, 20);

        List<Future<String>> futures = new ArrayList<Future<String>>();
        for (final List<String> indv_input_list : chunk_list) {
            Callable<String> callable = new Callable<String>() {
                public String call() throws Exception {
                    String output = new String();
                    for (String i :indv_input_list) {
                        supporting_tools.manipulatePdf(i, output_saveloc);
                    }
                    Thread.sleep(1500);

                    return output;
                }
            };
            futures.add(service.submit(callable));
        }

        service.shutdown();

        List<String> outputs = new ArrayList<String>();
        for (Future<String> future : futures) {
            //outputs.add(future.get());
            try {
                outputs.add(future.get());
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }
        return outputs;
    }
}
