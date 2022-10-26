package fr.uge.concurrence.ex01;

public class Exchanger<V> {
  private enum State {
    START, SEND, RECEIVE
  }
  private V value;
  private State pass;
  private final Object lock = new Object();

  public Exchanger() {
    synchronized (lock) {
      pass = State.START;
    }
  }

  public V exchange(V instantValue) throws InterruptedException {
    synchronized (lock) {
      if (pass == State.START) {
        pass = State.SEND;
        value = instantValue;
        while (pass != State.RECEIVE) {
          lock.wait();
        }
        return value;
      }
      pass = State.RECEIVE;
      var ret = value;
      value = instantValue;
      lock.notifyAll();
      return ret;
    }
  }
}
