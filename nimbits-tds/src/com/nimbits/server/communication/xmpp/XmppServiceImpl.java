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

package com.nimbits.server.communication.xmpp;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.xmpp.XmppResource;
import com.nimbits.client.model.xmpp.XmppResourceFactory;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.xmpp.XMPPService;
import com.nimbits.server.transactions.service.user.UserServerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service("xmppService")
@Transactional
public class XmppServiceImpl extends RemoteServiceServlet implements XMPPService {

   // private static final Logger log = Logger.getLogger(XmppServiceImpl.class.getName());

    private static final long serialVersionUID = 1L;
    private EntityService entityService;
    private UserServerService userService;

    private User getUser() {
        try {
            return userService.getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            return null;
        }

    }
    @Override
    public void sendMessage(final String msgBody, final EmailAddress email) {


        final JID jid = new JID(email.getValue());


        send(msgBody, jid);


    }

    private static void send(final String msgBody, final JID jid) {
        final Message msg = new MessageBuilder()
                .withRecipientJids(jid)
                .withBody(msgBody)
                .build();
        final com.google.appengine.api.xmpp.XMPPService xmpp = XMPPServiceFactory.getXMPPService();

        xmpp.sendMessage(msg);
    }

    @Override
    public void sendMessage(final User user, final List<XmppResource> resources, final String message, final EmailAddress email) throws NimbitsException {
        for (XmppResource resource : resources) {
            List<Entity> entity = entityService.getEntityByKey(user, resource.getKey(), EntityType.point);
            if (! entity.isEmpty()) {
                JID jid = new JID(email.getValue() + '/' + entity.get(0).getName().getValue());
                send(message, jid);
               // log.info("stanza sent with jid: " + email.getValue() + "/" + entity.getName().getValue());
            }
        }
    }

    @Override
    public void deleteResource(final User u, final Entity entity) {
      XmppTransactionFactory.getInstance().deleteResource(entity);
    }

    @Override
    public List<XmppResource> getPointXmppResources(final User user, final Point point) throws NimbitsException {
        return XmppTransactionFactory.getInstance().getPointXmppResources(point);
    }

    @Override
    public Entity createXmppResource(final Entity targetPointEntity, final EntityName resourceName) throws NimbitsException {
        User u = getUser();
        if (u != null) {

            Entity entity = EntityModelFactory.createEntity(resourceName, "", EntityType.resource, ProtectionLevel.onlyMe,
                    targetPointEntity.getKey(),getUser().getKey() , UUID.randomUUID().toString());

            XmppResource resource = XmppResourceFactory.createXmppResource(entity, targetPointEntity.getKey());
           entityService.addUpdateEntity(u, resource);
            return resource;
        }
        else {
            return null;
        }
    }



    @Override
    public void sendInvite() throws NimbitsException {

        final User u = userService.getHttpRequestUser(
                this.getThreadLocalRequest());

        final JID jid = new JID(u.getEmail().getValue());
        final com.google.appengine.api.xmpp.XMPPService xmpp = XMPPServiceFactory.getXMPPService();
        xmpp.sendInvitation(jid);


    }

    public void setEntityService(final EntityService entityService) {
        this.entityService = entityService;
    }

    public EntityService  getEntityService() {
        return entityService;
    }

    public void setUserService(UserServerService  userService) {
        this.userService = userService;
    }

    public UserServerService getUserService() {
        return userService;
    }
}
