package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.IntStream;

/**
 * Si Request.ALL_SITES contient 1000 sites 1000 threads seront créés et lancés simultanément, ce n'est pas du tout
 * raisonnable car on risque de faire un OutOfMemoryException.
 */
public class Cheapest {
  private final String item;
  private final int timeoutMilliPerRequest;

  public Cheapest(String item, int timeoutMilliPerRequest) {
    this.item = item;
    this.timeoutMilliPerRequest = timeoutMilliPerRequest;
  }

  /**
   * @return the cheapest price for item if it is sold
   */
  public Optional<Answer> retrieve() throws InterruptedException {
    var blockingQueueRequest = new ArrayBlockingQueue<Request>(10);
    var blockingQueueAnswer = new ArrayBlockingQueue<Answer>(10);
    var threads = new ArrayList<Thread>();
    IntStream.range(0, Request.ALL_SITES.size()).forEach(i -> threads.add(Thread.ofPlatform().start(() -> {
      while (!Thread.interrupted()) {
        try {
          var request = blockingQueueRequest.take();
          blockingQueueAnswer.put(request.request(timeoutMilliPerRequest));
        } catch (InterruptedException e) {
          return;
        }
      }
    })));
    Request.ALL_SITES.forEach(s -> {
      try {
        blockingQueueRequest.put(new Request(s, item));
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
    var answers = new ArrayList<Answer>();
    while (answers.size() != Request.ALL_SITES.size()) {
      answers.add(blockingQueueAnswer.take());
    }
    threads.forEach(Thread::interrupt);
    return answers.stream()
            .filter(Answer::isSuccessful)
            .min(Answer.ANSWER_COMPARATOR);
  }

  public static void main(String[] args) throws InterruptedException {
    var agregator = new Cheapest("pikachu", 2_000);
    var answer = agregator.retrieve();
    System.out.println(answer); // Optional[pikachu@darty.fr : 214]
  }
}
