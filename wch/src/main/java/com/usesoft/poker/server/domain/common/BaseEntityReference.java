package com.usesoft.poker.server.domain.common;

import java.net.URI;
import java.net.URISyntaxException;

import com.usesoft.poker.server.interfaces.resources.URLConstants;

public class BaseEntityReference implements EntityReference
{
    private final String id;
    private final String type;

    public BaseEntityReference(String id, String type)
    {
        this.id = id;
        this.type = type;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public String getUri() throws URISyntaxException
    {
        return buildUri(buildPath());
    }

    public static String buildUri(String unescapedPath) throws URISyntaxException
    {
        URI uri = new URI(null, null, unescapedPath, null, null);
        return uri.toString();
    }

    private String buildPath()
    {
        return URLConstants.API + URLConstants.API_VERSION_1 + getType().toLowerCase() + "s" + "/" + getId();
    }

}
