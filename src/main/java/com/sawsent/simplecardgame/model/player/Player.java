package com.sawsent.simplecardgame.model.player;

import com.sawsent.simplecardgame.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Player {
    private User user;
    private Hand hand;
    private int playedCard = -1;
    private int points = 0;

    public Player(User user) {
        this.user = user;
        this.hand = Hand.getPopulated();
    }

    public void gainPoint() {
        points += 1;
    }
}
