package fr.uge.concurrence;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

public class CheapestPooledWithGlobalTimeout {
  private final ExecutorService executorService;
  private final String item;
  private final int timeoutMilliPerRequest;
  private final int timeoutMilliGlobal;


  public CheapestPooledWithGlobalTimeout(String item, int timeoutMilliPerRequest, int poolSize, int timeoutMilliGlobal) {
    Objects.requireNonNull(item);
    if (poolSize <= 0 || timeoutMilliPerRequest < 0 || timeoutMilliGlobal < 0) {
      throw new IllegalArgumentException();
    }
    executorService = Executors.newFixedThreadPool(poolSize);
    this.item = item;
    this.timeoutMilliPerRequest = timeoutMilliPerRequest;
    this.timeoutMilliGlobal = timeoutMilliGlobal;
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
      var futures = executorService.invokeAll(callables, timeoutMilliGlobal, TimeUnit.MILLISECONDS);
      return futures.stream().filter(f -> f.state() == Future.State.SUCCESS)
              .map(Future::resultNow)
              .min(Answer.ANSWER_COMPARATOR);
    } finally {
      executorService.shutdown();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var agregator = new CheapestPooledWithGlobalTimeout("pikachu", 2_000, 5, 1_500);
    var answer = agregator.retrieve();
    System.out.println(answer);
  }
}
