package com.sawsent.simplecardgame.model.game;

import com.sawsent.simplecardgame.model.Play;
import com.sawsent.simplecardgame.model.board.Slot;
import com.sawsent.simplecardgame.model.board.Table;
import com.sawsent.simplecardgame.model.player.Hand;
import com.sawsent.simplecardgame.model.User;
import com.sawsent.simplecardgame.model.player.Player;
import com.sawsent.simplecardgame.model.player.PlayerDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Component
public class Game {
    private boolean started;
    private List<Player> players;
    private List<User> users;
    private int round;
    private int currentPlayerIndex;
    private HashMap<String, Integer> lastRoundResult;
    private GameState state;

    @PostConstruct
    public void init() {
        this.started = false;
        this.players = new ArrayList<>();
        this.users = new ArrayList<>();
        this.round = 0;
        this.currentPlayerIndex = 0;
        this.state = GameState.WAITING_FOR_PLAYERS;
        this.lastRoundResult = new HashMap<>();
    }

    public Hand connectPlayer(User user) {
        Player connectedPlayer = new Player(user);
        this.players.add(connectedPlayer);
        this.users.add(user);
        this.lastRoundResult.put(user.getUsername(), -1);
        return connectedPlayer.getHand();
    }

    public MetaInfo getMetaInfo() {
        return new MetaInfo(this.getState(),
                            PlayerDTO.fromPlayers(this.players),
                            PlayerDTO.fromPlayer(this.players.get(currentPlayerIndex)),
                            this.round,
                            winner(),
                            lastRoundResult);
    }

    public void start() {
        this.started = true;
        this.round += 1;
        this.state = GameState.ROUND_ONGOING;
    }

    public User getUserByUsername(String username) {
        for (User u : users) {
            if (username.equals(u.getUsername())) {
                return u;
            }
        }
        return null;
    }

    public boolean canStart() {
        return users.size() == 2 && users.stream().filter(User::isReady).toList().size() == users.size();
    }

    public Table convertToTable() {
        Table out = new Table();
        out.setMetaInfo(getMetaInfo());

        HashMap<String, Slot> map = new HashMap<>();

        for (Player p : players) {
            map.put(p.getUser().getUsername(), new Slot(p.getPlayedCard() == -1, state == GameState.ROUND_ONGOING ? -1 : p.getPlayedCard()));
        }

        out.setCardSlots(map);

        return out;
    }

    public boolean validatePlay(Play play) {
        Player currentPlayer = players.get(currentPlayerIndex);
        if (!currentPlayer.getUser().getUsername().equals(play.getUsername())) {
            return false;
        }
        return currentPlayer.getHand().getCards().contains(play.getCard());
    }

    public void doPlay(Play play) {
        if (state == GameState.GAME_FINISHED) {
            return;
        }

        Player currentPlayer = players.get(currentPlayerIndex);
        int card = play.getCard();

        currentPlayer.getHand().getCards().remove(Integer.valueOf(card));
        currentPlayer.setPlayedCard(card);
        currentPlayerIndex = nextPlayerIndex();

        if (currentPlayerIndex == 0) {
            round++;
            state = GameState.ROUND_FINISHED;
            if (players.get(0).getPlayedCard() > players.get(1).getPlayedCard()) {
                players.get(0).gainPoint();
            } else if (players.get(0).getPlayedCard() < players.get(1).getPlayedCard()) {
                players.get(1).gainPoint();
            }
            for (Player p : players) {
                lastRoundResult.replace(p.getUser().getUsername(), p.getPlayedCard());
                p.setPlayedCard(-1);
            }
            if (round == 5) {
                state = GameState.GAME_FINISHED;
            }
        }

    }

    private String winner() {
        if (state != GameState.GAME_FINISHED) {
            return "unfinished";
        }

        if (players.get(0).getPoints() == players.get(1).getPoints()) {
            return "noone, it was a tie";
        }
        if (players.get(0).getPoints() > players.get(1).getPoints()) {
            return players.get(0).getUser().getUsername();
        }
        return players.get(1).getUser().getUsername();

    }

    private int nextPlayerIndex() {
        if (currentPlayerIndex == 0) {
            return 1;
        }
        return 0;
    }
}
