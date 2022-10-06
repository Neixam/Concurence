# <p align=center>RAPPORT TP1 Concurence

## Exercice 1

#### Rappeler à quoi sert un Runnable.

Un `Runnable` est une interface fonctionnelle qui possède une méthode
__void run(void)__

#### Écrire, dans un premier temps, une classe HelloThread qui crée et démarre 4 threads (et faire en sorte qu'on puisse facilement en demander 150) qui affichent les nombres de 0 à 5 000 (sans le numéro unique par thread, donc).

```java
public class HelloThread {
    public static void main(String[] args) {
        IntStream.range(0, 4)
                .forEach(i -> Thread.ofPlatform().start(() -> IntStream.range(0, 5000)
                        .forEach(System.out::println)));
    }
}
```


#### Exécutez le programme plusieurs fois, que remarque-t-on ? Puis, en regardant l'affichage (scroller au besoin), qu'y a-t-il de bizarre ? Est-ce que tout ceci est bien normal ?

On remarque que l'éxécution n'est pas la meme alors qu'on ne touche pas au code.
On remarque que les threads ne sont pas éxécuté dans l'ordre de leur création.
Cela est tout à fait normal puisque le scheduler n'est pas predictible et n'a
pas le meme comportement à chaque éxécution

#### Modifiez votre code pour afficher en plus le numéro de chaque thread (sans utiliser le numéro du thread, juste la variable de boucle).

```java
public class HelloThread {
    public static void main(String[] args) {
        IntStream.range(0, 4)
                .forEach(i -> Thread.ofPlatform().start(() -> IntStream.range(0, 5000)
                        .mapToObj(j -> "hello " + i + " " + j)
                        .forEach(System.out::println)));
    }
}
```

## Exercice 2

#### Recopiez le programme de l'exercice précédent dans une nouvelle classe HelloThreadJoin puis modifiez le pour que soit affiché le message "le programme est fini" lorsque tous les threads ont fini leurs calculs.

```java
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
```

## Exercice 3

#### Réaliser la classe TurtleRace. Bien entendu, votre code doit fonctionner si l'on modifie le tableau times.

```java
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
```

#### Juste après avoir exécuté la classe TurtleRace, lancez jconsole dans un terminal.

#### Observer l'évolution du nombre de threads. Que devient le thread main ? Quand est-ce que la JVM s'éteint ?

Le thread main meurt dès le lancement des autres threads.
La JVM s'éteint lorsque la dernière tortue est arrivée, càd lors de la mort
du dernier thread en vie.

## Exercice 4

#### Exécuter la classe HelloThreadBis et comparer la sortie avec ce que vous avez obtenu avec la classe HelloThread de l'exercice 1.

On observe un chevauchement dans les affichages qui ne sont pas présent dans le premier exercice

#### Expliquer le comportement observé. Pourquoi ce comportement n’apparaît-il pas quand on utilise System.out.println ?

Je pense cela est du au fait que l'on affiche caractère par caractère ce qui peut
provoquer des chevauchements, car lors de l'affichage d'une chaine on peut
etre changé de thread par le scheduler.
Sur __System.out.println__ il y a une protection qui permet de forcer l'écriture
de la chaine entière.