package ru.sbt.threads.task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.sbt.threads.task.auxiliary.FailingAction;
import ru.sbt.threads.task.auxiliary.SuccessfulAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class TaskPerformanceTest {

    private ExecutorService executor;
    private Task<String> templateTask;
    private List<Callable<String>> tasks;

    @Parameterized.Parameters
    public static Collection<Task<String>> data() {
        return Arrays.asList(
                new Task<>(new SuccessfulAction()),
                new Task<>(new FailingAction())
        );
    }

    public TaskPerformanceTest(Task<String> templateTask) {
        this.templateTask = templateTask;
    }

    @Before
    public void setUp() throws Exception {
        executor = Executors.newFixedThreadPool(5);

        this.tasks = new ArrayList<>();
        tasks.add(templateTask::get);
        tasks.add(templateTask::get);
        tasks.add(templateTask::get);
        tasks.add(templateTask::get);
        tasks.add(templateTask::get);
    }

    @After
    public void tearDown() throws Exception {
        executor.shutdown();
    }

    @Test
    public void shouldExecuteFast() throws Exception {

        long startTime = System.currentTimeMillis();
        executor.invokeAll(tasks);
        long endTime = System.currentTimeMillis();

        assertTrue(endTime - startTime < 2100);
    }
}