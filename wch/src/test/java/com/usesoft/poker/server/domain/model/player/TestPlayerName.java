package com.usesoft.poker.server.domain.model.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.usesoft.poker.server.domain.common.BaseValueObject;
import com.usesoft.poker.server.domain.model.common.ValueObjectUtil;

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
        
        ValueObjectUtil.checkValues(name, name2, name3);
        assertTrue(test.equals(name2));
    }
    
    @Test
    public void testToString() {
        String n = "toto";
        PlayerName name = new PlayerName(n);
        assertEquals(n, name.toString());
    }
}
