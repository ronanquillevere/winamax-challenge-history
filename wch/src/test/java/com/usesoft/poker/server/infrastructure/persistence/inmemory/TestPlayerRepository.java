package com.usesoft.poker.server.infrastructure.persistence.inmemory;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.domain.model.player.PlayerRepository;

public class TestPlayerRepository {

    private PlayerRepository repo = PlayerRepositoryInMemory.INSTANCE;

    @Before
    public void beforeTest(){
        ((PlayerRepositoryInMemory) repo).clear();
    }

    @Test
    public void test(){

        assertEquals(0,repo.findAll().size());

        String ronanFN = "Ronan";
        Player ronan = new Player(ronanFN);
        repo.store(ronan);

        assertEquals(repo.find(ronanFN), ronan);
        assertEquals(1,repo.findAll().size());

        repo.store(ronan);
        assertEquals(1,repo.findAll().size());

        repo.store(new Player("Kim"));
        assertEquals(2,repo.findAll().size());
    }
}
