package ru.sbt.threads.executionmanager;

public class ExecutionManagerImpl implements ExecutionManager {

    @Override
    public Context execute(Runnable callback, Runnable... tasks) {
        ContextThreadPool contextThreadPool = new ContextThreadPool(callback, tasks);
        for (Runnable task : tasks) {
            contextThreadPool.execute(task);
        }
        return new Context() {
            @Override
            public int getCompletedTaskCount() {
                return contextThreadPool.getCompletedTaskCounter();
            }

            @Override
            public int getFailedTaskCount() {
                return contextThreadPool.getFailedTaskCounter();
            }

            @Override
            public int getInterruptedTaskCount() {
                return contextThreadPool.getInterruptedTaskCounter();
            }

            @Override
            public void interrupt() {
                contextThreadPool.interrupt();
            }

            @Override
            public boolean isFinished() {
                return contextThreadPool.isFinished();
            }
        };
    }
}
