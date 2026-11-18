package ats.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

// this is timeslot of the domian model (date, startTime, endTime, isAvailable)
// avilable during one professional, located at clinic, genrated by availabilty
public class TimeSlot {
    
private static int counter = 0;


// genrate the next tech id for the poc (S0001, S0002 etc)
public static String nextId() { 
return String.format("S%04d", ++counter); 
}


private final String id;
private final LocalDate date;
private final LocalTime startTime;
private final LocalTime endTime;

private boolean isAvailable = true;
private final HealthcareProfessional professional; 
private final ClinicLocation location;            
private final Availability availability;           



public TimeSlot(String id, LocalDate date, LocalTime startTime, LocalTime endTime,
HealthcareProfessional professional, ClinicLocation location, Availability availability) {
this.id = id;
this.date = date;
this.startTime = startTime;
this.endTime = endTime;
this.professional = professional;
this.location = location;
this.availability = availability;
}



public String getId() { 
return id; 
}


public LocalDate getDate() { 
return date; 
}


public LocalTime getStartTime() { 
return startTime; 
}


public LocalTime getEndTime() { 
return endTime; 
}


public boolean isAvailable() { 
return isAvailable; 
}


public HealthcareProfessional getProfessional() { 
return professional; 
}


public ClinicLocation getLocation() { 
return location; 
}


public Availability getAvailability() { 
return availability; 
}



public long getDurationMinutes() { 
return Duration.between(startTime, endTime).toMinutes(); 
}



// hold the slot for a bookin (isAvailable becomes false)
public void hold() { 
isAvailable = false; 
}


// release the slot if cancel or reselect
public void release() { 
isAvailable = true; 
}



// check if it overlapp
public boolean overlaps(LocalDate d, LocalTime start, LocalTime end) {
return date.equals(d) && startTime.isBefore(end) && start.isBefore(endTime);
}



// check if slot is bookable 

public boolean isBookable(ServiceType st, Appointment booking, LocalDate today) {
    
boolean heldByThisBooking = booking != null && booking.getTimeSlot() == this;


// it must not be in the past
if (date.isBefore(today)) {
return false;
}

// it must be available or already held by this exact booking
if (!isAvailable && !heldByThisBooking) {
return false;
}

// it must be available or already held by this exact booking
// slot must be long enough for the service
if (st != null && getDurationMinutes() < st.getDurationMinutes()) {
return false;
}

return true;
}



@Override
public String toString() {
String spec = professional.getMainSpecialty() == null ? "" : professional.getMainSpecialty().getName() + ", ";
return id + "  " + date + " " + startTime + "-" + endTime + "  " + professional.getProfession() + " "
+ professional.getFullName() + " (" + spec + location.getName() + ")"
+ (isAvailable ? "" : "  [booked]");
}

}