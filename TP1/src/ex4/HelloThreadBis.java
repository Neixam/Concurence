package ex4;

import java.util.stream.IntStream;

public class HelloThreadBis {
    public static void println(String s){
        for(var i = 0; i < s.length(); i++){
            System.out.print(s.charAt(i));
        }
        System.out.print("\n");
    }

    public static void main(String[] args) {
        IntStream.range(0, 4)
                .forEach(i -> Thread.ofPlatform().start(() -> IntStream.range(0, 5000)
                        .mapToObj(j -> "hello " + i + " " + j)
                        .forEach(HelloThreadBis::println)));
    }
}
