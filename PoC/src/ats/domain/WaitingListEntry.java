package ats.domain;

// waiting list entry of the domain model

public class WaitingListEntry {
    
public enum Status { WAITING, OFFERED, SCHEDULED, REMOVED }



private final Patient patient;
private TimeSlot offeredSlot;
private Appointment appointment;
private Status status = Status.WAITING;



public WaitingListEntry(Patient patient) {
this.patient = patient;
}



public Patient getPatient() { 
return patient; 
}


public Status getStatus() { 
return status; 
}


public TimeSlot getOfferedSlot() { 
return offeredSlot; 
}


public Appointment getAppointment() { 
return appointment; 
}



public boolean concerns(Patient p) {
return patient == p;
}



// offer a slot to the patient by phone
public void offer(TimeSlot ts) {
offeredSlot = ts;
status = Status.OFFERED;
ts.setOffer(this);
}



// the patient accepted the offer and the appointment is confirmed
public void accept(Appointment a) {
    
if (offeredSlot != null) {
offeredSlot.clearOffer();
}

setOfferedSlot(null);
setAppointment(a);
setStatus(Status.SCHEDULED);
}



public void setOfferedSlot(TimeSlot ts) { 
offeredSlot = ts; 
}


public void setAppointment(Appointment a) { 
appointment = a; 
}


public void setStatus(Status s) { 
status = s; 
}

}