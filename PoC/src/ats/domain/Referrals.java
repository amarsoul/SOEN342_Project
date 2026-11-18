package ats.domain;
import java.util.ArrayList;
import java.util.List;

// this is the registry of the referals
public class Referrals {

    private final List<Referral> referrals = new ArrayList<>();
    private int counter = 0;


    // record an external referral and add it to the patient
    public Referral record(Patient p, MedicalSpecialty ms, HealthcareProfessional hp, String reason){
        if (ms == null && hp == null) {
            throw new DomainException("a referral requests a specialty, a professional or both");
        }

        Referral r = new Referral("R" + (++counter), SystemDate.today(), reason, p, ms, hp);
        referrals.add(r);
        p.addReferral(r);
        return r;
    }

   // get all the referrals
    public List<Referral> getAll() {
        return referrals;
    }

}