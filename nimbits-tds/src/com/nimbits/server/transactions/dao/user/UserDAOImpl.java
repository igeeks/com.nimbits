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

package com.nimbits.server.transactions.dao.user;

import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.connection.ConnectionRequest;
import com.nimbits.client.model.connection.ConnectionRequestModelFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModelFactory;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.server.orm.ConnectionRequestEntity;
import com.nimbits.server.orm.UserEntity;
import com.nimbits.server.transactions.service.user.UserServerService;
import com.nimbits.server.transactions.service.user.UserTransactions;
import org.springframework.stereotype.Repository;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
@Repository("userDao")
public class UserDAOImpl implements UserTransactions {
    private static final Logger log = Logger.getLogger(UserDAOImpl.class.getName());
    private static final int MAX_REQUESTS = 25;
    public static final String STRING = "approved == a && targetEmail==e && rejected == r";
    public static final String STRING1 = "Boolean a, Boolean r, String e";
    public static final String STRING2 = ":p.contains(uuid)";
    private EntityService entityService;
    private UserServerService userService;
    private PersistenceManagerFactory pmf;


    @Override
    @SuppressWarnings(Const.WARNING_UNCHECKED)
    public List<User> getAllUsers(final String ordering, final int count) {
         final PersistenceManager pm = pmf.getPersistenceManager();
        List<User> retObj = null;
        try {
            final Query q = pm.newQuery(UserEntity.class);
            q.setOrdering(ordering);
            q.setRange(0, count);
            final Collection<User> result = (Collection<User>) q.execute();
            retObj = UserModelFactory.createUserModels(result);
        } catch (Exception e) {
            log.severe(e.getMessage());
        } finally {
            pm.close();
        }
        return retObj;
    }

    @Override
    public List<ConnectionRequest> getPendingConnectionRequests(final EmailAddress internetAddress) throws NimbitsException {
        final PersistenceManager pm = pmf.getPersistenceManager();
        try {
            final Query q = pm.newQuery(ConnectionRequestEntity.class);
            q.setFilter(STRING);
            q.declareParameters(STRING1);
            q.setRange(0, MAX_REQUESTS);


            final List<ConnectionRequestEntity> data = (List<ConnectionRequestEntity>) q.execute(false, false, internetAddress.getValue());

            return  ConnectionRequestModelFactory.CreateConnectionRequestModels(data);

        } finally {
            pm.close();
        }


    }

    @Override
    public void updateConnectionRequest(final Long key, final User requestor, final User acceptor, final boolean accepted) {
        final PersistenceManager pm = pmf.getPersistenceManager();

        try {
            final ConnectionRequestEntity c =  pm.getObjectById(ConnectionRequestEntity.class, key);
            if (c!= null) {
                final Transaction tx = pm.currentTransaction();
                tx.begin();
                c.setRejected(!accepted);
                c.setApproved(accepted);
                c.setApprovedDate(new Date());
                tx.commit();
            }
        } finally {
            pm.close();
        }



    }

    @Override
    public ConnectionRequest makeConnectionRequest(final User u, final EmailAddress emailAddress) throws NimbitsException {
        final ConnectionRequestEntity f = new ConnectionRequestEntity(u.getKey(), u.getEmail(), emailAddress, UUID.randomUUID().toString());

        final PersistenceManager pm = pmf.getPersistenceManager();
        try {
            pm.makePersistent(f);
            return  ConnectionRequestModelFactory.CreateConnectionRequestModel(f);

        } finally {
            pm.close();
        }



    }

    @Override
    public List<User> getConnectionRequests(final List<String> connections) throws NimbitsException {
        final PersistenceManager pm = pmf.getPersistenceManager();
        final Query q = pm.newQuery(ConnectionRequestEntity.class, STRING2);


        try {
            final Collection<ConnectionRequestEntity> result = (Collection<ConnectionRequestEntity>) q.execute(connections);
            final List<User> retObj = new ArrayList<User>(result.size());
            if (!result.isEmpty()) {

                for (final ConnectionRequest c : result) {
                    User u = (User) entityService.getEntityByKey(userService.getAdmin(), c.getTargetEmail().getValue(), EntityType.user);
                    retObj.add( UserModelFactory.createUserModel(u));

                }


            }
            return retObj;
        } finally {
            pm.close();
        }

    }

    @Override
    public List<User> getCachedAuthenticatedUser(String cacheKey) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }

    @Override
    public void cacheAuthenticatedUser(String cacheKey, User user) throws NimbitsException {
        throw new NimbitsException("Not Implemented");
    }


    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    public EntityService getEntityService() {
        return entityService;
    }


    public void setUserService(UserServerService userService) {
        this.userService = userService;
    }

    public UserServerService getUserService() {
        return userService;
    }


    public void setPmf(PersistenceManagerFactory pmf) {
        this.pmf = pmf;
    }
}
