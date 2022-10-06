package fr.uge.concurrence;

/**
 * Il y a une data-race entre les champs firstName et lastName mais il n'y a pas de block synchronized qui garantie une
 * cohérence des données entre les threads, donc lorsqu'un thread tente de modifié les champs avec set et/ou tente de
 * l'afficher avec la méthode toString on peut avoir une valeur coupée en deux.
 *
 * On ne peut pas remplacer System.out.println(board); par System.out.println(board.firstName() + ' ' + board.lastName());
 * en utilisant des accesseurs classiques, car board.lastName peut changer après l'affichage du board.firstName.
 */
public class HonorBoard {
  private String firstName;
  private String lastName;
  private final Object lock = new Object();

  public String firstName() {
      return firstName;
  }
  public String lastName() {
      return lastName;
  }

  public void set(String firstName, String lastName) {
    synchronized (lock) {
      this.firstName = firstName;
      this.lastName = lastName;
    }
  }
  
  @Override
  public String toString() {
    synchronized (lock) {
      return firstName + ' ' + lastName;
    }
  }
  
  public static void main(String[] args) {
    var board = new HonorBoard();
    Thread.ofPlatform().start(() -> {
      for(;;) {
        board.set("Mickey", "Mouse");
      }
    });
    
    Thread.ofPlatform().start(() -> {
      for(;;) {
        board.set("Donald", "Duck");
      }
    });
    
    Thread.ofPlatform().start(() -> {
      for(;;) {
        System.out.println(board.firstName() + ' ' + board.lastName());
      }
    });
  }
}
