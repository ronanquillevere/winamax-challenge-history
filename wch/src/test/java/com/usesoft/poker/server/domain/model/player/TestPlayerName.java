package com.usesoft.poker.server.domain.model.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.usesoft.poker.server.domain.common.BaseValueObject;
import com.usesoft.poker.server.domain.model.player.PlayerName;

public class TestPlayerName {

    @Test
    public void testInvariant() {
        try {
            new PlayerName(null);
            fail("Name should be mandatory");
        } catch (Exception e) {
        }
    }

    @Test
    public void test() {
        PlayerName name = new PlayerName("toto");
        PlayerName name2 = new PlayerName("toto");
        PlayerName name3 = new PlayerName("TOTO");
        BaseValueObject<PlayerName> test = new PlayerName("toto");

        assertFalse(name == name2);
        assertTrue(name.equals(name2));
        assertFalse(name.equals(name3));
        assertTrue(test.equals(name2));
        
        assertFalse(name.sameValueAs(null));
        assertTrue(name.sameValueAs(name2));
        assertFalse(name.sameValueAs(name3));
    }
    
    @Test
    public void testToString() {
        String n = "toto";
        PlayerName name = new PlayerName(n);
        assertEquals(n, name.toString());
    }
}
