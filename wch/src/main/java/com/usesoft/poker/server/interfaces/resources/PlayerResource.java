package com.usesoft.poker.server.interfaces.resources;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.usesoft.poker.server.domain.model.player.Player;
import com.usesoft.poker.server.infrastructure.persistence.datastore.PlayerRepositoryDatastore;

@Path("v1/players")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlayerResource {

    @GET
    public Collection<Player> getObjects()
    {
        return PlayerRepositoryDatastore.INSTANCE.findAll();
    }
    
    @GET
    @Path("{playerName}")
    public Player getObject(@PathParam("playerName") String playerName)
    {
        Player p = PlayerRepositoryDatastore.INSTANCE.find(playerName);
        return p;
    }

}
