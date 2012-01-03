/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.controls;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.ClientType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.GxtPointModel;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.service.datapoints.PointService;
import com.nimbits.client.service.datapoints.PointServiceAsync;

import java.util.List;

public class PointCombo extends ComboBox<GxtPointModel> {

    public String getText() {
        List<GxtPointModel> s = getSelection();
        if (s.size() > 0) {
            GxtPointModel si = s.get(0);
            return si.getName().getValue();
        } else {
            return null;

        }

    }

    public GxtPointModel getPoint() {
        List<GxtPointModel> s = getSelection();
        return s.get(0);

    }

    public PointCombo() {
        setEmptyText(Const.MESSAGE_LOADING_POINTS);
        final ListStore<GxtPointModel> cbStore = new ListStore<GxtPointModel>();
        setStore(cbStore);
        setDisplayField(Const.PARAM_NAME);
        setValueField(Const.PARAM_ID);
        setEditable(false);

        PointServiceAsync pointService = GWT.create(PointService.class);

        try {
            pointService.getPoints(new AsyncCallback<List<Point>>() {

                @Override
                public void onFailure(Throwable caught) {

                    GWT.log(caught.getMessage());
                }

                @Override
                public void onSuccess(List<Point> result) {

                    setEmptyText(Const.MESSAGE_SELECT_POINT);

                    for (Point p : result) {
                        GxtPointModel model = new GxtPointModel(p, ClientType.other);
                        cbStore.add(model);

                    }

                }

            });
        } catch (NimbitsException e) {
            GWT.log(e.getMessage());
        }

    }
}
