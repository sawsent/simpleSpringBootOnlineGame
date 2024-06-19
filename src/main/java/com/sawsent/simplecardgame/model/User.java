package com.sawsent.simplecardgame.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private String username;
    private String password;
    private boolean ready;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User userObj) {
            return this.username.equals(userObj.getUsername()) && this.password.equals(userObj.getPassword());
        }
        return false;
    }
}
