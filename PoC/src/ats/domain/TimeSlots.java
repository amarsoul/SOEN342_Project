package ats.domain;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// this is the registry of all the time slots
public class TimeSlots {

    // sort by date, then start time, then professional name
    public static final Comparator<TimeSlot> BY_DATE_AND_TIME =
        Comparator.comparing(TimeSlot::getDate).thenComparing(TimeSlot::getStartTime)
        .thenComparing(t -> t.getProfessional().getFullName());

    private final List<TimeSlot> timeSlots = new ArrayList<>();


    // add a time slot to the list
    public void add(TimeSlot ts){
        timeSlots.add(ts);
    }



    // return the slot with this id, or null
    public TimeSlot find(String slotId) {
        for (TimeSlot ts : timeSlots){
            if (ts.getId().equalsIgnoreCase(slotId.trim())) {
                return ts;
            }
        }

        return null;
    }

   // return every slot sorted by date and time
    public List<TimeSlot> getAll() {
        List<TimeSlot> sorted = new ArrayList<>(timeSlots);
        sorted.sort(BY_DATE_AND_TIME);
        return sorted;
    }

}