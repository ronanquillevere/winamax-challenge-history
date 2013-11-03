package com.usesoft.poker.client.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class MyModule extends AbstractGinModule
{
    @Override
    protected void configure()
    {
        bind(EventBus.class).to(SimpleEventBus.class);
    }
}
