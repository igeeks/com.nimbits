package com.nimbits;

import com.nimbits.client.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.point.*;
import com.nimbits.helper.DevelopmentSettingsHelper;
import com.nimbits.user.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/27/12
 * Time: 12:51 PM
 */
public class ServerUtils {

    protected static void loadRandomDataToTestAccount() throws NimbitsException {

        String email = DevelopmentSettingsHelper.getSetting(SettingType.testAccount);
        String password = DevelopmentSettingsHelper.getSetting(SettingType.testPassword);
        String url = DevelopmentSettingsHelper.getSetting(SettingType.testURL);
        String pointName = "foo";
        GoogleUser user = UserFactory.createGoogleUser(email, password);
        Random r = new Random();

        NimbitsClient client = NimbitsClientFactory.getInstance(user, url);

        Point p = client.getPoint(pointName);
        if (p == null) {
            p = client.addPoint(pointName);
        }
        for (int i = 0; i < 100; i++) {
            client.recordValue(pointName, r.nextDouble());

        }





    }

}
