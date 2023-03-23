package com.example.demo;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;

public class DojoReactiveTest {

    @Test
    void converterData(){
        List<Player> list = CsvUtilFile.getPlayers();
        assert list.size() == 18207;
    }

    @Test
    void jugadoresMayoresA35() {                                      //299 - 305
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> flux = Flux.fromIterable(list)
                .filter(player -> player.age >=35)
                .distinct();

        flux.subscribe(player -> System.out.println(player.getName() +" , " + player.getAge()+" , " + player.getIcon()));
    }


@Test
    void jugadoresMayoresA35SegunClub(){
        List<Player> list = CsvUtilFile.getPlayers();
        Flux.fromIterable(list)
                .filter(player -> player.age >=35)
                .groupBy(Player::getClub)
                .subscribe(group -> {
                    System.out.println(group.key() + ": ");
                    group.subscribe(System.out::println);
                });

    }


    @Test
    void mejorJugadorConNacionalidadFrancia(){
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> playerFrenchFlux = Flux.fromIterable(list);

        playerFrenchFlux
                .filter(player -> player.national.equals("France"))
                .sort(Comparator.comparingInt(Player::getWinners).reversed())
                .next()
                .subscribe(player -> {
                    System.out.println("-El mejor jugador frances es: " + player.getName());
                    System.out.println("-Con un total de juegos ganados de: " + player.getWinners());
                    System.out.println("-Con un total de juegos jugados de: " + player.getGames());
                    System.out.println("Actualmente juega en el: " + player.getClub());
                });



    }

    @Test
    void clubsAgrupadosPorNacionalidad(){

        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> playerFlux = Flux.fromIterable(list);

        playerFlux.groupBy(Player::getNational)
                .flatMap(group -> {
                    String nationality = group.key();
                    return group.map(Player::getClub)
                            .collectList()
                            .map(clubs -> new AbstractMap.SimpleEntry<>(nationality, clubs));
                    })
                .subscribe(entry ->{
                    String nationality = entry.getKey();
                    List<String> clubs = entry.getValue();

                    System.out.println(nationality + ": ");
                    clubs.forEach(System.out::println);
                });




    }

    @Test
    void clubConElMejorJugador(){
        List<Player> list = CsvUtilFile.getPlayers();

        Mono<Player> bestPlayerMono = Flux.fromIterable(list)
                .reduce((player1, player2) -> player1.getWinners() > player2.getWinners() ? player1 : player2); //el ultimo con max victorias

        bestPlayerMono.subscribe(bestPlayer -> {
            System.out.println("El club con el mejor jugador es: " + bestPlayer.getClub());
        });

    }

    @Test
    void clubConElMejorJugador2() {
        List<Player> list = CsvUtilFile.getPlayers();

        Mono<Player> bestPlayerMono = Flux.fromIterable(list)
                .reduce((player1, player2) -> {
                    if (player1.getWinners() == player2.getWinners()) {
                        // Si la cantidad de victorias es igual, conservo el primer elemento
                        return list.indexOf(player1) < list.indexOf(player2) ? player1 : player2;
                    } else {
                        return player1.getWinners() > player2.getWinners() ? player1 : player2;
                    }
                });

        bestPlayerMono.subscribe(bestPlayer -> {
            System.out.println("El club con el mejor jugador es: " + bestPlayer.getClub());
        });
    }

    @Test
    void ElMejorJugador() {
        List<Player> list = CsvUtilFile.getPlayers();

        Mono<Player> bestPlayerMono = Flux.fromIterable(list)
                .reduce((player1, player2) -> player1.getWinners() >= player2.getWinners() ? player1 : player2)
                .switchIfEmpty(Mono.justOrEmpty(null));

        bestPlayerMono.subscribe(bestPlayer -> {

                System.out.println("El mejor jugador es: " + bestPlayer.getName());

        });
    }

    @Test
    void mejorJugadorSegunNacionalidad(){

        List<Player> list = CsvUtilFile.getPlayers();

        Flux.fromIterable(list)
                .groupBy(Player::getNational)
                .flatMap(group -> group.reduce((player1, player2) -> player1.getWinners() >= player2.getWinners() ? player1 : player2)
                        .map(bestPlayer -> {
                            System.out.println("El mejor jugador de " + group.key() + " es: " + bestPlayer.getName() + " -Actualmente juega en el club:  " + bestPlayer.getClub());
                            return bestPlayer;
                        }))
                .subscribe();
    }



}
