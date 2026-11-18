package ats.domain;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
//referral class for the domain model, it concerns a Patient and request a medical specialty or professional to see the patient, it has a status and a date of creation
public class Referral {

    private final String referralNumber;
    private final LocalDate dateIssued;
    private final String clinicalReason;
    private ReferralStatus status = ReferralStatus.OPEN; 
    private final Patient patient;                       
    private final MedicalSpecialty requestedSpecialty; 
    private final HealthcareProfessional requestedProfessional;
    private final List<Appointment> appointments = new ArrayList<>();
public Referral(String referralNumber, LocalDate dateIssued, String clinicalReason, Patient patient, MedicalSpecialty requestedSpecialty, HealthcareProfessional requestedProfessional) {
        this.referralNumber = referralNumber;
        this.dateIssued = dateIssued;
        this.clinicalReason = clinicalReason;
        this.patient = patient;
        this.requestedSpecialty = requestedSpecialty;
        this.requestedProfessional = requestedProfessional;
    }

    public String getReferralNumber() {
        return referralNumber;
    }

    public LocalDate getDateIssued() {
        return dateIssued;
    }

    public String getClinicalReason() {
        return clinicalReason;
    }

    public ReferralStatus getStatus() {
        return status;
    }

    public void setStatus(ReferralStatus status) {
        this.status = status;
    }

    public Patient getPatient() {
        return patient;
    }

    public MedicalSpecialty getRequestedSpecialty() {
        return requestedSpecialty;
    }

    public HealthcareProfessional getRequestedProfessional() {
        return requestedProfessional;
    }

    public void addAppointment(Appointment a) {
        if (!appointments.contains(a)) {
            appointments.add(a);
        }
    }

    public void removeAppointment(Appointment a) {
        appointments.remove(a);
    }
   // valid for a service and a professional
    public boolean isValidFor(MedicalSpecialty ms, HealthcareProfessional hp) {

       boolean active = status == ReferralStatus.OPEN || status == ReferralStatus.SCHEDULED;

        boolean sameSpecialty = requestedSpecialty != null && requestedSpecialty == ms;

        boolean sameProfessional = requestedProfessional != null && requestedProfessional == hp;

        return active && (sameSpecialty || sameProfessional);
    }

    // the referral becomes scheduled when an appointment is created for the patient 
    public void markScheduled() {
         status = ReferralStatus.SCHEDULED;
      
        }

        @Override
    public String toString() {
        return referralNumber + "  " + patient.getFullName() + " -> "
        + (requestedSpecialty != null ? requestedSpecialty.getName() : "")
        + (requestedProfessional != null ? " " + requestedProfessional.getFullName() : "")
        + " (" + status + ", " + dateIssued + ")";
    }
}