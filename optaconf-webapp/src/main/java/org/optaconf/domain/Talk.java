package org.optaconf.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
@Entity(name="optaconf_talk")
public class Talk extends AbstractPersistable {

   @Column private String title;
   @Column private Track track;

    private Timeslot timeslot;
    private Room room;

    public Talk() {
    }

    public Talk(String id, String title, Track track) {
        super(id);
        this.title = title;
        this.track = track;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    @PlanningVariable(valueRangeProviderRefs = {"timeslotRange"})
    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    @PlanningVariable(valueRangeProviderRefs = {"roomRange"})
    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

}
