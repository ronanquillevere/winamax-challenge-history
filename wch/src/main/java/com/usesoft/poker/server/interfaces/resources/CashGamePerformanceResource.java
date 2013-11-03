package com.usesoft.poker.server.interfaces.resources;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.usesoft.poker.server.domain.model.cashgame.CashGamePerformance;
import com.usesoft.poker.server.infrastructure.persistence.datastore.CashGamePerformanceRepositoryDatastore;

@Path(URLConstants.API_VERSION_1 + URLConstants.ENTITY_CASHGAME_PERFORMANCES)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CashGamePerformanceResource {

    @GET
    public Collection<CashGamePerformance> getObjects()
    {
        return CashGamePerformanceRepositoryDatastore.INSTANCE.findAll();
    }


    @GET
    @Path("{perfId}")
    public CashGamePerformance getObject(@PathParam("perfId") String perfId)
    {
        CashGamePerformance p = CashGamePerformanceRepositoryDatastore.INSTANCE.findById(perfId);
        return p;
    }
}
