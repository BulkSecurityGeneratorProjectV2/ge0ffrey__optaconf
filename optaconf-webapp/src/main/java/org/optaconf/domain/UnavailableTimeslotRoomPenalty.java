/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaconf.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity(name = "optaconf_unavailtimeslotroompenalty")
public class UnavailableTimeslotRoomPenalty extends AbstractPersistable
{
   @OneToOne
   private Timeslot timeslot;
   
   @OneToOne
   private Room room;

   @ManyToOne
   @JoinColumn(name="schedule_id", nullable=false)
   private Schedule schedule;
   
   public UnavailableTimeslotRoomPenalty()
   {}

   public UnavailableTimeslotRoomPenalty(String id, Timeslot timeslot, Room room)
   {
      super(id);
      this.timeslot = timeslot;
      this.room = room;
   }

   public Timeslot getTimeslot()
   {
      return timeslot;
   }

   public void setTimeslot(Timeslot timeslot)
   {
      this.timeslot = timeslot;
   }

   public Room getRoom()
   {
      return room;
   }

   public void setRoom(Room room)
   {
      this.room = room;
   }
   
   public Schedule getSchedule()
   {
      return schedule;
   }

   public void setSchedule(Schedule schedule)
   {
      this.schedule = schedule;
   }

}
