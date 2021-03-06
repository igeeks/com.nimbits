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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.user;


import com.nimbits.client.enums.AuthLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.accesskey.AccessKey;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


//import com.google.appengine.api.users.User;


public class UserModel extends EntityModel implements Serializable, User {

    private Date lastLoggedIn;

    private String emailAddress;

    private String facebookToken;

    private String twitterToken;

    private String twitterTokenSecret;

    private long facebookID;

    private List<AccessKey> accessKeys;

    private boolean loggedIn = false;

    private String loginUrl;

    private String logoutUrl;

    private boolean userAdmin;

    private boolean billingEnabled;

    private int apiCount;

    /**
     *
     */
    private static final long serialVersionUID =2L;

    @SuppressWarnings("unused")
    public UserModel() {
        super();
    }

    public UserModel(final User u) throws NimbitsException {
        super(u);
        if (u != null) {
            this.lastLoggedIn = u.getLastLoggedIn();
            this.accessKeys = u.getAccessKeys();
            this.emailAddress = u.getEmail().getValue();
            this.facebookToken = u.getFacebookToken();
            this.twitterToken = u.getTwitterToken();
            this.twitterTokenSecret = u.getTwitterTokenSecret();
            this.facebookID = u.getFacebookID();
            this.billingEnabled = u.isBillingEnabled();

        }
        else {

        }
    }

    public UserModel(final Entity entity) throws NimbitsException {
        super(entity);
        this.lastLoggedIn =  new Date();
        this.emailAddress = entity.getName().getValue();
        this.billingEnabled = false;

    }



    @Override
    public Date getLastLoggedIn() {
        return (Date) this.lastLoggedIn.clone();
    }

    @Override
    public void setLastLoggedIn(final Date lastLoggedIn) {
        this.lastLoggedIn = new Date(lastLoggedIn.getTime());
    }


    @Override
    public String getTwitterTokenSecret() {
        return twitterTokenSecret;
    }

    @Override
    public void setTwitterTokenSecret(final String twitterTokenSecret) {
        this.twitterTokenSecret = twitterTokenSecret;
    }

    @Override
    public String getTwitterToken() {
        return twitterToken;
    }

    @Override
    public void setTwitterToken(final String twitterToken) {
        this.twitterToken = twitterToken;
    }

    @Override
    public String getFacebookToken() {
        return facebookToken;
    }

    @Override
    public void setFacebookToken(final String facebookToken) {
        this.facebookToken = facebookToken;
    }

    @Override
    public long getFacebookID() {
        return facebookID;
    }

    @Override
    public void setFacebookID(final long facebookID) {
        this.facebookID = facebookID;
    }

    @Override
    public boolean isRestricted() {
        if (accessKeys == null) {
            return true;
        }
        for (final AccessKey key : accessKeys) {
            if (key.getAuthLevel().getCode() > (AuthLevel.restricted.getCode()))
            {
                return false;
            }
        }
        return true;

    }

    @Override
    public List<AccessKey> getAccessKeys() {
        return this.accessKeys == null ? new ArrayList<AccessKey>(1) : this.accessKeys;
    }

    @Override
    public void addAccessKey(final AccessKey key) {
        if (accessKeys == null) {
            accessKeys = new ArrayList<AccessKey>(1);
        }
        accessKeys.add(key);
    }
    @Override
    public boolean isLoggedIn() {
        return loggedIn;
    }
    @Override
    public void setLoggedIn(final boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    @Override
    public String getLoginUrl() {
        return loginUrl;
    }
    @Override
    public void setLoginUrl(final String loginUrl) {
        this.loginUrl = loginUrl;
    }
    @Override
    public String getLogoutUrl() {
        return logoutUrl;
    }
    @Override
    public void setLogoutUrl(final String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }
    @Override
    public boolean isUserAdmin() {
        return userAdmin;
    }
    @Override
    public void setUserAdmin(final boolean userAdmin) {
        this.userAdmin = userAdmin;
    }

    @Override
    public EmailAddress getEmail() throws NimbitsException {
        return CommonFactory.createEmailAddress(emailAddress);
    }
    @Override
    public boolean isBillingEnabled() {
        return billingEnabled;
    }
    @Override
    public void setBillingEnabled(boolean billingEnabled) {
        this.billingEnabled = billingEnabled;
    }

    @Override
    public int getApiCount() {
        return apiCount;
    }

    @Override
    public void setApiCount(int apiCount) {
        this.apiCount = apiCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UserModel userModel = (UserModel) o;

        if (apiCount != userModel.apiCount) return false;
        if (billingEnabled != userModel.billingEnabled) return false;
        if (facebookID != userModel.facebookID) return false;
        if (loggedIn != userModel.loggedIn) return false;
        if (userAdmin != userModel.userAdmin) return false;
        if (accessKeys != null ? !accessKeys.equals(userModel.accessKeys) : userModel.accessKeys != null) return false;
        if (!emailAddress.equals(userModel.emailAddress)) return false;
        if (facebookToken != null ? !facebookToken.equals(userModel.facebookToken) : userModel.facebookToken != null)
            return false;
        if (lastLoggedIn != null ? !lastLoggedIn.equals(userModel.lastLoggedIn) : userModel.lastLoggedIn != null)
            return false;
        if (loginUrl != null ? !loginUrl.equals(userModel.loginUrl) : userModel.loginUrl != null) return false;
        if (logoutUrl != null ? !logoutUrl.equals(userModel.logoutUrl) : userModel.logoutUrl != null) return false;
        if (twitterToken != null ? !twitterToken.equals(userModel.twitterToken) : userModel.twitterToken != null)
            return false;
        if (twitterTokenSecret != null ? !twitterTokenSecret.equals(userModel.twitterTokenSecret) : userModel.twitterTokenSecret != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (lastLoggedIn != null ? lastLoggedIn.hashCode() : 0);
        result = 31 * result + emailAddress.hashCode();
        result = 31 * result + (facebookToken != null ? facebookToken.hashCode() : 0);
        result = 31 * result + (twitterToken != null ? twitterToken.hashCode() : 0);
        result = 31 * result + (twitterTokenSecret != null ? twitterTokenSecret.hashCode() : 0);
        result = 31 * result + (int) (facebookID ^ (facebookID >>> 32));
        result = 31 * result + (accessKeys != null ? accessKeys.hashCode() : 0);
        result = 31 * result + (loggedIn ? 1 : 0);
        result = 31 * result + (loginUrl != null ? loginUrl.hashCode() : 0);
        result = 31 * result + (logoutUrl != null ? logoutUrl.hashCode() : 0);
        result = 31 * result + (userAdmin ? 1 : 0);
        result = 31 * result + (billingEnabled ? 1 : 0);
        result = 31 * result + apiCount;
        return result;
    }
}
