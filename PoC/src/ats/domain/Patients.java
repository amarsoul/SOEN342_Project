package ats.domain;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// this is the registry of the patients, it holds the list and creates the patients
public class Patients {
    
private final List<Patient> patients = new ArrayList<>();
private int counter = 0;



public Patient add(String first, String last, String phone, LocalDate dob, String insuranceNumber) {
    
String ins = insuranceNumber;

if (ins == null || ins.isBlank() || ins.trim().equals("-")) {
ins = null;
} else {
ins = ins.trim();
}

Patient p = new Patient("PT" + (++counter), first.trim(), last.trim(), phone, dob, ins);
patients.add(p);
return p;
}



// find patients that match the criteria
public List<Patient> search(String criteria) {
    
List<Patient> matches = new ArrayList<>();

for (Patient p : patients) {
if (p.matches(criteria)) {
matches.add(p);
}
}

return matches;
}



// find patient with this id, or null
public Patient find(String patientId) {
    
for (Patient p : patients) {
if (p.getPatientId().equalsIgnoreCase(patientId.trim())) {
return p;
}
}

return null;
}



public List<Patient> getAll() { 
return patients; 
}

}