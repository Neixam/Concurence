package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

public class ThePriceIsRight {
  private final ArrayList<Integer> purposing = new ArrayList<>();
  private int actualPurpose;
  private Predicate<Integer> predicate = i -> true;
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition resultHere = lock.newCondition();
  private final int toFind;
  private final int players;

  private int winner = -1;
  public ThePriceIsRight(int price, int players) {
    if (players <= 0) {
      throw new IllegalArgumentException("");
    }
    this.players = players;
    toFind = price;
  }

  private int distance(int price) {
    return Math.abs(price - toFind);
  }

  private int findWinner() {
    if (winner == -1) {
      var purposeWinner = purposing.stream()
              .filter(i -> predicate.test(purposing.indexOf(i)))
              .min(Comparator.comparing(this::distance));
      purposeWinner.ifPresent(integer -> winner = purposing.indexOf(integer));
    }
    return winner;
  }

  public boolean propose(int price) {
    var position = 0;
    lock.lock();
    try {
      if (actualPurpose == players) {
        return false;
      }
      position = actualPurpose++;
      System.out.println(position);
      purposing.add(price);
      while (actualPurpose != players) {
        resultHere.await();
      }
      resultHere.signalAll();
      var purposeWinner = findWinner();
      System.out.println(position + " " + purposeWinner);
      return position == purposeWinner;
    } catch (InterruptedException e) {
      var finalPosition = position;
      predicate = predicate.and(i -> finalPosition != i);
      actualPurpose = players;
      resultHere.signalAll();
      return false;
    } finally {
      lock.unlock();
    }
  }
}
