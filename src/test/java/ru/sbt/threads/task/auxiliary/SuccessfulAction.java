package ru.sbt.threads.task.auxiliary;

import java.util.concurrent.Callable;

public class SuccessfulAction implements Callable<String> {

    @Override
    public String call() throws Exception {
        System.out.println("Gonna sleep for 2 seconds ...");
        Thread.sleep(2000);
        return "result";
    }
}
