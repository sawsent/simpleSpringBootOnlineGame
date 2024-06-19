package com.sawsent.simplecardgame.model.player;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Hand {
    private List<Integer> cards;

    public static Hand getPopulated() {
        Hand out = new Hand(new ArrayList<>());
        for (int i = 0; i < 4; i++) {
            out.cards.add((int) (Math.random() * 10));
        }
        return out;
    }
}