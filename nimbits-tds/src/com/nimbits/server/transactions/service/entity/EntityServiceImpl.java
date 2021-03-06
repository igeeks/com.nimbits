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

package com.nimbits.server.transactions.service.entity;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.AuthLevel;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.location.Location;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.server.admin.common.ServerInfo;
import com.nimbits.server.api.ApiServlet;
import com.nimbits.server.api.helper.LocationServiceImpl;
import com.nimbits.server.process.task.TaskImpl;
import com.nimbits.server.transactions.service.user.UserServerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 12:05 PM
 */
@SuppressWarnings("unchecked")
@Transactional
@Service("entityService")
public class EntityServiceImpl  extends RemoteServiceServlet implements EntityService {

    private static final long serialVersionUID = -6442025194172745189L;
    private UserServerService userService;
    private ValueService valueService;
    private EntityTransactions entityCache;
    private TaskImpl taskFactory;
    private LocationServiceImpl locationService;
    private ServerInfo serverInfoService;

    private User getUser()  {

        try {
            return userService.getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            return userService.getAnonUser();
        }

    }



    @Override
    public List<Entity> deleteEntity(final User user, final Entity entity) throws NimbitsException {
        final List<Entity> deleted;
        try {
            deleted = entityCache.deleteEntity(user, entity, Class.forName(entity.getEntityType().getClassName()));
        } catch (ClassNotFoundException e) {
            throw  new NimbitsException(e);
        }
        entityCache.removeEntityFromCache(user, deleted);
        for (final Entity e : deleted) {

            taskFactory.startCoreTask(getThreadLocalRequest(), e, Action.delete, serverInfoService.getFullServerURL(getThreadLocalRequest()));

        }


        if (entity.getEntityType().equals(EntityType.point)) {
            valueService.purgeValues(entity);

        }
        return deleted;
    }

//    @Override
//    public List<Entity> getEntityChildren(final User user, final Entity entity,final  EntityType type) throws NimbitsException {
//        return entityCache.getChildren(user, entity, type);
//    }


    @Override
    public List<Entity> getEntities(final User user) throws NimbitsException {
        final List<Entity> retVal =entityCache.getEntities(user);
        Collections.sort(retVal);
        return retVal;
    }


    @Override
    public Entity addUpdateEntity(final Entity entity) throws NimbitsException {
        final User u = entity.getEntityType().equals(EntityType.user) ? (User) entity : getUser();
        if (Utils.isEmptyString(entity.getOwner())) {
            entity.setOwner(u.getKey());
        }
        if (Utils.isEmptyString(entity.getParent())) {
            entity.setParent(u.getKey());
        }
        if (Utils.isEmptyString(entity.getUUID())) {
            entity.setUUID(UUID.randomUUID().toString());
        }



        return addUpdateEntity(u, entity);

    }

    @Override
    public List<Entity> deleteEntity(final Entity entity) throws NimbitsException {
        User u = getUser();
        if (u == null)  {
            u = userService.getUserByKey(entity.getOwner(), AuthLevel.admin);
        }
        return  deleteEntity(u, entity);
    }


    @Override
    public List<Entity> findEntityByKey(final User user, final String key) throws NimbitsException {


        for (final EntityType t : EntityType.values()) {
            final List<Entity> r = getEntityByKey(user, key, t);
            if (! r.isEmpty()) {
                return r;

            }
        }
        return new ArrayList<Entity>(0);


    }


    @Override
    public Map<String, Entity> getEntityMap(final User user, final EntityType type, final int limit) throws NimbitsException {
        return entityCache.getEntityMap(user, type, limit);
    }

    @Override
    public Map<EntityName, Entity> getEntityNameMap(final EntityType type) throws NimbitsException {
        return entityCache.getEntityNameMap(getUser(), type);
    }

