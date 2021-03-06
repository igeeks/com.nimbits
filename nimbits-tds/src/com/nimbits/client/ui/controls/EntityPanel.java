/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.ui.controls;

import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.model.entity.Entity;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/20/12
 * Time: 1:10 PM
 */
public class EntityPanel extends FormPanel {

    private final ProtectionLevelOptions protectionOptions;


    public EntityPanel(Entity entity) {

        setEncoding(FormPanel.Encoding.MULTIPART);
        setHeaderVisible(false);
        setFrame(false);

        final TextArea description = new TextArea();
        description.setFieldLabel("Description");
        description.setName(Parameters.description.getText());
        add(description);

        protectionOptions = new ProtectionLevelOptions(entity);

        add(protectionOptions);
       // protectionOptions.addHandler(Events.OnClick, ClickEvent)
        final HiddenField<ProtectionLevel> protectionLevelHiddenField = new HiddenField<ProtectionLevel>();
        protectionLevelHiddenField.setName(Parameters.protection.getText());
        add(protectionLevelHiddenField);
        protectionLevelHiddenField.setValue(protectionOptions.getProtectionLevel());
    }


    public ProtectionLevel getProtectionLevel() {
        return protectionOptions.getProtectionLevel();
    }


}
