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

package com.nimbits.server.io.export;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;

import java.util.List;
import java.util.Map;


public interface ExportHelper {
    String exportPointDataToCSVSeparateColumns(final Map<EntityName, Entity> points, final Map<EntityName, List<Value>> values);
    String exportPointDataToDescriptiveStatistics(final User user, final Map<EntityName, Point> points) throws NimbitsException;
    String exportPointDataToPossibleContinuation(final User user, final Map<EntityName, Point> points) throws NimbitsException;
}
