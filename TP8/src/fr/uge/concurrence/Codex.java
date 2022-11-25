package fr.uge.concurrence;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.IntStream;

public class Codex {
  public static void main(String[] args) {
    var blockingQueueCoded = new ArrayBlockingQueue<String>(10);
    var blockingQueueDecoded = new ArrayBlockingQueue<String>(10);
    IntStream.range(1, 4).forEach(i -> Thread.ofPlatform().start(() -> {
      while (!Thread.interrupted()) {
        try {
          var codedMessage = CodeAPI.receive();
          System.out.println("ReceiverThread " + i + " receive " + codedMessage);
          blockingQueueCoded.put(codedMessage);
          System.out.println("ReceiverThread " + i + " send " + codedMessage);
        } catch (InterruptedException e) {
          return;
        }
      }
    }));
    IntStream.range(1, 3).forEach(i -> Thread.ofPlatform().start(() -> {
      while (!Thread.interrupted()) {
        try {
          var codedMessage = blockingQueueCoded.take();
          System.out.println("DecoderThread " + i + " receive " + codedMessage);
          var decodedMessage = CodeAPI.decode(codedMessage);
          System.out.println("DecoderThread " + i + " decode " + codedMessage + " to " + decodedMessage);
          blockingQueueDecoded.put(decodedMessage);
          System.out.println("DecoderThread " + i + " send " + decodedMessage);
        } catch (InterruptedException e) {
          return;
        } catch (IllegalArgumentException ignored) {

        }
      }
    }));
    Thread.ofPlatform().start(() -> {
      while (!Thread.interrupted()) {
        try {
          var decodedMessage = blockingQueueDecoded.take();
          System.out.println("ArchiverThread receive " + decodedMessage);
          CodeAPI.archive(decodedMessage);
          System.out.println("ArchiverThread archive " + decodedMessage);
        } catch (InterruptedException e) {
          return;
        }
      }
    });
  }
}
