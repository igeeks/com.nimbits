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

package com.nimbits.server.transactions.service.counter;

import com.nimbits.server.transactions.dao.counter.ShardedCounter;
import com.nimbits.server.transactions.dao.counter.ShardedDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 9/21/12
 * Time: 2:37 PM
 */

@Service("counterService")
@Transactional
public class CounterServiceImpl implements CounterService {


    private ShardedDate shardedDate;

    @Override
   public void createShards(final String name) {

       final ShardedCounter counter = new ShardedCounter(name);
       counter.addShards(10);

   }

   @Override
   public void incrementCounter(final String name) {



       final ShardedCounter counter = new ShardedCounter(name);

       counter.increment();

   }

   @Override
   public long getCount(String name) {
       final ShardedCounter counter = new ShardedCounter(name);
       return counter.getCount();
   }

   @Override
   public Date updateDateCounter(final String name) {
       shardedDate.setName(name);
       return shardedDate.update();


   }

   @Override
   public Date getDateCounter(String name) {
       shardedDate.setName(name);
       return shardedDate.getMostRecent();

   }


    public void setShardedDate(ShardedDate shardedDate) {
        this.shardedDate = shardedDate;
    }
}
