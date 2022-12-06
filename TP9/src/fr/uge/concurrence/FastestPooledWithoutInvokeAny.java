package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class FastestPooledWithoutInvokeAny {
  private final ExecutorService executorService;
  private final String item;
  private final int timeoutMilliPerRequest;
  private final SynchronousQueue<Answer> synchronousQueue = new SynchronousQueue<>();

  public FastestPooledWithoutInvokeAny(String item, int timeoutMilliPerRequest, int poolSize) {
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
      var futures = Request.ALL_SITES.stream()
              .<Callable<Answer>>map(s -> () -> {
                var answer = new Request(s, item).request(timeoutMilliPerRequest);
                if (!answer.isSuccessful()) {
                  throw new IllegalArgumentException("no answer");
                }
                return answer;
              }).map(executorService::submit)
              .toList();
      futures.forEach(f -> Thread.ofPlatform().daemon().start(() -> {
        try {
          synchronousQueue.put(f.get());
        } catch (InterruptedException | ExecutionException ignored) {
        }
      }));
      return Optional.of(synchronousQueue.take());
    } finally {
      executorService.shutdownNow();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var agregator = new FastestPooledWithoutInvokeAny("pikachu", 2_000, 5);
    var answer = agregator.retrieve();
    System.out.println(answer);
  }
}
