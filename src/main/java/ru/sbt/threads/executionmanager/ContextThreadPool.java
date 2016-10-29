package ru.sbt.threads.executionmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ContextThreadPool extends ThreadPoolExecutor {
    private final Runnable lastTask;
    private final List<Runnable> notStartedTasks;
    private final Object lock = new Object();
    private int activeTaskCounter = 0;
    private int failedTaskCounter = 0;
    private int completedTaskCounter = 0;
    private int interruptedTaskCounter = 0;

    public ContextThreadPool(Runnable lastTask, Runnable... tasks) {
        super(5, 10, 5, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(tasks.length, false));
        notStartedTasks = new ArrayList<>(Arrays.asList(tasks));
        this.lastTask = lastTask;
    }

    public int getFailedTaskCounter() {
        synchronized (lock) {
            return failedTaskCounter;
        }
    }

    public int getCompletedTaskCounter() {
        synchronized (lock) {
            return completedTaskCounter;
        }
    }

    public int getInterruptedTaskCounter() {
        synchronized (lock) {
            return interruptedTaskCounter;
        }
    }

    public void interrupt() {
        synchronized (lock) {
            interruptedTaskCounter = notStartedTasks.size();
            notStartedTasks.forEach(this::remove);
            submitLastTask();
        }
    }

    public boolean isFinished() {
        synchronized (lock) {
            return activeTaskCounter == 0;
        }
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        if (r != lastTask) {
            synchronized (lock) {
                notStartedTasks.remove(r);
                activeTaskCounter++;
            }
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (r != lastTask) {
            synchronized (lock) {
                activeTaskCounter--;
                if (t != null) {
                    failedTaskCounter++;
                } else {
                    completedTaskCounter++;
                }
                submitLastTask();
            }
        }
    }

    private void submitLastTask() {
        if (activeTaskCounter == 0) {
            execute(lastTask);
        }
    }

}
