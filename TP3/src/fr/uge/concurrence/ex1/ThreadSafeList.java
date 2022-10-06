package fr.uge.concurrence.ex1;

import java.util.ArrayList;

public class ThreadSafeList<T> {
  private final ArrayList<T> arrayList;

  public ThreadSafeList() {
    arrayList = new ArrayList<>();
  }

  public ThreadSafeList(int capacity) {
    arrayList = new ArrayList<>(capacity);
  }

  public void add(T value) {
    synchronized (arrayList) {
      arrayList.add(value);
    }
  }

  public int size() {
    synchronized (arrayList) {
      return arrayList.size();
    }
  }

  @Override
  public String toString() {
    synchronized (arrayList) {
      return arrayList.toString();
    }
  }
}
