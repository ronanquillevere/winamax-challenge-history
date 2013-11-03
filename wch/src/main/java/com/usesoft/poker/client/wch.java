package com.usesoft.poker.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.usesoft.poker.client.inject.MyInjector;

public class wch implements EntryPoint {

    @Override
    public void onModuleLoad() {

        MyInjector injector = GWT.create(MyInjector.class);

        injector.getApplicationContext();

        RootLayoutPanel.get().add(injector.getRootLayout());
    }
}
