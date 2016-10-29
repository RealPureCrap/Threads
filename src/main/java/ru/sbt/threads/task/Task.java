package ru.sbt.threads.task;

import java.util.concurrent.Callable;

public class Task<T> {
    private final Callable<? extends T> callable;
    private volatile T result = null;
    private volatile TaskException exception = null;

    public Task(Callable<? extends T> callable) {
        this.callable = callable;
    }

    public T get() {
        T beforeSynchronized = checkResult();
        if (beforeSynchronized != null) return beforeSynchronized;
        synchronized (this) {
            T afterSynchronized = checkResult();
            if (afterSynchronized != null) return afterSynchronized;
            return getResult();
        }
    }

    private T getResult() {
        try {
            result = callable.call();
            return result;
        } catch (Exception e) {
            exception = new TaskException("Exception in method call().", e);
            throw exception;
        }
    }

    private T checkResult() {
        if (result != null) {
            return result;
        } else if (exception != null) {
            throw exception;
        }
        return null;
    }
}
