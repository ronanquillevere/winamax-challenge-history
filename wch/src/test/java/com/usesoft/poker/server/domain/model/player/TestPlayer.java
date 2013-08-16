package com.usesoft.poker.server.domain.model.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.usesoft.poker.server.domain.model.common.EntityUtil;

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
        Player p = new Player(new PlayerName(n));
        Player p2 = new Player(new PlayerName(n));
        Player p3 = new Player(new PlayerName("TOTO"));

        EntityUtil.checkValues(p, p2, p3);
    }
    
    @Test
    public void testToString() {
        String n = "toto";
        Player player1 = new Player(new PlayerName(n));
        assertEquals(n, player1.toString());
    }
}
