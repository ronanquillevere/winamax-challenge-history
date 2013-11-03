package com.usesoft.poker.server.domain.common;

import java.net.URISyntaxException;

public interface Identifiable
{
    String getId();

    String getType();

    String getUri() throws URISyntaxException;
}
