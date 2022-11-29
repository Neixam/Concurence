package fr.uge.concurrence;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

public class FixedThreadPool {
    final private int poolSize;
    final private LinkedBlockingQueue<Task> tasksQueue;
    final private Thread[] threads;

    public FixedThreadPool(int poolSize) {
        if (poolSize <= 0) {
            throw new IllegalArgumentException();
        }
        this.poolSize = poolSize;
        threads = new Thread[poolSize];
        tasksQueue = new LinkedBlockingQueue<>();
    }

    public void start() {
        IntStream.range(0, poolSize).forEach(i -> threads[i] = Thread.ofPlatform().start(() -> {
            while (!Thread.interrupted()) {
                try {
                    var task = tasksQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    return;
                }
            }
        }));
    }

    public void submit(Task r) throws InterruptedException {
        tasksQueue.put(r);
    }

    public void stop() {
        Arrays.stream(threads).forEach(Thread::interrupt);
    }
}
