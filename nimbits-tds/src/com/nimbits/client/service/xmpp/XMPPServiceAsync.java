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

package com.nimbits.client.service.xmpp;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.xmpp.XmppResource;

import java.util.List;

public interface XMPPServiceAsync {
    void sendInvite(AsyncCallback<Void> asyncCallback);

    void sendMessage(final String msgBody, final EmailAddress email, final AsyncCallback<Void> callback);

    void createXmppResource(final Entity targetPointEntity, final EntityName resourceName, final AsyncCallback<Entity> async);

    void getPointXmppResources(final User user, final Point point, final AsyncCallback<List<XmppResource>> async) throws NimbitsException;

    void sendMessage(final User user, final List<XmppResource> resources, final String message, final EmailAddress email, final AsyncCallback<Void> async) throws NimbitsException;

    void deleteResource(final User u, final Entity entity, final AsyncCallback<Void> async);
}
