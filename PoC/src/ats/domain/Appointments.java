package ats.domain;

import java.util.ArrayList;
import java.util.List;

// this is the registry of the appointments, it creates them and give the numbers
public class Appointments {
    
private final List<Appointment> appointments = new ArrayList<>();

private int counter = 0;



// new requested appointment for p, booked by u
public Appointment makeAppointment(Patient p, UserAccount u) {
Appointment a = new Appointment(p, u);
appointments.add(a);
return a;
}



// get the next unique appointment number
public String nextAppointmentNumber() {
counter++;
return String.format("A%05d", counter);
}



// delete the unfinished booking
public void remove(Appointment a) {
appointments.remove(a);
}



// return only the confirmed appointments
public List<Appointment> getConfirmed() {
    
List<Appointment> result = new ArrayList<>();

for (Appointment a : appointments) {
if (a.getAppointmentNumber() != null) {
result.add(a);
}
}

return result;
}

}