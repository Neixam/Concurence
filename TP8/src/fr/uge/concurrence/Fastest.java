package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.IntStream;

public class Fastest {
  private final String item;
  private final int timeoutMilliPerRequest;

  public Fastest(String item, int timeoutMilliPerRequest) {
    this.item = item;
    this.timeoutMilliPerRequest = timeoutMilliPerRequest;
  }

  /**
   * @return the cheapest price for item if it is sold
   */
  public Optional<Answer> retrieve() throws InterruptedException {
    var threads = new ArrayList<Thread>();
    try {
      var blockingQueueRequest = new ArrayBlockingQueue<Request>(10);
      var synchronousQueueAnswer = new SynchronousQueue<Answer>();
      IntStream.range(0, Request.ALL_SITES.size()).forEach(i -> threads.add(Thread.ofPlatform().start(() -> {
        while (!Thread.interrupted()) {
          try {
            var request = blockingQueueRequest.take();
            synchronousQueueAnswer.put(request.request(timeoutMilliPerRequest));
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
      var answer = synchronousQueueAnswer.take();
      return (answer.isSuccessful()) ? Optional.of(answer) : Optional.empty();
    } finally {
      threads.forEach(Thread::interrupt);
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var agregator = new Fastest("pikachu", 2_000);
    var answer = agregator.retrieve();
    System.out.println(answer); // Optional[pikachu@darty.fr : 214]
  }
}
