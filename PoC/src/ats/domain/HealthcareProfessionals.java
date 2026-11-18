package ats.domain;

import java.util.ArrayList;
import java.util.List;

// this is the registry of the healthcare professionals
public class HealthcareProfessionals {

    private final List<HealthcareProfessional> professionals = new ArrayList<>();
    private int counter = 0;


    // return the professional with this id or licence number, or null
    public HealthcareProfessional find(String professionalId) {
        String id = professionalId.trim();

        for (HealthcareProfessional hp : professionals) {
            if (hp.getId().equalsIgnoreCase(id)) {
                return hp;
            }
            if (hp.getLicenceNumber() != null && hp.getLicenceNumber().equalsIgnoreCase(id)) {
                return hp;
            }
        }

        return null;
    }



    // find by first and last name, the csv has no licence number
    public HealthcareProfessional findByName(String first, String last){
        for (HealthcareProfessional hp : professionals) {
            if (hp.hasName(first, last)) {
                return hp;
            }
        }

        return null;
    }


    // return the professional, or create it if it does not exist
    public HealthcareProfessional findOrCreate(String first, String last, String profession) {
        HealthcareProfessional hp = findByName(first, last);

        if (hp == null) {
            hp = new HealthcareProfessional("HP" + (++counter), first.trim(), last.trim(), profession.trim());
            professionals.add(hp);
        }

        return hp;
    }



   // return the professionals that match the search criteria
    public List<HealthcareProfessional> findMatching(AvailabilityCriteria criteria) {
        List<HealthcareProfessional> result = new ArrayList<>();

        for (HealthcareProfessional hp : professionals) {
            if (criteria.matches(hp)) {
                result.add(hp);
            }
        }

        return result;
    }


    // return all the professionals
    public List<HealthcareProfessional> getAll() {
        return professionals;
    }

}