package fr.uge.concurrence.ex1;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class UnboundedSafeQueue<T> {
  private final ArrayDeque<T> internQueue = new ArrayDeque<>();
  private final ReentrantLock reentrantLock = new ReentrantLock();
  private final Condition empty = reentrantLock.newCondition();

  public void add(T value) {
    Objects.requireNonNull(value);
    reentrantLock.lock();
    try {
      internQueue.add(value);
      empty.signal();
    } finally {
      reentrantLock.unlock();
    }
  }

  public T take() throws InterruptedException {
    reentrantLock.lock();
    try {
      while (internQueue.isEmpty()) {
        empty.await();
      }
      return internQueue.remove();
    } finally {
      reentrantLock.unlock();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var safeQueue = new UnboundedSafeQueue<String>();
    IntStream.range(0, 3).forEach(i ->
            Thread.ofPlatform().name("Thread " + i).start(() -> {
              for (; ; ) {
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
