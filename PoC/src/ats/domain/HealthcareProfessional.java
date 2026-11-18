package ats.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// this is for helthcare professional of the domain model 
// id is a tech identifer for the poc ,  csv import dont have licence number
public class HealthcareProfessional extends Person {
    
private final String id;

private String licenceNumber;

private final String profession;

private final Set<MedicalSpecialty> specialties = new LinkedHashSet<>(); 
private final Set<ClinicLocation> locations = new LinkedHashSet<>();     
private final Set<ServiceType> services = new LinkedHashSet<>();         

private final List<TimeSlot> timeSlots = new ArrayList<>();              
private final List<Availability> availabilities = new ArrayList<>();     



// construtor for the profesional
public HealthcareProfessional(String id, String firstName, String lastName, String profession) {
super(firstName, lastName, null);
this.id = id;
this.profession = profession;
}



public String getId() { 
return id; 
}


public String getLicenceNumber() { 
return licenceNumber; 
}


public void setLicenceNumber(String licenceNumber) { 
this.licenceNumber = licenceNumber; 
}


public String getProfession() { 
return profession; 
}


public Set<MedicalSpecialty> getSpecialties() { 
return specialties; 
}


public Set<ClinicLocation> getLocations() { 
return locations; 
}


public List<TimeSlot> getTimeSlots() { 
return timeSlots; 
}


public List<Availability> getAvailabilities() { 
return availabilities; 
}



public void addSpecialty(MedicalSpecialty ms) { 
specialties.add(ms); 
}


public void addLocation(ClinicLocation cl) { 
locations.add(cl); 
}


public void addService(ServiceType st) { 
services.add(st); 
}


public void addTimeSlot(TimeSlot ts) { 
timeSlots.add(ts); 
}



public boolean practisesAt(ClinicLocation cl) { 
return locations.contains(cl); 
}


public boolean specializesIn(MedicalSpecialty ms) { 
return specialties.contains(ms); 
}


public boolean provides(ServiceType st) { 
return services.contains(st); 
}



public MedicalSpecialty getMainSpecialty() {
if (specialties.isEmpty()) {
return null;
}
return specialties.iterator().next();
}



// creator pattern: profsional has its availabilities
public Availability createAvailability() {
return new Availability(this);
}


public void addAvailability(Availability av) { 
availabilities.add(av); 
}



// expert pattern: profsional knows the timeslots they are available 

public List<TimeSlot> getBookableSlots(ServiceType st, Appointment booking, LocalDate from, LocalDate to, LocalDate today) {
    
List<TimeSlot> result = new ArrayList<>();

for (TimeSlot ts : timeSlots) {
    
if (!ts.getDate().isBefore(from) && !ts.getDate().isAfter(to) && ts.isBookable(st, booking, today)) {
result.add(ts);
}

}

return result;
}



// creator of a timeslot imported from csv
public TimeSlot createTimeSlot(SlotPeriod period, ClinicLocation cl) {
    
TimeSlot ts = new TimeSlot(TimeSlot.nextId(), period.date(), period.start(), period.end(), this, cl, null);

addTimeSlot(ts);

return ts;
}



// it is true if period overlapp with existing slot at any locaiton
public boolean overlapsExistingSlot(SlotPeriod period) {
    
for (TimeSlot ts : timeSlots) {
    
if (ts.overlaps(period.date(), period.start(), period.end())) {
return true;
}

}

return false;
}



@Override
public String toString() {
String spec = getMainSpecialty() == null ? "" : ", " + getMainSpecialty().getName();
return id + "  " + profession + " " + getFullName() + spec;
}

}