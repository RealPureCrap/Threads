package ru.sbt.threads.executionmanager;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ExecutionManagerImplTest {
    private final List<String> resultFromUsualTasks = new ArrayList<>();
    private String resultFromFinalTask = null;

    private final Runnable usualTask = () -> {
        try {
            Thread.sleep(1000);
            resultFromUsualTasks.add("usualTask");
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread was interrupted.", e);
        }
    };

    private final Runnable usualTaskWithException = () -> {
        throw new IllegalArgumentException();
    };

    private final Runnable finalTask = () -> resultFromFinalTask = "finalTask";

    @Test
    public void executeUsualAndUsualWithExceptionTasks() throws InterruptedException {
        Runnable[] tasks = new Runnable[10];
        Arrays.fill(tasks, 0, 5, usualTask);
        Arrays.fill(tasks, 5, 10, usualTaskWithException);

        ExecutionManager manager = new ExecutionManagerImpl();
        Context result = manager.execute(finalTask, tasks);

        Thread.sleep(1300);

        assertEquals(5, result.getCompletedTaskCount());
        assertEquals(5, result.getFailedTaskCount());
        assertEquals(0, result.getInterruptedTaskCount());
        assertEquals(5, resultFromUsualTasks.size());
        assertEquals(true, result.isFinished());
        assertEquals("finalTask", resultFromFinalTask);
    }

    @Test
    public void testInterrupt() throws InterruptedException {
        Runnable[] tasks = new Runnable[15];
        Arrays.fill(tasks, 0, 15, usualTask);

        ExecutionManager manager = new ExecutionManagerImpl();
        Context result = manager.execute(finalTask, tasks);

        Thread.sleep(800);
        assertEquals(false, result.isFinished());
        result.interrupt();
        Thread.sleep(230);

        assertEquals(5, result.getCompletedTaskCount());
        assertEquals(0, result.getFailedTaskCount());
        assertEquals(10, result.getInterruptedTaskCount());
        assertEquals(5, resultFromUsualTasks.size());
        assertEquals(true, result.isFinished());
        assertEquals("finalTask", resultFromFinalTask);
    }

}