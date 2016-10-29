package ru.sbt.threads.task;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TaskTest {
    private Task<String> stringTask = new Task<>(() -> {
        Thread.sleep(2000);
        return "result";
    });

    private Task<String> stringTaskWithException = new Task<>(() -> {
        Thread.sleep(2000);
        throw new IllegalArgumentException();
    });

    @Test
    public void getUsualResult() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<String>> results = new ArrayList<>();
        List<Callable<String>> tasks = new ArrayList<>();
        tasks.add(stringTask::get);
        tasks.add(stringTask::get);
        tasks.add(stringTask::get);
        tasks.add(stringTask::get);
        tasks.add(stringTask::get);

        long startTime = System.currentTimeMillis();
        for (Callable<String> task : tasks) {
            results.add(executor.submit(task));
        }
        executor.invokeAll(tasks);
        long endTime = System.currentTimeMillis();
        executor.shutdown();

        assertTrue(endTime - startTime < 2100);

        String result = results.get(0).get();
        assertEquals(result, results.get(1).get());
        assertEquals(result, results.get(2).get());
        assertEquals(result, results.get(3).get());
        assertEquals(result, results.get(4).get());
    }

    @Test
    public void getResultWithException() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<String>> results = new ArrayList<>();
        List<Callable<String>> tasks = new ArrayList<>();
        tasks.add(stringTaskWithException::get);
        tasks.add(stringTaskWithException::get);
        tasks.add(stringTaskWithException::get);
        tasks.add(stringTaskWithException::get);
        tasks.add(stringTaskWithException::get);

        long startTime = System.currentTimeMillis();
        for (Callable<String> task : tasks) {
            results.add(executor.submit(task));
        }
        executor.invokeAll(tasks);
        long endTime = System.currentTimeMillis();
        executor.shutdown();

        assertTrue(endTime - startTime < 2100);

        Exception exception = null;
        try{
            String result = results.get(0).get();
        }
        catch (ExecutionException e){
            exception = (TaskException) e.getCause();
        }

        try{
            String result = results.get(1).get();
        }
        catch (ExecutionException e){
            assertEquals(exception, e.getCause());
        }
    }

}