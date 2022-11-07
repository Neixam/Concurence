package fr.uge.concurrence.ex4;

import java.util.stream.IntStream;

public class Counter {
  private int counter;
  private final Sync<Integer> sync = new Sync<>();
  
  public int count() throws InterruptedException {
    return sync.safe(() -> counter++);
  }

  public boolean isSomeOneCounting() {
    return sync.inSafe();
  }

  public static void main(String[] args) {
    var counter = new Counter();

    IntStream.range(0, 2).forEach(i -> Thread.ofPlatform().start(() -> {
      for (;;) {
        try {
          System.out.println("Thread " + i + ": " + counter.count());
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }));
  }
}
