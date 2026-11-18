package ats.domain;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// this is for the patient of the domain model

public class Patient extends Person{
    private final String patientId;
    private final LocalDate dateOfBirth;
    private final String insuranceNumber;
    // referal is about patients
    private final List<Referral> referrals = new ArrayList<>();

    public Patient(String patientId, String firstName, String lastName,String phone, LocalDate dateOfBirth, String insuranceNumber) {
        super(firstName, lastName, phone);
        this.patientId = patientId;
        this.dateOfBirth = dateOfBirth;
        this.insuranceNumber = insuranceNumber;
    }

    public String getPatientId() {
        return patientId;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public List<Referral> getReferrals() {
        return referrals;
    }

    public void addReferral(Referral referral) {
        referrals.add(referral);
    }

    // patient valid's referral for a specialty or a professional
    public Referral findValidReferral(MedicalSpecialty ms, HealthcareProfessional hp) {
        for (Referral referral : referrals) {
            if (referral.isValidFor(ms, hp)) {
                return referral;
            }
        }
        return null;
    }

    // summary of the patient shown when a booking starts
    public String getSummary() {
        return toString();
    }

    // it maches by insurance number, date of birth, phone number, first name and laste name
    public boolean matches(String criteria){
        String c = criteria.trim().toLowerCase();
        if (c.isEmpty()) {
            return false;
        }
        if(insuranceNumber != null && insuranceNumber.equalsIgnoreCase(c)){
            return true;
        }
        if(dateOfBirth != null && dateOfBirth.toString().equals(c)){
            return true;
        }
        if(getPhone() != null && getPhone().replaceAll("[^0-9]", "").equals(c.replaceAll("[^0-9]", ""))
                && !c.replaceAll("[^0-9]", "").isEmpty()){
            return true;
        }
        return getFullName().toLowerCase().contains(c);
    }

@Override

public String toString() {

    
return patientId + "  " + getFullName() + "  (born " + (dateOfBirth == null ? "?" : dateOfBirth)
     + ", tel. " + getPhone() + ", RAMQ " + (insuranceNumber == null ? "-" : insuranceNumber) + ")";
}
}