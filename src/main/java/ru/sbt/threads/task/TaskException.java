package ru.sbt.threads.task;

public class TaskException extends RuntimeException {

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
