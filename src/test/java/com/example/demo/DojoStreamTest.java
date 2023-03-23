package com.example.demo;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class DojoStreamTest {

    @Test
    void converterData(){
        List<Player> list = CsvUtilFile.getPlayers();
        assert list.size() == 18207;
    }

    @Test
    void jugadoresMayoresA35(){
        List<Player> list = CsvUtilFile.getPlayers();
        var newList = list.stream().filter(player -> player.age >=35)
                .distinct()
                .collect(Collectors.toList());

        //newList.forEach(System.out::println);
        newList.forEach(player -> System.out.println(player.getName() +" , " + player.getAge()+" , " + player.getIcon()));

    }

    @Test
    void jugadoresMayoresA35SegunClub(){
        List<Player> list = CsvUtilFile.getPlayers();
        var newList = list.stream().filter(player -> player.age >=35)
                .flatMap(player1 -> list.parallelStream()
                        .filter(player2 -> player1.club.equals(player2.club)))
                .distinct()
                .collect(Collectors.groupingBy(Player::getClub));


        newList.forEach((s, players) -> {
            System.out.println(s+ "");
            players.forEach(System.out::println);
        });

    }

    @Test
    void mejorJugadorConNacionalidadFrancia(){
        List<Player> list = CsvUtilFile.getPlayers();

        Optional<Player> bestFrechPlayer = list.stream()
                .filter(player -> player.national.equals("France"))
                .sorted(Comparator.comparingInt(Player::getWinners).reversed())

                .findFirst();

               //.collect(Collectors.groupingBy(Player::getNational));

        System.out.println("-El mejor jugador frances es: " + bestFrechPlayer.get().getName());
        System.out.println("-Con un total de juegos ganados de: " + bestFrechPlayer.get().getWinners());
        System.out.println("-Con un total de juegos jugados de: " + bestFrechPlayer.get().getGames());
        System.out.println("Actualmente juega en el: " + bestFrechPlayer.get().getClub());

    }


    @Test
    void clubsAgrupadosPorNacionalidad(){
        List<Player> list = CsvUtilFile.getPlayers();
        Map<String, List<String>> clubsByNationality = list.stream()

                .collect(Collectors.groupingBy(Player::getNational, Collectors.mapping(Player::getClub, Collectors.toList())));

                clubsByNationality.forEach((nationality, clubs) -> {
                    System.out.println(nationality + ": ");
                    clubs.forEach(System.out::println);
                });

    }

    @Test
    void clubConElMejorJugador(){
        List<Player> list = CsvUtilFile.getPlayers();
        Player bestClub = list.stream()
                .max(Comparator.comparingDouble(Player::getWinners))
                .orElse(null);

        System.out.println("El club con el mejor jugador es: " + bestClub.getClub());

    }

    @Test
    void ElMejorJugador(){
        List<Player> list = CsvUtilFile.getPlayers();
        Player bestPlayer = list.stream()
                .max(Comparator.comparingInt(Player::getWinners)) //max, encuentra el mayor valor en la propiedad
                        .orElse(null);
        //.map(Player::getName);

        System.out.println("El mejor jugador es: " + bestPlayer.getName());
    }

    @Test
    void mejorJugadorSegunNacionalidad(){

        List<Player> list = CsvUtilFile.getPlayers();

        Map<String, Player> bestPlayerByNationality = list.stream()
                .collect(Collectors.groupingBy(Player::getNational, Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparingDouble(Player::getWinners)), Optional::get)));

        bestPlayerByNationality.forEach((nationality, bestPlayer) -> {
            System.out.println("El mejor jugador de " + nationality + " es: " + bestPlayer.getName() + " -Actualmente juega en el club:  " + bestPlayer.getClub());
        });

    }


}
