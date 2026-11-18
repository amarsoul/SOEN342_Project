package ats.domain;

// search criteria for availabilty 
public record AvailabilityCriteria(ServiceType serviceType, MedicalSpecialty specialty, HealthcareProfessional professional) {
    
public boolean isEmpty() {
return serviceType == null && specialty == null && professional == null;
}


// check if hp provides the service,  practice the spec, or is the exact professional
public boolean matches(HealthcareProfessional hp) {
    
if (professional != null && professional != hp) {
return false;
}
    
if (serviceType != null && !hp.provides(serviceType)) {
return false;
}
    
if (specialty != null && !hp.specializesIn(specialty)) {
return false;
}
    
return true;
}

}