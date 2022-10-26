package fr.uge.concurrence.ex02;

import java.util.ArrayDeque;
import java.util.List;
import java.util.stream.IntStream;

public class ThreadInfo<V> {
  private final int max;
  private final ArrayDeque<V> internQueue;

  public ThreadInfo(int max) {
    if (max <= 0) {
      throw new IllegalArgumentException("max must >= 0");
    }
    this.max = max;
    internQueue = new ArrayDeque<>();
  }

  public void add(V value) {
    synchronized (internQueue) {
      internQueue.add(value);
      if (internQueue.size() == max) {
        internQueue.notifyAll();
      }
    }
  }

  public List<V> getValues() throws InterruptedException {
    synchronized (internQueue) {
      while (internQueue.size() != max) {
        internQueue.wait();
      }
      return IntStream.range(0, max).mapToObj(i -> internQueue.remove()).toList();
    }
  }
}
