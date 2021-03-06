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

package com.nimbits.server.transactions.service.feed;

import com.google.gson.JsonSyntaxException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nimbits.client.common.Utils;
import com.nimbits.client.constants.Const;
import com.nimbits.client.enums.*;
import com.nimbits.client.enums.point.PointType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.feed.FeedValue;
import com.nimbits.client.model.feed.FeedValueModel;
import com.nimbits.client.model.location.LocationFactory;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.impl.ValueFactory;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.feed.FeedService;
import com.nimbits.client.service.feed.FeedService;
import com.nimbits.client.service.value.ValueService;
import com.nimbits.server.admin.common.ServerInfo;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transactions.service.user.UserServerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.logging.Logger;


/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/24/12
 * Time: 2:02 PM
 */
@Service("feedService")
@Transactional
public class FeedServiceImpl extends RemoteServiceServlet implements FeedService {

    private static final int MAX_LENGTH = 1024;
    private static final int SIZE = 1024;
    private static final int LENGTH = 200;
    private static final int OFFSET = 500;
    private static final Logger log = Logger.getLogger(FeedServiceImpl.class.getName());
    private EntityService entityService;
    private UserServerService userService;
    private ValueService valueService;
    private ServerInfo serverInfoService;


    private User getUser() {
        try {
            return userService.getHttpRequestUser(
                    this.getThreadLocalRequest());
        } catch (NimbitsException e) {
            return userService.getAnonUser();
        }
    }

    @Override
    public void postToFeed(final User user, final Entity entity, final Point originalPoint, final Value value, final FeedType type) throws NimbitsException {
        final List<Point> feedPoint = getFeedPoint(user);
        if (! feedPoint.isEmpty()) {
            final FeedValue feedValue = new FeedValueModel(valueToHtml(entity, originalPoint, value), value.getData().getContent(), type);
            final String json = GsonFactory.getSimpleInstance().toJson(feedValue);
            final Value v = ValueFactory.createValueModel(value, json);
            valueService.recordValue(user, feedPoint.get(0), v);
        }
    }

    @Override
    public void postToFeed(final User user, final Throwable ex) {
        LogHelper.logException(this.getClass(), ex);
//        try {
//         //   postToFeed(user, ExceptionUtils.getStackTrace(ex), FeedType.error);
//
//
//        } catch (NimbitsException e) {
//            LogHelper.logException(this.getClass(), e);
//        }

    }

    @Override
    public void postToFeed(final User user, final String message, final FeedType type) throws NimbitsException {
        final List<Point> feedPoint = getFeedPoint(user);

        if (! feedPoint.isEmpty())  {
            final String shortened = message.length() > LENGTH ? message.substring(0, LENGTH) : message;

            final String finalMessage;
            try {
                finalMessage = generatePostToFeedHtml(shortened, message, type);
            } catch (UnsupportedEncodingException e) {
                throw new NimbitsException(e);
            }

            final FeedValue feedValue = new FeedValueModel(finalMessage, "", type);
            final String json = GsonFactory.getSimpleInstance().toJson(feedValue);
            final Value value = ValueFactory.createValueModel(LocationFactory.createLocation(), Const.CONST_IGNORED_NUMBER_VALUE,
                    new Date(),"", ValueFactory.createValueData(json), AlertType.OK);
            final Value v = ValueFactory.createValueModel(value, json);

            valueService.recordValue(user, feedPoint.get(0), v);

        }

    }

    protected String  generatePostToFeedHtml(final String shortMessage, final String originalMessage, final FeedType type) throws UnsupportedEncodingException {
        final StringBuilder sb = new StringBuilder(MAX_LENGTH + OFFSET) ;
        final String start ="<p style=\"white-space: normal;width:150px\"><img style=\"float:left;\" ";
        switch (type) {

            case error:
                sb.append(start)
                        .append("src=\"")
                        .append(serverInfoService.getFullServerURL(this.getThreadLocalRequest()))
                        .append("/resources/images/symbol-error.png\" width=\"35\" height=\"35\">");

                break;
            case system:
                sb.append(start)
                        .append("src=\"")
                        .append(serverInfoService.getFullServerURL(this.getThreadLocalRequest()))
                        .append("/resources/images/logo.png\"  width=\"40\" height=\"40\">");
                break;
            case info:
                sb.append(start).append("src=\"")
                        .append(serverInfoService.getFullServerURL(this.getThreadLocalRequest()))
                        .append("/resources/images/info.png\" width=\"35\" height=\"35\">");
                break;
            case data:
                sb.append(start).append("src=\"")
                        .append(serverInfoService.getFullServerURL(this.getThreadLocalRequest()))
                        .append("/resources/images/point_ok.png\" width=\"40\" height=\"40\">");
                break;
            default:
                sb.append(start).append("src=\"")
                        .append(serverInfoService.getFullServerURL(this.getThreadLocalRequest()))
                        .append("/resources/images/logo.png\" width=\"40\" height=\"40\">");
        }

       // final String shortenedOriginal = originalMessage.length() > MAX_LENGTH ? originalMessage.substring(0, MAX_LENGTH) : originalMessage;

        sb.append("<a href=\"#\" onclick=\"showFeed('")
                .append(originalMessage)
                .append("');\">")
                .append("<span>")
                .append(new Date())
                .append("</span>")
                .append("<br /></a>")
                .append(shortMessage)
                .append("</p>");
        return sb.toString();
    }



