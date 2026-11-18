package ats.domain;

import java.time.LocalDateTime;

// this is a log entry of the domain model to track actions 
// performed by admin and concerns a patient
public record ActivityLogEntry(LocalDateTime timestamp, String action, String recordType,AdministrativeStaff performedBy, Patient concerns) {
    
@Override
public String toString() {
    
String patientInfo = concerns == null ? "" : ", patient " + concerns.getFullName();
    
return timestamp.withNano(0) + "  " + action + " (" + recordType + ") by " + performedBy.getFullName() + patientInfo;
}

}