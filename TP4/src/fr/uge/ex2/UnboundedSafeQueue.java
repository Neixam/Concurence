package fr.uge.ex2;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.IntStream;

public class UnboundedSafeQueue<V> {
  private final Queue<V> internQueue = new ArrayDeque<>();

  public void add(V value) {
    Objects.requireNonNull(value);
    synchronized (internQueue) {
      internQueue.add(value);
      internQueue.notify();
    }
  }

  public V take() throws InterruptedException {
    synchronized (internQueue) {
      while (internQueue.isEmpty()) {
        internQueue.wait();
      }
      return internQueue.poll();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var safeQueue = new UnboundedSafeQueue<String>();
    IntStream.range(0, 3).forEach(i ->
            Thread.ofPlatform().name("Thread " + i).start(() -> {
              for (;;) {
                safeQueue.add(Thread.currentThread().getName());
                try {
                  Thread.sleep(2000);
                } catch (InterruptedException e) {
                  throw new RuntimeException(e);
                }
              }
            }));
    while (true) {
      System.out.println(safeQueue.take());
    }
  }
}
