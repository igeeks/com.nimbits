/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.external.google.drive;

import com.nimbits.client.service.docs.DriveService;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/3/12
 * Time: 3:22 PM
 */
public class DriveServiceFactory {

    private static DriveService _instance;


    public static DriveService getInstance() {
          if (_instance == null) {
              _instance = new DriveServiceImpl();

          }
        return _instance;
    }
}
