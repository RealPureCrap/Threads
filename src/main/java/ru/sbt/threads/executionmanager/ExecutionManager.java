package ru.sbt.threads.executionmanager;

public interface ExecutionManager {
    Context execute(Runnable callback, Runnable... tasks);
}
