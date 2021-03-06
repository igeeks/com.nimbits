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

package com.nimbits.client.service.user;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.enums.AuthLevel;
import com.nimbits.client.model.connection.ConnectionRequest;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;

import java.util.List;

public interface UserServiceAsync {


    void sendConnectionRequest(final EmailAddress email, final AsyncCallback<Void> asyncCallback);

    void getPendingConnectionRequests(final EmailAddress email, final AsyncCallback<List<ConnectionRequest>> asyncCallback);

    void connectionRequestReply(final EmailAddress targetEmail, final EmailAddress requestorEmail, final Long key, final boolean accepted, final AsyncCallback<Void> asyncCallback);

    void getAppUserUsingGoogleAuth(final AsyncCallback<User> async);

    void getUserByKey(final String key, AuthLevel authLevel, final AsyncCallback<User> async);

    void getConnectionRequests(final List<String> connections, final AsyncCallback<List<User>> async);

    void login(final String requestUri, final AsyncCallback<User> async);

    void getQuota(final AsyncCallback<Integer> async);

    void getAccountBalance(final AsyncCallback<List<Point>> async);

    void updateBilling(final User user, final boolean billingEnabled, final double maxQuota, final AsyncCallback<Void> async);

    void getAllUsers(final String s, final int count, final AsyncCallback<List<User>> async);

    void processCoupon(String value, AsyncCallback<Double> async);
}
