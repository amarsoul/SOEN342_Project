package ats.domain;
// This is the service type for the domain model

public class ServiceType {
    private final String id;
    private final String name;
    private final int durationMinutes;
    private final boolean requiresReferral;
    private final MedicalSpecialty specialty;


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

@Override
public String toString() {
return id + "  " + name + " (" + durationMinutes + " min"
+ (specialty == null ? "" : ", " + specialty.getName())
+ (requiresReferral ? ", referral required" : "") + ")";
}
}