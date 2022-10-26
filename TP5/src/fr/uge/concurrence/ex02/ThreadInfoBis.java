package fr.uge.concurrence.ex02;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

public class ThreadInfoBis<V> {

  private final int max;
  private final HashSet<String> rooms;
  private final ArrayDeque<V> internQueue;

  public ThreadInfoBis(int max) {
      if (max <= 0) {
        throw new IllegalArgumentException("max must >= 0");
      }
      this.max = max;
      rooms = new HashSet<>();
      internQueue = new ArrayDeque<>();
  }

  public void add(V value, String room) throws InterruptedException {
    synchronized (internQueue) {
      internQueue.add(value);
      rooms.add(room);
      if (rooms.size() == max) {
        internQueue.notifyAll();
      }
      while (rooms.contains(room)) {
        internQueue.wait();
      }
    }
  }

  public List<V> getValues() throws InterruptedException {
    synchronized (internQueue) {
      while (rooms.size() != max) {
        internQueue.wait();
      }
      rooms.clear();
      internQueue.notifyAll();
      return IntStream.range(0, max).mapToObj(i -> internQueue.remove()).toList();
    }
  }
}
