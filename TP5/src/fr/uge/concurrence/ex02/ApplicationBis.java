package fr.uge.concurrence.ex02;

import com.domo.Heat4J;

import java.util.List;

public class ApplicationBis {

  public static void main(String[] args) throws InterruptedException {
    var rooms = List.of("bedroom1", "bedroom2", "kitchen", "dining-room", "bathroom", "toilets");

    var temperatures = new ThreadInfoBis<Integer>(rooms.size());

    for (String room : rooms) {
      Thread.ofPlatform().daemon().start(() -> {
        for (;;) {
          try {
            var temperature = Heat4J.retrieveTemperature(room);
            System.out.println("Temperature in room " + room + " : " + temperature);
            temperatures.add(temperature, room);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
      });
    }

    for (;;) {
      System.out.println(temperatures.getValues().stream().mapToInt(Integer::intValue).average().getAsDouble());
    }
  }
}
