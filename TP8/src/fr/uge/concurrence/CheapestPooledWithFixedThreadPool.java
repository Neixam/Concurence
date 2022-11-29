package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.IntStream;

public class CheapestPooledWithFixedThreadPool {
  private final String item;
  private final int timeoutMilliPerRequest;
  private final FixedThreadPool fixedThreadPool;

  private final static int NB_SITES = Request.ALL_SITES.size();

  private final ArrayBlockingQueue<Request> sitesQueue = new ArrayBlockingQueue<>(10);
  private final ArrayBlockingQueue<Answer> answersQueue = new ArrayBlockingQueue<>(10);

  public CheapestPooledWithFixedThreadPool(String item, int timeoutMilliPerRequest, int poolSize) {
    this.item = item;
    this.timeoutMilliPerRequest = timeoutMilliPerRequest;
    fixedThreadPool = new FixedThreadPool(poolSize);
  }

  public Optional<Answer> retrieve() throws InterruptedException {
    IntStream.range(0, Request.ALL_SITES.size()).forEach(j -> {
      try {
        fixedThreadPool.submit(() -> {
          var request = sitesQueue.take();
          answersQueue.put(request.request(timeoutMilliPerRequest));
        });
      } catch (InterruptedException e) {
        System.exit(0);
      }
    });
    fixedThreadPool.start();
    for (var s : Request.ALL_SITES) {
      sitesQueue.put(new Request(s, item));
    }
    try {
      var answers = new ArrayList<Answer>();
      for (int i = 0; i < NB_SITES; i++) {
        answers.add(answersQueue.take());
      }
      return answers.stream().filter(Answer::isSuccessful).min(Answer.ANSWER_COMPARATOR);
    } finally {
      fixedThreadPool.stop();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var agregator = new CheapestPooledWithFixedThreadPool("pikachu", 2_000, 5);
    var answer = agregator.retrieve();
    System.out.println(answer);
  }
}
