package com.sawsent.simplecardgame.controller;

import com.sawsent.simplecardgame.message.Message;
import com.sawsent.simplecardgame.model.Play;
import com.sawsent.simplecardgame.model.board.Table;
import com.sawsent.simplecardgame.model.game.Game;
import com.sawsent.simplecardgame.model.game.MetaInfo;
import com.sawsent.simplecardgame.model.player.Hand;
import com.sawsent.simplecardgame.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.logging.Logger;
import java.util.logging.StreamHandler;

@Controller
public class GameController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Game game;
    private static final Logger logger = Logger.getLogger(GameController.class.getName());

    @Autowired
    public GameController(SimpMessagingTemplate messagingTemplate, Game game) {
        this.messagingTemplate = messagingTemplate;
        this.game = game;
    }

    @MessageMapping("/connect")
    @SendTo("/topic/game-updates")
    public Table connect(User user) {
        logger.info("Received connection request from user: " + user.getUsername());
        // Authenticate the user (this is a simple placeholder, replace with real authentication logic)
        if (authenticateUser(user.getUsername(), user.getPassword())) {
            if (! game.isStarted()) {
                if (game.getPlayers().size() < 2) {
                    Hand startingHand = game.connectPlayer(user);
                    messagingTemplate.convertAndSendToUser(user.getUsername(), "/queue/hand", startingHand);
                }
            } else {
                if (game.getUsers().contains(user)) {
                    Hand startingHand = game.connectPlayer(user);
                    messagingTemplate.convertAndSendToUser(user.getUsername(), "/queue/hand", startingHand);
                }
            }
        }

        return game.convertToTable();
    }

    @MessageMapping("/ready")
    public void ready(@RequestParam String username) {
        logger.info("User " + username + " has readied up!");

        User user = game.getUserByUsername(username);
        user.setReady(true);

        if (game.canStart()) {
            game.start();
        }

        messagingTemplate.convertAndSend("/topic/game-updates", game.convertToTable());
    }

    @MessageMapping("/play")
    public void play(Play play) {
        if (! game.validatePlay(play)) {
            logger.warning("invalid play! ");
            return;
        }

        game.doPlay(play);
        messagingTemplate.convertAndSend("/topic/game-updates", game.convertToTable());

    }

    @GetMapping("/api/v1/table")
    @ResponseBody
    public Table table() {
        return game.convertToTable();
    }

    @GetMapping("/api/v1/game")
    @ResponseBody
    public Game game() {
        return game;
    }

    private boolean authenticateUser(String username, String password) {
        return username != null && !username.isEmpty() && password != null && !password.isEmpty();
    }

    public void sendMessageToPlayer(String username, String message) {
        messagingTemplate.convertAndSendToUser(username, "/queue/messages", message);
    }
}
