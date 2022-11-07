package fr.uge.concurrence.ex3;

import java.nio.channels.AsynchronousCloseException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * Le problème avec le code proposé c'est qu'il est possible qu'au moment entre l'appel au waitingForPermits et le close
 * il peut y avoir un thread qui appelle acquire, on peut corriger ce problème en faisant une méthode qui ferme le
 * semaphore lorsqu'il n'y a plus de thread en attente.
 */
public class SemaphoreClosable {
  private int permits;
  private int waiters;
  private boolean isClosed;
  private final ReentrantLock lock;

  private final Condition empty;

  public SemaphoreClosable(int permits) {
    if (permits <= 0) {
      throw new IllegalArgumentException("Permits must > 0");
    }
    this.permits = permits;
    lock = new ReentrantLock();
    empty = lock.newCondition();
  }

  public void release() {
    lock.lock();
    try {
      if (isClosed) {
        return;
      }
      permits++;
      empty.signal();
    } finally {
      lock.unlock();
    }
  }

  public boolean tryAcquire() {
    lock.lock();
    try {
      if (permits == 0) {
        return false;
      }
      permits--;
      return true;
    } finally {
      lock.unlock();
    }
  }

  public void acquire() throws InterruptedException, AsynchronousCloseException {
    lock.lock();
    try {
      if (isClosed) {
        throw new IllegalStateException("Semaphore are closed");
      }
      waiters++;
      while (permits == 0) {
        empty.await();
        if (isClosed) {
          throw new AsynchronousCloseException();
        }
      }
      waiters--;
      permits--;
    } finally {
      lock.unlock();
    }
  }

  public int waitingForPermits() {
    lock.lock();
    try {
      return waiters;
    } finally {
      lock.unlock();
    }
  }

  public void close() {
    lock.lock();
    try {
      isClosed = true;
    } finally {
      lock.unlock();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var semaphore = new SemaphoreClosable(5);
    IntStream.range(0, 10).forEach(i -> Thread.ofPlatform().start(() -> {
      try {
        semaphore.acquire();
        System.out.println("Thread " + i + " acquire permit");
        Thread.sleep(1000);
        semaphore.release();
        System.out.println("Thread " + i + " release permit");
      } catch (InterruptedException | AsynchronousCloseException e) {
        Thread.currentThread().interrupt();
      }
    }));
    Thread.sleep(1000);
    System.out.println("Number of thread who waiting for permits " + semaphore.waitingForPermits());
  }
}
