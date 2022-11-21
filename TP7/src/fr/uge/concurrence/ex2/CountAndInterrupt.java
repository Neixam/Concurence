package fr.uge.concurrence.ex2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class CountAndInterrupt {
  private final int[] counters;
  private final ReentrantLock lock = new ReentrantLock();

  public CountAndInterrupt(int size) {
    if (size <= 0) {
      throw new IllegalArgumentException("size must > 0");
    }
    counters = new int[size];
  }

  public void increment(int index) throws InterruptedException {
    Objects.checkIndex(index, counters.length);
    lock.lockInterruptibly();
    try {
      counters[index]++;
    } finally {
      lock.unlock();
    }
  }

  public int get(int index) throws InterruptedException {
    Objects.checkIndex(index, counters.length);
    lock.lockInterruptibly();
    try {
      return counters[index];
    } finally {
      lock.unlock();
    }
  }

  public static void main(String[] args) {
    var nbThread = 4;
    var counter = new CountAndInterrupt(nbThread);
    var threads = new ArrayList<Thread>();
    IntStream.range(0, nbThread).forEach(i -> threads.add(Thread.ofPlatform().start(() -> {
      while (!Thread.interrupted()) {
        try {
          System.out.println("Thread " + i + ": counter = " + counter.get(i));
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    })));

    for (;;) {
      System.out.println("enter a thread id:");
      try (var input = new InputStreamReader(System.in);
           var reader = new BufferedReader(input)) {
        String line;
        while ((line = reader.readLine()) != null) {
          var threadId = Integer.parseInt(line);
          counter.increment(threadId);
        }
      } catch (IOException e) {
        System.exit(0);
      } catch (InterruptedException e) {
        System.exit(1);
      } finally {
        threads.forEach(Thread::interrupt);
      }
    }
  }
}
