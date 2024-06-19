package com.sawsent.simplecardgame.model.game;

import com.sawsent.simplecardgame.model.game.GameState;
import com.sawsent.simplecardgame.model.player.Player;
import com.sawsent.simplecardgame.model.player.PlayerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MetaInfo {
    private GameState gameState;
    private List<PlayerDTO> players;
    private PlayerDTO currentPlayer;
    private int currentRound;
    private String winner;
    private HashMap<String, Integer> lastRoundResult;
}
