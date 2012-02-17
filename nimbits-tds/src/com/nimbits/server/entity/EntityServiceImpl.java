package com.nimbits.server.entity;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.server.orm.entity.EntityStore;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.subscription.*;
import com.nimbits.server.user.UserServiceFactory;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 2/7/12
 * Time: 12:05 PM
 */
public class EntityServiceImpl  extends RemoteServiceServlet implements EntityService {


    @Override
    public Entity addUpdateEntity(EntityName name, EntityType type) {
        User u = getUser();
        Entity e = EntityModelFactory.createEntity(name, "", EntityType.point, ProtectionLevel.everyone,
                UUID.randomUUID().toString(), u.getUuid(), u.getUuid());
        Entity r = EntityServiceFactory.getDaoInstance(u).addUpdateEntity(e);
        switch (type) {
            case point:
                PointServiceFactory.getInstance().addPoint(u, r);


        }

        return r;
    }

    @Override
    public Entity getEntityByName(User user, EntityName name) {
       return EntityServiceFactory.getDaoInstance(user).getEntityByName(name);
    }

    @Override
    public void deleteEntity(User user, Entity entity) {
         EntityServiceFactory.getDaoInstance(user).deleteEntity(entity);
    }

    @Override
    public List<Entity> getEntityChildren(User user, Entity c, EntityType type) {
        return EntityServiceFactory.getDaoInstance(user).getEntityChildren(c, type);
    }

    private HttpSession getSession() {
             // Get the current request and then return its session
             return this.getThreadLocalRequest().getSession();
    }


    private User getUser() {
        try {
            return UserServiceFactory.getServerInstance().getHttpRequestUser(
                     this.getThreadLocalRequest());
        } catch (NimbitsException e) {
           return null;
        }
    }


    @Override
    public List<Entity> getEntities() {

         return EntityServiceFactory.getDaoInstance(getUser()).getEntities();
    }

    @Override
    public Entity addUpdateEntity(Entity entity) {
        User u = getUser();
        if (entity.getOwner() == null) {
            entity.setOwner(u.getUuid());
        }
        if (entity.getParent() == null) {
            entity.setParent(u.getUuid());
        }
        if (entity.getEntity() == null) {
            entity.setEntity(UUID.randomUUID().toString());
        }
        return EntityServiceFactory.getDaoInstance(u).addUpdateEntity(entity);
    }

    @Override
    public void deleteEntity(Entity entity) {
        EntityServiceFactory.getDaoInstance(getUser()).deleteEntity(entity);
    }

    @Override
    public Entity getEntityByUUID(String uuid) {
       return EntityServiceFactory.getDaoInstance(getUser()).getEntityByUUID(uuid);
    }

    @Override
    public Map<String, Entity> getEntityMap(EntityType type) {
       return EntityServiceFactory.getDaoInstance(getUser()).getEntityMap(type);
    }

    @Override
    public Map<EntityName, Entity> getEntityNameMap(EntityType type) {
        return EntityServiceFactory.getDaoInstance(getUser()).getEntityNameMap(type);
    }



    @Override
    public Entity copyEntity(Entity originalEntity, EntityName newName) {
        Entity newEntity = new EntityStore(originalEntity);
        newEntity.setEntity(UUID.randomUUID().toString());
        switch (newEntity.getEntityType()) {

            case user:
                 return null;
            case point:
                return PointServiceFactory.getInstance().copyPoint(getUser(), originalEntity, newName);
            case category:
                return null;
            case file:
                return null;
            case subscription:
                return null;
            default:
                return null;
        }
    }

    @Override
    public List<Entity> getChildren(Entity parentEntity, EntityType type) {
        return EntityServiceFactory.getDaoInstance(getUser()).getEntityChildren(parentEntity, type);
    }


    @Override
    public List<Entity> getEntityChildren(Entity parentEntity, EntityType type) {
        return EntityServiceFactory.getDaoInstance(getUser()).getEntityChildren(parentEntity, type);
    }

    @Override
    public Entity getEntityByName(EntityName name) {
       return EntityServiceFactory.getDaoInstance(getUser()).getEntityByName(name);
    }

    @Override
    public Entity addUpdateEntity(User user, Entity entity) {
        return EntityServiceFactory.getDaoInstance(user).addUpdateEntity(entity);
    }

    @Override
    public Entity getEntityByUUID(User user, String entityId) {
        return EntityServiceFactory.getDaoInstance(user).getEntityByUUID(entityId);
    }


}