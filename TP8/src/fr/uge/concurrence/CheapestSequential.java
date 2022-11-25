package fr.uge.concurrence;

import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.Optional;

public class CheapestSequential {

    private final String item;
    private final int timeoutMilliPerRequest;

    public CheapestSequential(String item, int timeoutMilliPerRequest) {
        this.item = item;
        this.timeoutMilliPerRequest = timeoutMilliPerRequest;
    }

    /**
     * @return the cheapest price for item if it is sold
     */
    public Optional<Answer> retrieve() throws InterruptedException {
        try {
            return Request.ALL_SITES.stream().map(site -> {
                try {
                    return new Request(site, item).request(timeoutMilliPerRequest);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).filter(Answer::isSuccessful).min(Answer.ANSWER_COMPARATOR);
        } catch (RuntimeException e) {
            throw (InterruptedException) e.getCause();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var agregator = new CheapestSequential("pikachu", 2_000);
        var answer = agregator.retrieve();
        System.out.println(answer); // Optional[pikachu@darty.fr : 214]
    }
}