    @Override
    public Entity copyEntity(final Entity originalEntity, final EntityName newName) throws NimbitsException {

        switch (originalEntity.getEntityType()) {

            case user:
                return null;
            case point:
                Point p = PointModelFactory.createPointModel((Point)originalEntity);
                p.setName(newName);
                p.setKey(null);
                return addUpdateEntity(p);
            //return PointServiceFactory.getInstance().copyPoint(getUser(), originalEntity, newName);

            case category:
                return null;

            case subscription:
                return null;
            default:
                return null;
        }
    }

//    @Override
//    public List<Entity> getChildren(final Entity parentEntity, final EntityType type) throws NimbitsException {
//        return entityCache.getChildren(getUser(), parentEntity, type);
//    }

    @Override
    public List<Entity> getChildren(final User user, final  List<Entity> parentEntity) throws NimbitsException {
        return entityCache.getChildren(user, parentEntity);
    }

    @Override
    public void updateUser(User user) throws NimbitsException {
        entityCache.updateUser(user);
    }

    @Override
    public List<Entity>  getEntityByName(final User user, final EntityName name, final EntityType type) throws NimbitsException {
        try {
            return entityCache.getEntityByName(user, name, Class.forName(type.getClassName()));
        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }
    }

    @Override
    public List<Entity> getEntityByTrigger(User user, Entity trigger, EntityType type) throws NimbitsException {
        try {
            final Class cls = Class.forName(type.getClassName());
            return entityCache.getEntityByTrigger(user, trigger, cls);
        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }

    }

    @Override
    public Map<String, Entity> getSystemWideEntityMap(final User user, final EntityType type) throws NimbitsException {
        return entityCache.getSystemWideEntityMap(user, type);
    }

    @Override
    public List<Entity> getIdleEntities() throws NimbitsException {
        return entityCache.getIdleEntities(userService.getAdmin());
    }

    @Override
    public List<Entity> getSubscriptionsToEntity(final User user, final Entity subscribedEntity) throws NimbitsException {
        return entityCache.getSubscriptionsToEntity(user, subscribedEntity);

    }

    @Override
    public Entity addUpdateEntity(final User user, final Entity entity) throws NimbitsException {
        if (! entity.getEntityType().equals(EntityType.user)) {
            if (StringUtils.isEmpty(entity.getOwner())) {
                throw new NimbitsException("Owner must not be null");
            }
            if (! entity.getOwner().equals(user.getKey())) {
                throw new NimbitsException("You can't create an entity with an owner other than yourself!");
            }
        }


            taskFactory.startCoreTask(this.getThreadLocalRequest(), entity, Action.update, serverInfoService.getFullServerURL(getThreadLocalRequest()));
            Location location =   ApiServlet.getGPS(this.getThreadLocalRequest());
            locationService.reportLocation(entity, location);



        return entityCache.addUpdateEntity(user, entity, true);

    }

    @Override
    public List<Entity>  getEntityByKey(final User user, final String entityId, final EntityType type) throws NimbitsException {
        try {
            if (user != null) {
                return entityCache.getEntityByKey(user, entityId, Class.forName(type.getClassName()));
            }
            else {

                return entityCache.getEntityByKey(getUser(), entityId, Class.forName(type.getClassName()));
            }

        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }
    }

    public void setUserService(UserServerService userService) {
        this.userService = userService;
    }

    public UserServerService getUserService() {
        return userService;
    }

    public void setValueService(ValueService valueService) {
        this.valueService = valueService;
    }

    public ValueService getValueService() {
        return valueService;
    }

    public void setEntityCache(EntityTransactions entityCache) {
        this.entityCache = entityCache;
    }

    public EntityTransactions getEntityCache() {
        return entityCache;
    }

    public void setTaskFactory(TaskImpl taskFactory) {
        this.taskFactory = taskFactory;
    }

    public TaskImpl getTaskFactory() {
        return taskFactory;
    }

    public void setLocationService(LocationServiceImpl locationService) {
        this.locationService = locationService;
    }

    public LocationServiceImpl getLocationService() {
        return locationService;
    }

    public void setServerInfoService(ServerInfo serverInfoService) {
        this.serverInfoService = serverInfoService;
    }

    public ServerInfo getServerInfoService() {
        return serverInfoService;
    }
}
