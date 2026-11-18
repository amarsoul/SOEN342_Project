package ats.domain;

import java.util.ArrayList;
import java.util.List;

// this is the registry of the service types
public class ServiceTypes {
    
private final List<ServiceType> serviceTypes = new ArrayList<>();

private int counter = 0;



// create a new service type
public ServiceType add(String name, int durationMinutes, boolean requiresReferral, MedicalSpecialty ms) {
if (durationMinutes <= 0) {
throw new DomainException("duration must be positive");
}

ServiceType st = new ServiceType("ST" + (++counter), name, durationMinutes, requiresReferral, ms);

serviceTypes.add(st);

return st;
}

// return the service type with this id, or null
public ServiceType find(String serviceTypeId) {

for (ServiceType st : serviceTypes) {
if (st.getId().equalsIgnoreCase(serviceTypeId.trim())) {

return st;
}

}
return null;
}

// return all the service types
public List<ServiceType> getAll() { 
return serviceTypes; 
}

}