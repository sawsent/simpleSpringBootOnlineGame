package com.sawsent.simplecardgame.model.board;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Slot {
    private boolean empty;
    private int card;
}
