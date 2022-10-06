package ex1;

import java.util.stream.IntStream;

public class HelloThread {
	public static void main(String[] args) {
        IntStream.range(0, 4)
                .forEach(i -> Thread.ofPlatform().start(() -> IntStream.range(0, 5000)
                            .mapToObj(j -> "hello " + i + " " + j)
                            .forEach(System.out::println)));
    }
}