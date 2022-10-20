package fr.uge.ex4;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Vote {
  private final HashMap<String, Integer> map;
  private final int totalVote;
  private int count;
  public Vote(int totalVote) {
    if (totalVote <= 0) {
      throw new IllegalArgumentException("0 >= value forbidden");
    }
    this.totalVote = totalVote;
    map = new HashMap<>();
  }

  private String computeWinner() {
    synchronized (map) {
      return map.entrySet().stream()
              .sorted(Comparator.comparingInt(Map.Entry::getValue))
              .map(Map.Entry::getKey)
              .findFirst()
              .orElseThrow();
    }
  }
  public String vote(String titu) {
    synchronized (map) {
      if (count == totalVote) {
        return computeWinner();
      }
      count++;
      map.compute(titu,  (k, v) -> v == null ? 1 : v + 1);
      return computeWinner();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var vote = new Vote(4);
    Thread.ofPlatform().start(() -> {
      try {
        Thread.sleep(2_000);
        System.out.println("The winner is " + vote.vote("un"));
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });
    Thread.ofPlatform().start(() -> {
      try {
        Thread.sleep(1_500);
        System.out.println("The winner is " + vote.vote("zero"));
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });
    Thread.ofPlatform().start(() -> {
      try {
        Thread.sleep(1_000);
        System.out.println("The winner is " + vote.vote("un"));
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });
    System.out.println("The winner is " + vote.vote("zero"));
  }
}
