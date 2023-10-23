package com.siberteam.pokerhand;

import com.siberteam.pokerhand.PokerHand.Combination;

import java.util.List;
import java.util.Map;

public class PokerHandUnitTest {
    private static final Map<Combination, String> diffCombinations = Map.of(
        Combination.ROYAL_FLUSH, "AS TS QS JS KS", Combination.STRAIGHT_FLUSH, "5C 3C 6C 2C 4C",
        Combination.FOUR, "2H 3C 2C 2S 2D", Combination.FULL_HOUSE, "AC 5C 5H AH 5D",
        Combination.FLUSH, "4C 2C 7C QC TC", Combination.STRAIGHT, "7H TH 8C 9S JH",
        Combination.THREE, "4C 8S 9H 4H 4S", Combination.TWO_PAIR, "2C 2H 7H TC 7S",
        Combination.PAIR, "6H KH TS KS 3S", Combination.HIGH_CARD, "7C 4H 2C QS JD"
    );

    private static final Map<Character, String> highCardCombinations = Map.of(
            'A', "5S 3H AC TC KC", 'K', "QC 6C JC 8H KS",
            'Q', "4H 9S TC 3C QC", 'J', "2D 9H 3C JH 4S",
            'T', "8S TC 7D 3D 2S", '9', "5S 7D 3D 8H 9D",
            '7', "7C 3C 2C 5H 6S"
    );

    public static void main(String[] args) {
        testDifferentCombinationNames();
        testDifferentCombinationsSort();
        testHighCardCombinationNames();
        testHighCardCombinationsSort();
        testHighCardCombinationsComparison();
    }

    public static void testDifferentCombinationNames() {
        for (Map.Entry<Combination, String> combination: diffCombinations.entrySet()) {
            PokerHand pokerHand = new PokerHand(combination.getValue());
            assert combination.getKey()
                    .getName()
                    .equals(pokerHand.getCombinationName());
        }
    }

    public static void testDifferentCombinationsSort() {
        List<PokerHand> hands = diffCombinations.values()
                .stream().map(PokerHand::new)
                .sorted().toList();

        List<Combination> sortedCombinations = Combination.getByRating();

        for (int index = 0; index < hands.size(); ++index) {
            String combinationName = sortedCombinations.get(index).getName(),
                    handName = hands.get(index).getCombinationName();

            assert combinationName.equals(handName);
        }
    }

    public static void testHighCardCombinationNames() {
        for (Map.Entry<Character, String> combination: highCardCombinations.entrySet()) {
            PokerHand pokerHand = new PokerHand(combination.getValue());

            String combinationName = Combination.HIGH_CARD.getName() + combination.getKey(),
                    handName = pokerHand.getCombinationName() + pokerHand.getHighCard();

            assert combinationName.equals(handName);
        }
    }

    public static void testHighCardCombinationsSort() {
        List<PokerHand> hands = highCardCombinations.values()
                .stream().map(PokerHand::new)
                .sorted().toList();

        List<Character> sortedHighCards = List.of(
            'A', 'K', 'Q', 'J', 'T', '9', '7'
        );

        assert hands.size() == sortedHighCards.size();

        for (int index = 0; index < hands.size(); ++index) {
            assert hands.get(index).getHighCard() == sortedHighCards.get(index);
        }
    }

    public static void testHighCardCombinationsComparison() {
        PokerHand fPokerHand = new PokerHand("2C 5H 4D QC AC");
        PokerHand sPokerHand = new PokerHand("2C 5H 4D QC AC");
        PokerHand tPokerHand = new PokerHand("3C 5H 4D QC AC");

        assert fPokerHand.compareTo(sPokerHand) == 0;
        assert tPokerHand.compareTo(fPokerHand) < 0;
    }
}