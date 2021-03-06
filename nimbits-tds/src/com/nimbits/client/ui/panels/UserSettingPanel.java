/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.BoxLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.user.UserService;
import com.nimbits.client.service.user.UserServiceAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:29 PM
 */
public class UserSettingPanel extends NavigationEventProvider {
    private NumberField balance;
    private CheckBox enabled;
    private static final int WIDTH = 350;

    private FormData formdata;
    private VerticalPanel vp;
    private final Map<SettingType, String> settings;

    private User user;
    public UserSettingPanel(final User user,  final Map<SettingType, String> settings) {

        this.settings = settings;
        this.user = user;


    }

    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);
        // setLayout(new FillLayout());
        formdata = new FormData("-20");
        vp = new VerticalPanel();
        vp.setSpacing(10);
        vp.setWidth(WIDTH);


        try {
            createForm();
            add(vp);
            doLayout();
        } catch (NimbitsException e) {
            FeedbackHelper.showError(e);
        }






    }







    private void createForm() throws NimbitsException {

        FormPanel simple = new FormPanel();
        simple.setWidth(WIDTH);
        simple.setFrame(true);
        simple.setHeaderVisible(false);
        simple.setBodyBorder(false);
        simple.setFrame(false);

        final NumberField maxQuota = new NumberField();
        maxQuota.setFieldLabel("Maximum Daily Budget");
        maxQuota.setAllowBlank(false);
        maxQuota.setMinValue(0.0);
        maxQuota.setAllowDecimals(true);

        maxQuota.setFormat(NumberFormat.getFormat("##.00"));

        balance = new NumberField();
        balance.setFieldLabel("Account Balance");
        balance.setReadOnly(true);
        balance.setAllowBlank(false);


        balance.setFormat(NumberFormat.getFormat("##.##"));

        enabled = new CheckBox();




        enabled.setBoxLabel("Enable Billing");
        enabled.setLabelSeparator("");


        Button coupon = new Button("Use Coupon");


        Button submit = new Button("Submit");
        Button cancel = new Button("Cancel");
        cancel.addSelectionListener(new CancelButtonEventSelectionListener());

        submit.addSelectionListener(new SubmitButtonEventSelectionListener(enabled, maxQuota));

        coupon.addSelectionListener(new CouponButtonEventSelectionListener());


        Html h = new Html("<h5>Billing and Quotas</h5>" +
                "<p>Nimbits provides a free quota of 1000 api calls a day. " +
                " If you exceed the free quota, you can use pre-purchased credits to ensure your " +
                "data is processed.</p>" +
                "<p>Credits can be purchased at <a href=\"http://www.nimbits.com\">nimbits.com.</a> </p>" +
                "<p> While subject to change per " +
                "our terms of use, the current rate for api calls is $0.01 per 1000 api calls over the free quota.</p>" +
                "<p>You can specify a maximum daily budget to allow your account to be charged.</p>");

        enabled.setBoxLabel("Enabled Billing");
        enabled.setLabelSeparator("");


      //  maxQuota.setValue(user.getBilling().getMaxDailyAllowance());




        vp.add(h);
        simple.add(balance, formdata);
        simple.add(maxQuota, formdata);

        simple.add(enabled, formdata);


        LayoutContainer c = new LayoutContainer();
        HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(5));
        layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        layout.setPack(BoxLayout.BoxLayoutPack.END);
        c.setLayout(layout);
        cancel.setWidth(100);
        submit.setWidth(100);
        HBoxLayoutData layoutData = new HBoxLayoutData(new Margins(0, 5, 0, 0));
        c.add(coupon, layoutData);
        c.add(cancel, layoutData);
        c.add(submit, layoutData);
        vp.add(simple);
        vp.add(c);


       UserServiceAsync service = GWT.create(UserService.class);
        service.getAccountBalance(new AsyncCallback<List<Point>>() {
            @Override
            public void onFailure(Throwable caught) {
                FeedbackHelper.showError(caught);
            }

            @Override
            public void onSuccess(List<Point> result) {
              if (! result.isEmpty()) {
                  Point point = result.get(0);
                  Value value = point.getValue();
                  balance.setValue(value.getDoubleValue());
                  maxQuota.setValue(point.getDeltaAlarm());
                  enabled.setValue(point.isDeltaAlarmOn());

              }
            }
        });
//        EntityServiceAsync entityService = GWT.create(EntityService.class);
//        Point point = entityService.getE
//        service.getCurrentValue();


    }







    private class CancelButtonEventSelectionListener extends SelectionListener<ButtonEvent> {


        CancelButtonEventSelectionListener() {
        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {
            try {
                notifyEntityAddedListener(null);
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
        }
    }

    private class CouponButtonEventSelectionListener extends SelectionListener<ButtonEvent> {


        CouponButtonEventSelectionListener() {
        }

        @Override
        public void componentSelected(ButtonEvent buttonEvent) {
            final MessageBox box = MessageBox.prompt(
                    "Fund your account with a coupon code",
                    "Please enter the coupon code");

            box.addCallback(new NewCouponMessageBoxEventListener());
        }
    }
    private class NewCouponMessageBoxEventListener implements Listener<MessageBoxEvent> {

        private String value;

        NewCouponMessageBoxEventListener() {
        }

        @Override
        public void handleEvent(MessageBoxEvent be) {
            value = be.getValue();
            if (!Utils.isEmptyString(value)) {
                final MessageBox box = MessageBox.wait("Progress",
                        "Validating your code and funding your account", " Validating " + value) ;
                box.show();
                UserServiceAsync service = GWT.create(UserService.class);



                    service.processCoupon(value, new AsyncCallback<Double>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            box.close();
                          FeedbackHelper.showError(caught);
                        }

                        @Override
                        public void onSuccess(Double result) {
                          box.close();
                            final MessageBox s = MessageBox.info("Success",
                                    "Your Coupon code was accepted. You can set a daily budget using the settings menu", null) ;
                            s.show();
                            enabled.setValue(true);
                            balance.setValue(result);

                        }
                    });


            }
        }
    }

    private class SubmitButtonEventSelectionListener extends SelectionListener<ButtonEvent> {
        CheckBox enabled;
        NumberField maxQuota;

        public SubmitButtonEventSelectionListener(CheckBox enabled, NumberField maxQuota) {
            this.enabled = enabled;
            this.maxQuota = maxQuota;
        }

        @Override
        public void componentSelected(ButtonEvent ce) {

            final MessageBox box = MessageBox.wait("Progress",
                    "Saving your settings, please wait...", "Saving...");
            box.show();

           UserServiceAsync service = GWT.create(UserService.class);
            service.updateBilling(user, this.enabled.getValue(), this.maxQuota.getValue().doubleValue(), new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                  box.close();
                  FeedbackHelper.showError(caught);
                }

                @Override
                public void onSuccess(Void result) {
                  box.close();
                }
            });

        }
    }
}
