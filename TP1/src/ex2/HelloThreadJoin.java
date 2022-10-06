package ex2;

import java.util.stream.IntStream;

public class HelloThreadJoin {
    public static void main(String[] args) throws InterruptedException {
        var threads = IntStream.range(0, 4)
                .mapToObj(i -> Thread.ofPlatform().start(() -> IntStream.range(0, 5000)
                        .mapToObj(j -> "hello " + i + " " + j)
                        .forEach(System.out::println)))
                .toList();
        for (var t : threads) {
            t.join();
        }
        System.out.println("le programme est fini");
    }
}
