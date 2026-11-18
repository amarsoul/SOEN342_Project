package ats.domain;

import java.util.ArrayList;
import java.util.List;

// this is the registry of the clinic locations
public class ClinicLocations {

    private final List<ClinicLocation> locations = new ArrayList<>();


    // create a new location, name must be unique
    public ClinicLocation add(String name, String address, String phone){
        if (find(name) != null) {
            throw new DomainException("location already exists: " + name);
        }

        ClinicLocation cl = new ClinicLocation(name.trim(), address, phone);
        locations.add(cl);
        return cl;
    }



    // find a location by name, or null
    public ClinicLocation find(String name) {
        for (ClinicLocation cl : locations){
            if (cl.getName().equalsIgnoreCase(name.trim())) {
                return cl;
            }
        }

        return null;
    }

    // get the location, or create it if it doesnt exist
    public ClinicLocation findOrCreate(String name) {
        ClinicLocation cl = find(name);

        if (cl != null) {
            return cl;
        }

        return add(name, null, null);
    }


   // get the only location of the clinic, or null if none or multiple
    public ClinicLocation getOnly() {
        if (locations.size() == 1) {
            return locations.get(0);
        }

        return null;
    }


    public List<ClinicLocation> getAll() {
        return locations;
    }

}