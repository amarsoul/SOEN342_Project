package ats.domain;

import java.util.ArrayList;
import java.util.List;

// this is the registry of the medical specialties
public class MedicalSpecialties {

    private final List<MedicalSpecialty> specialties = new ArrayList<>();


    // return the specialty with this name, or null
    public MedicalSpecialty find(String name) {
        for (MedicalSpecialty ms : specialties) {
            if (ms.getName().equalsIgnoreCase(name.trim())) {
                return ms;
            }
        }

        return null;
    }



    // return the specialty, or create it if it does not exist
    public MedicalSpecialty findOrCreate(String name) {
        MedicalSpecialty ms = find(name);

        if (ms == null) {
            ms = new MedicalSpecialty(null, name.trim());
            specialties.add(ms);
        }

        return ms;
    }

}