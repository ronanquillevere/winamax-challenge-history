package com.usesoft.poker.client.inject;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.usesoft.poker.client.ApplicationContext;
import com.usesoft.poker.client.view.rootlayout.RootLayoutView;

@GinModules(MyModule.class)
public interface MyInjector extends Ginjector
{
    RootLayoutView getRootLayout();

    ApplicationContext getApplicationContext();
}
