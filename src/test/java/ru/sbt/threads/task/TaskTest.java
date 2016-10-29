package ru.sbt.threads.task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.sbt.threads.task.auxiliary.FailingAction;
import ru.sbt.threads.task.auxiliary.SuccessfulAction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertTrue;

public class TaskTest {

    private ExecutorService executor;

    @Before
    public void setUp() throws Exception {
        executor = Executors.newFixedThreadPool(5);
    }

    @After
    public void tearDown() throws Exception {
        executor.shutdown();
    }

    @Test
    public void shouldResultInTheSameValueForTheSameSuccessfulAction() throws Exception {
        Task<String> task = new Task<>(new SuccessfulAction());
        List<Callable<String>> tasks = createTasksBasedOn(task);
        List<Future<String>> futures = executor.invokeAll(tasks);

        List<String> values = resolve(futures);

        assertTrue("Some tasks results are not the same", allValuesMatch(values));
    }

    @Test(expected=ExecutionException.class)
    public void shouldThrowTaskExceptionForFailingAction() throws Exception {
        Task<String> task = new Task<>(new FailingAction());
        Future<String> future = executor.submit(task::get);

        future.get();
    }

    private List<Callable<String>> createTasksBasedOn(Task<String> task) {
        List<Callable<String>> result = new ArrayList<>();

        result.add(task::get);
        result.add(task::get);
        result.add(task::get);
        result.add(task::get);
        result.add(task::get);

        return result;
    }

    private <T> List<T> resolve(List<Future<T>> futures) throws Exception {
        List<T> result = new ArrayList<>();

        for (Future<T> future : futures) {
            result.add(future.get());
        }

        return result;
    }

    private <T> boolean allValuesMatch(List<T> list) throws Exception {
        T firstValue = list.get(0);
        return list.stream().allMatch(elem -> elem.equals(firstValue));
    }
}