package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

public class CheapestPooledGlobalTimeoutWithoutInvokeAll {
  private final ExecutorService executorService;
  private final ScheduledExecutorService schedule;
  private final String item;
  private final int timeoutMilliPerRequest;
  private final int timeoutMilliGlobal;


  public CheapestPooledGlobalTimeoutWithoutInvokeAll(String item, int timeoutMilliPerRequest, int poolSize, int timeoutMilliGlobal) {
    Objects.requireNonNull(item);
    if (poolSize <= 0 || timeoutMilliPerRequest < 0 || timeoutMilliGlobal < 0) {
      throw new IllegalArgumentException();
    }
    executorService = Executors.newFixedThreadPool(poolSize);
    schedule = Executors.newScheduledThreadPool(1);
    this.item = item;
    this.timeoutMilliPerRequest = timeoutMilliPerRequest;
    this.timeoutMilliGlobal = timeoutMilliGlobal;
  }

  public Optional<Answer> retrieve() throws InterruptedException {
    try {
      var futures = Request.ALL_SITES.stream()
              .<Callable<Answer>>map(s -> () -> {
                var answer = new Request(s, item).request(timeoutMilliPerRequest);
                if (!answer.isSuccessful()) {
                  throw new IllegalArgumentException("no answer");
                }
                return answer;
              }).map(executorService::submit)
              .toList();
      var answers = new ArrayList<Answer>();
      schedule.schedule(() -> futures.forEach(f -> f.cancel(false)), timeoutMilliGlobal, TimeUnit.MILLISECONDS);
      for (var future : futures) {
        try {
          answers.add(future.get());
        } catch (ExecutionException ignored) {
        } catch (CancellationException e) {
          return futures.stream().filter(f -> f.state() == Future.State.SUCCESS)
                  .map(Future::resultNow)
                  .min(Answer.ANSWER_COMPARATOR);
        }
      }
      return answers.stream().min(Answer.ANSWER_COMPARATOR);
    } finally {
      executorService.shutdownNow();
      schedule.shutdown();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var agregator = new CheapestPooledGlobalTimeoutWithoutInvokeAll("pikachu", 2_000, 5, 1500);
    var answer = agregator.retrieve();
    System.out.println(answer);
  }
}
