package fr.uge.concurrence.ex03;

public class ExchangerReuse<V> {
  private enum State {
    START, IN_PROGRESS, FINISH
  }
  private V value;
  private State state;
  private final Object lock = new Object();

  public ExchangerReuse() {
    synchronized (lock) {
      state = State.START;
    }
  }
  public V exchange(V instantValue) throws InterruptedException {
    synchronized (lock) {
      V ret = null;
      switch (state) {
        case START -> {
          value = instantValue;
          state = State.IN_PROGRESS;
          lock.notifyAll();
          while (state != State.FINISH) {
            lock.wait();
          }
          state = State.START;
          lock.notifyAll();
          ret = value;
        }
        case IN_PROGRESS -> {
          ret = value;
          value = instantValue;
          state = State.FINISH;
          lock.notifyAll();
        }
        case FINISH -> {
          while (state == State.FINISH) {
            lock.wait();
          }
          ret = exchange(instantValue);
        }
      }
      return ret;
    }
  }
}
