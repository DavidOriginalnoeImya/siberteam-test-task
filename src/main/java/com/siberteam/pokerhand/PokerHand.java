package com.siberteam.pokerhand;

import org.javatuples.Pair;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class PokerHand implements Comparable<PokerHand> {

    enum Combination {
        ROYAL_FLUSH("Флеш-Рояль", 0, (r, s) -> isStrMatchPattern(r, "TJQKA") && isStrMatchPattern(s, "(.)\\1{4}")),
        STRAIGHT_FLUSH("Стрит-флеш", 1, (r, s) -> "23456789TJQK".contains(r) && isStrMatchPattern(s, "(.)\\1{4}")),
        FOUR("Каре", 2, (r, s) -> isStrMatchPattern(r, "(.)\\1{3}")),
        FULL_HOUSE("Фулл Хаус", 3, (r, s) -> isStrMatchPattern(r, "(.)\\1{2}(.)\\2{1}|(.)\\3{1}(.)\\4{2}")),
        FLUSH("Флеш", 4,  (r, s) -> isStrMatchPattern(s, "(.)\\1{4}")),
        STRAIGHT("Стрит", 5, (r, s) -> "23456789TJQK".contains(r)),
        THREE("Тройка", 6, (r, s) -> isStrMatchPattern(r, "(.)\\1{2}")),
        TWO_PAIR("Две пары", 7, (r, s) -> isStrMatchPattern(r, "(.)\\1{1}.*(.)\\2{1}")),
        PAIR("Пара", 8, (r, s) -> isStrMatchPattern(r, "(.)\\1{1}")),
        HIGH_CARD("Старшая карта", 9, (r, s) -> true);

        private final String name;
        private final int rating;
        private final BiPredicate<String, String> matcher;

        Combination(String name, int rating, BiPredicate<String, String> matcher) {
            this.name = name;
            this.rating = rating;
            this.matcher = matcher;
        }

        String getName() {
            return name;
        }

        int getRating() {
            return rating;
        }

        static List<Combination> getByRating() {
            return Arrays.stream(Combination.values()).toList();
        }

        private BiPredicate<String, String> getMatcher() {
            return matcher;
        }

        private static boolean isStrMatchPattern(String str, String pattern) {
            return Pattern
                    .compile(pattern)
                    .matcher(str).find();
        }
    }

    private static final Map<Character, Integer> honourCardRatings = Map.of(
            'T', 10, 'J', 11, 'Q', 12, 'K', 13, 'A', 14
    );

    private final String sortedCardRatings;
    private final Combination combination;

    public PokerHand(String cards) {
        if (!isValidHand(cards))
            throw new IllegalArgumentException("Недопустимый набор карт: " + cards);
        Pair<String,String> cardRatingsAndSuits = getCardRatingsAndSuits(sortCards(cards));
        sortedCardRatings = cardRatingsAndSuits.getValue0();
        combination = getCombination(cardRatingsAndSuits.getValue0(), cardRatingsAndSuits.getValue1());
    }

    public String getCombinationName() {
        return combination.getName();
    }

    public char getHighCard() {
        return sortedCardRatings.charAt(sortedCardRatings.length() - 1);
    }

    @Override
    public int compareTo(PokerHand pokerHand) {
        if (combination.equals(Combination.HIGH_CARD) &&
                pokerHand.combination.equals(Combination.HIGH_CARD)
        ) {
            return -compareHighPairCombinations(sortedCardRatings, pokerHand.sortedCardRatings);
        }

        return Integer.compare(combination.getRating(), pokerHand.combination.getRating());
    }

    public boolean isValidHand(String cards) {
        return cards != null && cards.matches("([2-9TJQKA][HSCD]\\s){4}[2-9TJQKA][HSCD]") &&
                Arrays.stream(cards.split(" ")).collect(Collectors.toSet()).size() == 5;
    }

    private int compareHighPairCombinations(String fComb, String sComb) {
        for (int index = fComb.length() - 1; index >= 0; --index) {
            if (fComb.charAt(index) != sComb.charAt(index)) {
                return Integer.compare(
                        honourCardRatings.getOrDefault(fComb.charAt(index), fComb.charAt(index) - 49),
                        honourCardRatings.getOrDefault(sComb.charAt(index), sComb.charAt(index) - 49)
                );
            }
        }

        return 0;
    }

    private Combination getCombination(String sortedCardRatings, String sortedCardSuits) {
        for (Combination combination: Combination.values()) {
            if (combination.getMatcher().test(sortedCardRatings, sortedCardSuits)) {
                return combination;
            }
        }

        throw new IllegalStateException("Ошибка определения комбинации");
    }

    private Pair<String, String> getCardRatingsAndSuits(String cards) {
        StringBuilder ratingsBuilder = new StringBuilder(),
                suitsBuilder = new StringBuilder();

        for (int index = 0; index < cards.length(); ++index) {
            if (index % 2 == 0) {
                ratingsBuilder.append(cards.charAt(index));
            }
            else {
                suitsBuilder.append(cards.charAt(index));
            }
        }

        return new Pair<>(ratingsBuilder.toString(), suitsBuilder.toString());
    }

    private String sortCards(String cards) {
        return Arrays
                .stream(cards.split(" "))
                .sorted(
                    Comparator.comparingInt(
                        c -> honourCardRatings
                                .getOrDefault(c.charAt(0), c.charAt(0) - 49)
                    )
                )
                .collect(Collectors.joining());
    }
}
