package ats.controller;
import ats.domain.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// this is the controller of the system, each public method is a system operation
public class Console {

    private final Patients patients;
    private final Appointments appointments;
    private final ServiceTypes serviceTypes;
    private final TimeSlots timeSlots;
    private final HealthcareProfessionals professionals;
    private final MedicalSpecialties specialties;
    private final ClinicLocations locations;
    private final ActivityLog activityLog;

    private final UserAccount currentUser;
    private Appointment currentAppointment;
    private Availability currentAvailability;

    public Console(UserAccount currentUser, Patients patients, Appointments appointments, ServiceTypes serviceTypes,
                   TimeSlots timeSlots, HealthcareProfessionals professionals, MedicalSpecialties specialties,
                   ClinicLocations locations, ActivityLog activityLog) {
        this.currentUser = currentUser;
        this.patients = patients;
        this.appointments = appointments;
        this.serviceTypes = serviceTypes;
        this.timeSlots = timeSlots;
        this.professionals = professionals;
        this.specialties = specialties;
        this.locations = locations;
        this.activityLog = activityLog;
    }


    // what specifyAvailability return
    public record Preview(List<SlotPeriod> periods, List<SlotPeriod> overlapping) {}

    // what importAvailability return
    public record ImportReport(int imported, List<String> rejected) {}



    // --------- UC1 book an appointment ------------

    // CO1
    public List<Patient> searchPatients(String criteria){
        List<Patient> matches = patients.search(criteria);
        activityLog.record(currentUser, "search patients", "Patient");

        if (matches.isEmpty()) {
            throw new DomainException("no patient found");
        }
        return matches;
    }


    // CO2
    public String startNewBooking(String patientId) {
        Patient p = patients.find(patientId);
        if (p == null) {
            throw new DomainException("no patient with id " + patientId);
        }

        currentAppointment = appointments.makeAppointment(p, currentUser);
        return p.getSummary();
    }



    // CO3, return the bookable slots
    public List<TimeSlot> specifyService(String serviceTypeId, LocalDate fromDate, LocalDate toDate) {
        ServiceType st = serviceTypes.find(serviceTypeId);
        if (st == null) {
            throw new DomainException("no service type with id " + serviceTypeId);
        }
        if (fromDate.isAfter(toDate)) {
            throw new DomainException("the from date is after the to date");
        }

        currentAppointment.setServiceType(st);
        currentAppointment.setPreferredTimeFrame(fromDate, toDate);

        List<TimeSlot> slots = new ArrayList<>();
        for (HealthcareProfessional hp : st.getProviders()){
            slots.addAll(hp.getBookableSlots(fromDate, toDate, st, currentAppointment));
        }
        slots.sort(TimeSlots.BY_DATE_AND_TIME);

        if (slots.isEmpty()) {
            throw new DomainException("no slots available");
        }
        return slots;
    }

   // CO4, return the appointment details
    public String selectTimeSlot(String slotId) {
        TimeSlot ts = timeSlots.find(slotId);
        if (ts == null) {
            throw new DomainException("no time slot with id " + slotId);
        }

        if (!currentAppointment.selectSlot(ts)) {
            throw new DomainException(currentAppointment.rejectionReason(ts));
        }
        return currentAppointment.details();
    }


    // CO5, returns the appointment number
    public String confirmAppointment() {
        String n = appointments.nextAppointmentNumber();
        currentAppointment.confirm(n);
        activityLog.record(currentUser, "book appointment", "Appointment", currentAppointment.getPatient());
        currentAppointment = null;
        return n;
    }


    // CO6, nothing is saved and slot is released
    public void cancelBooking(){
        if (currentAppointment == null) {
            return;
        }
        currentAppointment.releaseSlot();
        appointments.remove(currentAppointment);
        currentAppointment = null;
    }



    // ---------UC4 search availabilty ----------------

    // CO21
    public List<TimeSlot> searchAvailability(AvailabilityCriteria criteria, LocalDate fromDate, LocalDate toDate) {
        if (criteria.isEmpty()) {
            throw new DomainException("give at least a service type, a specialty or a professional");
        }
        if (fromDate.isAfter(toDate)) {
            throw new DomainException("the from date is after the to date");
        }

        List<TimeSlot> slots = new ArrayList<>();
        for (HealthcareProfessional hp : professionals.findMatching(criteria)) {
            slots.addAll(hp.getBookableSlots(fromDate, toDate, criteria.serviceType(), null));
        }
        slots.sort(TimeSlots.BY_DATE_AND_TIME);

        if (slots.isEmpty()) {
            throw new DomainException("no slots available");
        }
        return slots;
    }



    // ------------------- UC5 make availability ------------

    // CO22
    public HealthcareProfessional startAvailability(String professionalId) {
        HealthcareProfessional hp = professionals.find(professionalId);
        if (hp == null) {
            throw new DomainException("no professional with id " + professionalId);
        }

        currentAvailability = hp.createAvailability();
        return hp;
    }


