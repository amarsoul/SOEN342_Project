package ats.domain;

import java.time.LocalDate;
import java.time.LocalTime;

// appointement for the domian model. represent a bookin from request to confirm
public class Appointment {
    
private String appointmentNumber;
private LocalDate date;
private LocalTime startTime;
private AppointmentStatus status;
private String preferredTimeFrame;

private final Patient patient;                  
private final AdministrativeStaff bookedBy;   
private ServiceType serviceType;            
private TimeSlot timeSlot;                     
private HealthcareProfessional professional;    
private Referral referral;                      



// start a neew booking requst
public Appointment(Patient patient, AdministrativeStaff bookedBy) {
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


public AdministrativeStaff getBookedBy() { 
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



// specify the srvice and prefered time
public void specifyService(ServiceType st, LocalDate from, LocalDate to) {
this.serviceType = st;
this.preferredTimeFrame = from + " to " + to;
}



// check if slot is availble and handle referal before we hold it
public void selectTimeSlot(TimeSlot ts, LocalDate today) {
    
if (!ts.isBookable(serviceType, this, today)) {
throw new DomainException("slot no longer available");
}

HealthcareProfessional hp = ts.getProfessional();

if (!hp.provides(serviceType)) {
throw new DomainException("slot no longer available");
}

Referral r = null;

if (serviceType.isRequiresReferral()) {
    
r = patient.findValidReferral(serviceType, hp);

if (r == null) {
throw new DomainException("referral required, none found");
}

}


if (timeSlot != null && timeSlot != ts) {
releaseEarlierSelection();
}


ts.hold();

this.timeSlot = ts;
this.professional = hp;
this.date = ts.getDate();
this.startTime = ts.getStartTime();
this.referral = r;

}



private void releaseEarlierSelection() {
timeSlot.release();
timeSlot = null;
professional = null;
referral = null;
}



// confrm the appointement and genrate numbre
public void confirm() {
    
if (timeSlot == null) {
throw new DomainException("select a time slot first");
}

this.status = AppointmentStatus.CONFIRMED;
this.appointmentNumber = generateAppointmentNumber();

if (referral != null) {
referral.markScheduled();
}

}


private static int counter = 0;


private static String generateAppointmentNumber() {
return String.format("A%05d", ++counter);
}



// free the slot if book is cancelled or rescheduled
public void releaseSlot() {
    
if (timeSlot != null) {
releaseEarlierSelection();
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