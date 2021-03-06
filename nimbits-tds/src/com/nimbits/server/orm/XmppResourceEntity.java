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

package com.nimbits.server.orm;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.xmpp.XmppResource;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/15/12
 * Time: 12:36 PM
 */
@SuppressWarnings("unused")
@PersistenceCapable
public class XmppResourceEntity extends EntityStore implements XmppResource {


    @Persistent
    private String entity;

    protected XmppResourceEntity() {
    }

    public XmppResourceEntity(final XmppResource resource) throws NimbitsException {
        super(resource);
        this.entity = resource.getEntity();
    }

    @Override
    public String getEntity() {
        return entity;
    }

    @Override
    public void update(Entity update) throws NimbitsException {
        super.update(update);
        XmppResource resource = (XmppResource) update;
        this.entity = resource.getEntity();
    }
}
