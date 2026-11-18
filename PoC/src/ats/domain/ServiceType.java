package ats.domain;

import java.util.ArrayList;
import java.util.List;
// This is the service type for the domain model

public class ServiceType {
    private final String id;
    private final String name;
    private final int durationMinutes;
    private final boolean requiresReferral;
    private final MedicalSpecialty specialty;
    private final List<HealthcareProfessional> providers = new ArrayList<>();
    private final List<ResourceRequirement> requirements = new ArrayList<>();


public ServiceType(String id, String name, int durationMinutes, boolean requiresReferral, MedicalSpecialty specialty) {
    this.id = id;
    this.name = name;
    this.durationMinutes = durationMinutes;
    this.requiresReferral = requiresReferral;
    this.specialty = specialty;
}

public String getId() {
    return id;
}
public String getName() {
    return name;
}
public int getDurationMinutes() {
    return durationMinutes;
}
public boolean isRequiresReferral() {       
    return requiresReferral;
}
public MedicalSpecialty getSpecialty() {
    return specialty;
}   
public List<HealthcareProfessional> getProviders() {
    return providers;
}
public List<ResourceRequirement> getRequirements() {
    return requirements;
}

public void addProvider(HealthcareProfessional hp) {
    if (!providers.contains(hp)) {
        providers.add(hp);
    }
}

public void addRequirement(Resource r, int quantityNeeded) {
    requirements.add(new ResourceRequirement(r, quantityNeeded));
}

// the quantity of r this service needs, or 0 if it does not require r
public int getQuantityNeeded(Resource r) {
    for (ResourceRequirement rr : requirements) {
        if (rr.getResource() == r) {
            return rr.getQuantityNeeded();
        }
    }
    return 0;
}

@Override
public String toString() {

return id + "  " + name + " (" + durationMinutes + " min"
+ (specialty == null ? "" : ", " + specialty.getName())
+ (requiresReferral ? ", referral required" : "") + ")";

}

}