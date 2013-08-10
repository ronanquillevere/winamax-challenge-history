package com.usesoft.poker.server.domain.model.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.usesoft.poker.server.domain.common.BaseValueObject;
import com.usesoft.poker.server.domain.model.player.PlayerName;

public class TestPlayer {

    @Test
    public void testInvariant() {
        try {
            new Player(null);
            fail("Name should be mandatory");
        } catch (Exception e) {
        }
    }

    @Test
    public void test() {
        String n = "toto";
        Player player1 = new Player(new PlayerName(n));
        Player player2 = new Player(new PlayerName(n));
        Player player3 = new Player(new PlayerName("TOTO"));

        assertFalse(player1 == player2);
        assertTrue(player1.equals(player2));
        assertFalse(player1.equals(player3));

        assertTrue(player1.sameIdentityAs(player2));
        assertFalse(player1.sameIdentityAs(player3));
    }
    
    @Test
    public void testToString() {
        String n = "toto";
        Player player1 = new Player(new PlayerName(n));
        assertEquals(n, player1.toString());
    }
}
