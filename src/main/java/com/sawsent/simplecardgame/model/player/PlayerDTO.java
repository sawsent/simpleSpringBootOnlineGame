package com.sawsent.simplecardgame.model.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlayerDTO {
    private String username;
    private int points;
    private boolean ready;

    public static PlayerDTO fromPlayer(Player player) {
        return new PlayerDTO(player.getUser().getUsername(), player.getPoints(), player.getUser().isReady());
    }

    public static List<PlayerDTO> fromPlayers(List<Player> players) {
        List<PlayerDTO> out = new ArrayList<>();
        for (Player p : players) {
            out.add(fromPlayer(p));
        }
        return out;
    }
}