    protected String valueToHtml(final Entity entity, final Entity point, final Value value) {
        final StringBuilder sb = new StringBuilder(SIZE);
        if (! (Double.compare(value.getDoubleValue(), Const.CONST_IGNORED_NUMBER_VALUE) == 0)) {
            sb.append("<img style=\"float:left\" src=\"")
                    .append(serverInfoService.getFullServerURL(this.getThreadLocalRequest()));


            switch (value.getAlertState()) {

                case LowAlert:
                    sb.append("/resources/images/point_low.png\">");
                    break;
                case HighAlert:
                    sb.append("/resources/images/point_high.png\">");
                    break;
                case IdleAlert:
                    sb.append("/resources/images/point_idle.png\">");
                    break;
                case OK:
                    sb.append("/resources/images/point_ok.png\">");
                    break;
            }
        }

        if (entity != null && point != null) {

            sb.append("&nbsp;");

            if (! (Double.compare(value.getDoubleValue(), Const.CONST_IGNORED_NUMBER_VALUE) == 0)) {
                sb.append("Alert&nbsp;Status:")
                        .append(value.getAlertState().name());
                sb.append("<br>Value:")
                        .append(value.getDoubleValue());
            }


            if (! Utils.isEmptyString(value.getNote())) {
                sb.append("<br>Note:").append(value.getNote());
            }


            sb.append("<a href=\"#\" onclick=\"window.open('report.html?uuid=")
                    .append(point.getKey())
                    .append("', 'Report',")
                    .append("'height=800,width=800,toolbar=0,status=0,Location=0' );\" >")
                    .append("&nbsp;[more]</a>");

        }





        return sb.toString();
    }


    @Override
    public List<Point> getFeedPoint(final User user) throws NimbitsException {

        final Map<String, Entity> map =  entityService.getEntityMap(user, EntityType.feed, 1);

      if (map.isEmpty()) {
         return Collections.emptyList();
       }
      else {
           List<Point> retObj = new ArrayList<Point>(map.size());
           for (Entity e : map.values()) {
               retObj.add((Point) e);
           }

           return retObj;

        }

    }

    @Override
    public List<FeedValue> getFeed(final int count, final String relationshipEntityKey) throws NimbitsException {

        final User loggedInUser = getUser();
        final User feedUser = getFeedUser(relationshipEntityKey, loggedInUser);

        if (feedUser != null) {


            final List<Point> feedPoint = getFeedPoint(feedUser);
            if (feedPoint.isEmpty()) {
                return Collections.emptyList();
            }
            else {
                final List<Value> values = valueService.getTopDataSeries(feedPoint.get(0), count, new Date());
                final List<FeedValue> retObj = new ArrayList<FeedValue>(values.size());

                for (final Value v : values) {
                    if (! Utils.isEmptyString(v.getData().getContent())) {
                        try {
                            retObj.add(
                                    GsonFactory.getInstance().fromJson(v.getData().getContent(),
                                            FeedValueModel.class)
                            );
                        } catch (JsonSyntaxException e) {
                            LogHelper.logException(this.getClass(), e);
                        }
                    }
                }
                return retObj;
            }
        }
        else
        {
            return new ArrayList<FeedValue>(0);
        }
    }

    protected static User getFeedUser(final String relationshipEntityKey, final User loggedInUser) throws NimbitsException {

       // if (loggedInUser != null && loggedInUser.getKey().equals(relationshipEntityKey)) {

            return loggedInUser;
//        }
//        else {
//            final Relationship r = RelationshipTransactionFactory.getInstance().getRelationship(relationshipEntityKey);
//
//            if (r != null) {
//                final String feedOwnersUUID = r.getForeignKey();
//                return UserServiceFactory.getInstance().getUserByKey(feedOwnersUUID);
//            }
//            throw new NimbitsException("FeedService User not found");
//        }

    }

    @Override
    public Point createFeedPoint(final User user) throws NimbitsException {

        final EntityName name = CommonFactory.createName(Const.TEXT_DATA_FEED, EntityType.point);
        final Entity entity = EntityModelFactory.createEntity(name, "", EntityType.feed,
                ProtectionLevel.onlyConnection, user.getKey(), user.getKey(), UUID.randomUUID().toString());
        final Point point = PointModelFactory.createPointModel(entity, 0.0, 90, "", 0.0, false, false, false, 0, false, FilterType.fixedHysteresis, 0.1, false, PointType.feed , 0, false, 0.0);
        return (Point) entityService.addUpdateEntity(user, point);



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

    public void setValueService(ValueService valueService) {
        this.valueService = valueService;
    }

    public ValueService getValueService() {
        return valueService;
    }

    public void setServerInfoService(ServerInfo serverInfoService) {
        this.serverInfoService = serverInfoService;
    }

    public ServerInfo getServerInfoService() {
        return serverInfoService;
    }
}
