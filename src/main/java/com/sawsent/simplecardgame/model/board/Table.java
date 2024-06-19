package com.sawsent.simplecardgame.model.board;

import com.sawsent.simplecardgame.model.game.MetaInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Table {
    private MetaInfo metaInfo;
    private HashMap<String, Slot> cardSlots;
}

