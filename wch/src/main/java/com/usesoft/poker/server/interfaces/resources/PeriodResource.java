package com.usesoft.poker.server.interfaces.resources;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.usesoft.poker.server.domain.model.period.Period;
import com.usesoft.poker.server.infrastructure.persistence.datastore.PeriodRepositoryDatastore;

@Path(URLConstants.API_VERSION_1 + URLConstants.ENTITY_PERIODS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PeriodResource {

    @GET
    public Collection<Period> getObjects()
    {
        return PeriodRepositoryDatastore.INSTANCE.findAll();
    }

    @GET
    @Path("{id}")
    public Period getObject(@PathParam("id") String id)
    {
        return PeriodRepositoryDatastore.INSTANCE.findById(id);
    }
}
