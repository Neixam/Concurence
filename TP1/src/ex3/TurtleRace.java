package ex3;

import java.util.stream.IntStream;

public class TurtleRace {
	public static void main(String[] args) throws InterruptedException {
        System.out.println("On your mark!");
        Thread.sleep(30_000);
        System.out.println("Go!");
        int[] times = {25_000, 10_000, 20_000, 5_000, 50_000, 60_000};
        IntStream.range(0, times.length).forEach(i -> Thread.ofPlatform().start(() -> {
            try {
                Thread.sleep(times[i]);
                System.out.println("Turtle " + i + " has finished");
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        }));
    }
}
