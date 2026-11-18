package ats.domain;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// this is the registry of the activity log entries
public class ActivityLog {

    private final List<ActivityLogEntry> entries = new ArrayList<>();

 // record an action that doesnt concern a patient
 public void record(UserAccount u, String action, String recordType){
        record(u, action, recordType, null);
    }

    // record an action done by u, with date and time
    public void record(UserAccount u, String action, String recordType, Patient p) {
        LocalDateTime now = SystemDate.today().atTime(LocalTime.now().withNano(0));
        entries.add(new ActivityLogEntry(now, action, recordType, u, p));
    }


   // get all the entries
    public List<ActivityLogEntry> getAll() {
        return entries;
    }

}