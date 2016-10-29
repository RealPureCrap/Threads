package ru.sbt.threads.executionmanager;

interface ExecutionManager {

    Context execute(Runnable callback, Runnable... tasks);
}
