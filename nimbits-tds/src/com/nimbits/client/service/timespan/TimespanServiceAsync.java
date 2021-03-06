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

package com.nimbits.client.service.timespan;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.timespan.Timespan;

import java.util.Date;

public interface TimespanServiceAsync {
    void createTimespan(final String start, final String end, final AsyncCallback<Timespan> async);

    void createTimespan(final String start, final String end, final int offset, AsyncCallback<Timespan> async) throws NimbitsException;

    void zeroOutDateToStart(Date date, AsyncCallback<Date> async);

    void zeroOutDateToEnd(Date date, AsyncCallback<Date> async);
}
