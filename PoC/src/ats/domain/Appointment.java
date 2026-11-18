package ats.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// appointement for the domian model. represent a bookin from request to confirm
public class Appointment {
    
private String appointmentNumber;
private LocalDate date;
private LocalTime startTime;
private AppointmentStatus status;
private String preferredTimeFrame;

private final Patient patient;                  
private final UserAccount bookedBy;   
private ServiceType serviceType;            
private TimeSlot timeSlot;                     
private HealthcareProfessional professional;    
private Referral referral;                      
private final List<Resource> resources = new ArrayList<>();



// start a neew booking requst
public Appointment(Patient patient, UserAccount bookedBy) {
this.patient = patient;
this.bookedBy = bookedBy;
this.status = AppointmentStatus.REQUESTED;
}


public String getAppointmentNumber() { 
return appointmentNumber; 
}


public LocalDate getDate() { 
return date; 
}


public LocalTime getStartTime() { 
return startTime; 
}


public AppointmentStatus getStatus() { 
return status; 
}


public String getPreferredTimeFrame() { 
return preferredTimeFrame; 
}


public Patient getPatient() { 
return patient; 
}


public UserAccount getBookedBy() { 
return bookedBy; 
}


public ServiceType getServiceType() { 
return serviceType; 
}


public TimeSlot getTimeSlot() { 
return timeSlot; 
}


public HealthcareProfessional getProfessional() { 
return professional; 
}


public Referral getReferral() { 
return referral; 
}


public List<Resource> getResources() { 
return resources; 
}


public void setStatus(AppointmentStatus status) { 
this.status = status; 
}


public void setAppointmentNumber(String n) { 
this.appointmentNumber = n; 
}


public void setServiceType(ServiceType st) { 
this.serviceType = st; 
}


public void setPreferredTimeFrame(LocalDate from, LocalDate to) { 
this.preferredTimeFrame = from + " to " + to; 
}


public void setTimeSlot(TimeSlot ts) { 
this.timeSlot = ts; 
}


public void setProfessional(HealthcareProfessional hp) { 
this.professional = hp; 
}


public void setDate(LocalDate d) { 
this.date = d; 
}


public void setStartTime(LocalTime t) { 
this.startTime = t; 
}


public void setReferral(Referral r) { 
this.referral = r; 
}


public void addResource(Resource r) { 
resources.add(r); 
}


// requested, confirmed or arrived: it still uses its slot and resources
public boolean isActive() {
return status == AppointmentStatus.REQUESTED || status == AppointmentStatus.CONFIRMED || status == AppointmentStatus.ARRIVED;
}



// specify the srvice and prefered time
public void specifyService(ServiceType st, LocalDate from, LocalDate to) {
this.serviceType = st;
this.preferredTimeFrame = from + " to " + to;
}



// check if slot is availble and handle referal before we hold it
public boolean selectSlot(TimeSlot ts) {
    
HealthcareProfessional hp = ts.getProfessional();

if (!hp.provides(serviceType)) {
return false;
}

if (!ts.isBookable(serviceType, this)) {
return false;
}

Referral r = null;

if (serviceType.isRequiresReferral()) {
    
r = patient.findValidReferral(serviceType.getSpecialty(), hp);

if (r == null) {
return false;
}

}


if (timeSlot != null) {
releaseEarlierSelection(ts);
}


ts.setAvailable(false);
ts.setOccupant(this);

this.timeSlot = ts;
this.professional = hp;
this.date = ts.getDate();
this.startTime = ts.getStartTime();
this.referral = r;

if (r != null) {
r.addAppointment(this);
}

for (ResourceRequirement rr : serviceType.getRequirements()) {
Resource res = rr.getResource();
res.allocate(this);
addResource(res);
}

return true;
}



// the reason why a slot was refused
public String rejectionReason(TimeSlot ts) {
if (!ts.getProfessional().provides(serviceType) || !ts.isBookable(serviceType, this)) {
return "slot no longer available";
}
return "referral required, none found";
}



private void releaseEarlierSelection(TimeSlot newSlot) {
timeSlot.setOccupant(null);
if (timeSlot != newSlot) {
timeSlot.setAvailable(true);
}
if (referral != null) {
referral.removeAppointment(this);
}
for (Resource res : resources) {
res.deallocate(this);
}
clearSelection();
}


public void clearSelection() {
timeSlot = null;
professional = null;
referral = null;
date = null;
startTime = null;
resources.clear();
}



// confrm the appointement and genrate numbre
public void confirm(String n) {
    
if (timeSlot == null) {
throw new DomainException("select a time slot first");
}

setAppointmentNumber(n);
setStatus(AppointmentStatus.CONFIRMED);

if (referral != null) {
referral.setStatus(ReferralStatus.SCHEDULED);
}

WaitingListEntry w = timeSlot.getOffer();

if (w != null) {
w.accept(this);
}

}



// free the slot if book is cancelled or rescheduled
public void releaseSlot() {
    
if (timeSlot != null) {
releaseEarlierSelection(null);
}

}



public boolean isConfirmed() {
return appointmentNumber != null;
}



public String details() {
return "Patient: " + patient.getFullName()
+ "\n  Service: " + (serviceType == null ? "-" : serviceType.getName())
+ "\n  Professional: " + (professional == null ? "-" : professional.getProfession() + " " + professional.getFullName())
+ "\n  Location: " + (timeSlot == null ? "-" : timeSlot.getLocation().getName())
+ "\n  Date and time: " + (timeSlot == null ? "-" : date + " " + startTime + "-" + timeSlot.getEndTime())
+ (referral == null ? "" : "\n  Referral: " + referral.getReferralNumber())
+ "\n  Status: " + status + (appointmentNumber == null ? "" : "\n  Appointment number: " + appointmentNumber);
}

}