   // CO23, return a preview of the slots
    public Preview specifyAvailability(String locationName, LocalDate fromDate, LocalDate toDate, Repetition repetition,
                                       Set<DayOfWeek> days, Integer dayOfMonth, LocalTime startTime,
                                       LocalTime endTime, int duration) {
        ClinicLocation cl = locations.find(locationName);
        if (cl == null) {
            throw new DomainException("no clinic location named " + locationName);
        }

        currentAvailability.specify(cl, fromDate, toDate, repetition, days, dayOfMonth, startTime, endTime, duration);
        return new Preview(currentAvailability.computeSlotPeriods(), currentAvailability.findOverlappingPeriods());
    }


    // CO24, return how many slots were created
    public int confirmAvailability(){
        List<TimeSlot> created = currentAvailability.confirm();
        for (TimeSlot ts : created) {
            timeSlots.add(ts);
        }

        activityLog.record(currentUser, "make availability", "Availability");
        currentAvailability = null;
        return created.size();
    }

    // CO25, nothing is saved
    public void cancelAvailability() {
        currentAvailability = null;
    }



    // -----------------------import and export csv --------------------

    private static final String[] COLUMNS = {"First Name", "Last Name", "Title", "Specialty", "Date", "Start", "End"};

    // CO26, each valid row become a time slot, bad rows are rejected
    public ImportReport importAvailability(String filePath) {
        CsvFile csv = CsvFile.read(filePath);

        for (String column : COLUMNS) {
            if (!csv.hasColumn(column)) {
                throw new DomainException("missing column " + column);
            }
        }

        int imported = 0;
        List<String> rejected = new ArrayList<>();

        for (int i = 0; i < csv.rowCount(); i++){
            try {
                importRow(csv, i);
                imported++;
            } catch (DomainException e) {
                rejected.add("row " + csv.lineNumber(i) + ": " + e.getMessage());
            } catch (DateTimeParseException e) {
                rejected.add("row " + csv.lineNumber(i) + ": invalid date or time");
            }
        }

        activityLog.record(currentUser, "import availability", "TimeSlot");
        return new ImportReport(imported, rejected);
    }


    private void importRow(CsvFile csv, int i) {
        String first = csv.get(i, "First Name");
        String last = csv.get(i, "Last Name");
        String title = csv.get(i, "Title");
        String specialtyName = csv.get(i, "Specialty");
        LocalDate date = LocalDate.parse(csv.get(i, "Date"));
        LocalTime start = LocalTime.parse(csv.get(i, "Start"));
        LocalTime end = LocalTime.parse(csv.get(i, "End"));

        if (!start.isBefore(end)) {
            throw new DomainException("start is not before end");
        }

        SlotPeriod period = new SlotPeriod(date, start, end);
        HealthcareProfessional existing = professionals.findByName(first, last);
        if (existing != null && existing.overlapsExistingSlot(period)) {
            throw new DomainException("overlaps an existing slot of " + first + " " + last);
        }

        // row is valid, find or create the professional, specialty and location
        HealthcareProfessional hp = professionals.findOrCreate(first, last, title);
        MedicalSpecialty ms = specialties.findOrCreate(specialtyName);
        hp.addSpecialty(ms);
        for (ServiceType st : serviceTypes.getAll()) {
            if (st.getSpecialty() == ms) {
                st.addProvider(hp);
            }
        }

        ClinicLocation cl;
        if (csv.hasColumn("Location")) {
            cl = locations.findOrCreate(csv.get(i, "Location"));
        } else {
            cl = locations.getOnly();
        }
        if (cl == null) {
            throw new DomainException("no location for this row");
        }
        hp.addLocation(cl);

        timeSlots.add(hp.createTimeSlot(period, cl));
    }



   // CO27, return the number of rows written
    public int exportAvailability(String filePath) {
        List<String> lines = new ArrayList<>();
        lines.add("First Name,Last Name,Title,Specialty,Date,Start,End,Location,Status");

        List<TimeSlot> all = timeSlots.getAll();
        for (TimeSlot ts : all){
            HealthcareProfessional hp = ts.getProfessional();
            MedicalSpecialty ms = hp.getMainSpecialty();
            String specialty = "";
            if (ms != null) {
                specialty = ms.getName();
            }
            String status = "Booked";
            if (ts.isAvailable()) {
                status = "Available";
            }

            lines.add(CsvFile.field(hp.getFirstName()) + "," + CsvFile.field(hp.getLastName()) + ","
                    + CsvFile.field(hp.getProfession()) + "," + CsvFile.field(specialty) + ","
                    + ts.getDate() + "," + ts.getStartTime() + "," + ts.getEndTime() + ","
                    + CsvFile.field(ts.getLocation().getName()) + "," + status);
        }

        CsvFile.write(filePath, lines);
        activityLog.record(currentUser, "export availability", "TimeSlot");
        return all.size();
    }



    // ---------------- lookups for the menu -------------------------------

    public ServiceType findServiceType(String id) {
        return serviceTypes.find(id);
    }

    public MedicalSpecialty findSpecialty(String name) {
        return specialties.find(name);
    }


    public HealthcareProfessional findProfessional(String id) {
        return professionals.find(id);
    }

    public List<ServiceType> getServiceTypes() {
        return serviceTypes.getAll();
    }


    public List<HealthcareProfessional> getProfessionals() {
        return professionals.getAll();
    }

}