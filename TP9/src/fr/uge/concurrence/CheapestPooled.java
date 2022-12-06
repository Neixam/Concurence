package fr.uge.concurrence;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CheapestPooled {
  private final ExecutorService executorService;
  private final String item;
  private final int timeoutMilliPerRequest;

  public CheapestPooled(String item, int timeoutMilliPerRequest, int poolSize) {
    Objects.requireNonNull(item);
    if (poolSize <= 0 || timeoutMilliPerRequest < 0) {
      throw new IllegalArgumentException();
    }
    executorService = Executors.newFixedThreadPool(poolSize);
    this.item = item;
    this.timeoutMilliPerRequest = timeoutMilliPerRequest;
  }

  public Optional<Answer> retrieve() throws InterruptedException {
    try {
      var callables = Request.ALL_SITES.stream()
              .<Callable<Answer>>map(s -> () -> {
                var answer = new Request(s, item).request(timeoutMilliPerRequest);
                if (!answer.isSuccessful()) {
                  throw new IllegalArgumentException("no answer");
                }
                return answer;
              }).toList();
      var futures = executorService.invokeAll(callables);
      return futures.stream().filter(f -> f.state() == Future.State.SUCCESS)
              .map(Future::resultNow)
              .min(Answer.ANSWER_COMPARATOR);
    } finally {
      executorService.shutdown();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var agregator = new CheapestPooled("pikachu", 2_000, 5);
    var answer = agregator.retrieve();
    System.out.println(answer);
  }
}
