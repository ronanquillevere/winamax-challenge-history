package com.usesoft.poker.client.view.rootlayout;

import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class RootLayoutView extends Composite
{
    @UiTemplate("RootLayout.ui.xml")
    interface Binder extends UiBinder<Widget, RootLayoutView>
    {
    }

    @Inject
    public RootLayoutView(Binder binder)
    {
        initWidget(binder.createAndBindUi(this));
    }

    @UiField
    LayoutPanel northPanel;
    @UiField
    LayoutPanel southPanel;
    @UiField
    LayoutPanel eastPanel;
    @UiField
    LayoutPanel westPanel;
    @UiField
    LayoutPanel topPanel;
    @UiField
    LayoutPanel centerPanel;
    @UiField
    LayoutPanel bottomPanel;

}
