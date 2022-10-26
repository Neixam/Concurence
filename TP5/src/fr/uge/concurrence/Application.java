package fr.uge.concurrence;

import java.util.List;

import com.domo.Heat4J;
import fr.uge.concurrence.ex02.ThreadInfo;

public class Application {
	public static void main(String[] args) throws InterruptedException {
		var rooms = List.of("bedroom1", "bedroom2", "kitchen", "dining-room", "bathroom", "toilets");

		var temperatures = new ThreadInfo<Integer>(rooms.size());

		for (String room : rooms) {
			Thread.ofPlatform().start(() -> {
				try {
					var temperature = Heat4J.retrieveTemperature(room);
					System.out.println("Temperature in room " + room + " : " + temperature);
					temperatures.add(temperature);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			});
		}

		System.out.println(temperatures.getValues().stream().mapToInt(Integer::intValue).average().getAsDouble());
	}
}
