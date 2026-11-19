package ats.ui;
import ats.controller.Console;
import ats.domain.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

// this is the console menu, it reads user input and call the controller
public class Menu {

    private final Console console;
    private final Scanner in = new Scanner(System.in);

    public Menu(Console console){
        this.console = console;
    }


    public static void main(String[] args) {
        // --today YYYY-MM-DD to set system date
        if (args.length == 2 && args[0].equals("--today")) {
            SystemDate.setToday(LocalDate.parse(args[1]));
        }

        Patients patients = new Patients();
        ServiceTypes serviceTypes = new ServiceTypes();
        MedicalSpecialties specialties = new MedicalSpecialties();
        ClinicLocations locations = new ClinicLocations();

        // clinic, services and patients, availabilty comes from csv import
        locations.add("Main Clinic", "1455 De Maisonneuve Blvd W, Montreal", "514-555-0100");
        serviceTypes.add("Family medicine consultation", 30, false, specialties.findOrCreate("Family Medicine"));
        serviceTypes.add("Cardiology consultation", 60, true, specialties.findOrCreate("Cardiology"));
        serviceTypes.add("Primary care assessment", 30, false, specialties.findOrCreate("Primary Care"));
        patients.add("Emilie", "Lavoie", "514-555-0101", LocalDate.of(1999, 1, 1), "LAVE99010101");
        patients.add("Olivier", "Cote", "514-555-0102", LocalDate.of(1985, 6, 15), "COTO85061502");
        patients.add("Thomas", "Girard", "514-555-0104", LocalDate.of(1992, 8, 24), "GIRT92082403");

        UserAccount u = new UserAccount("admin", "AdministrativeStaff", new AdministrativeStaff("E001", "Admin", "Staff"));
        Console console = new Console(u, patients, new Appointments(), serviceTypes, new TimeSlots(),
                new HealthcareProfessionals(), specialties, locations, new ActivityLog());

        new Menu(console).run();
    }



    private void run() {
        System.out.println("=== Appointment Tracking System - PoC ===");
        System.out.println("Today is " + SystemDate.today());

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1. Import availability from CSV");
            System.out.println("2. Export availability to CSV");
            System.out.println("3. Search availability");
            System.out.println("4. Make availability");
            System.out.println("5. Book an appointment");
            System.out.println("0. Exit");

            String choice = ask("Choice");

            try {
                switch (choice) {
                    case "1":
                        importAvailability();
                        break;
                    case "2":
                        exportAvailability();
                        break;
                    case "3":
                        searchAvailability();
                        break;
                    case "4":
                        makeAvailability();
                        break;
                    case "5":
                        bookAppointment();
                        break;
                    case "0":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice");
                }
            } catch (DomainException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Error: invalid input");
            }
        }
    }


    private void importAvailability(){
        Console.ImportReport report = console.importAvailability(ask("CSV file path"));
        System.out.println(report.imported() + " slots imported, " + report.rejected().size() + " rows rejected.");
        for (String r : report.rejected()) {
            System.out.println("  rejected " + r);
        }
    }

    private void exportAvailability() {
        String path = ask("Destination file path");
        int rows = console.exportAvailability(path);
        System.out.println(rows + " rows written to " + path);
    }



    private void searchAvailability() {
        System.out.println("Enter at least one criteria (press Enter to skip):");

        ServiceType st = null;
        String id = ask("Service type id");
        if (!id.isEmpty()) {
            st = console.findServiceType(id);
        }

        MedicalSpecialty ms = null;
        String name = ask("Specialty");
        if (!name.isEmpty()) {
            ms = console.findSpecialty(name);
        }

        HealthcareProfessional hp = null;
        String hpId = ask("Professional id");
        if (!hpId.isEmpty()) {
            hp = console.findProfessional(hpId);
        }

        LocalDate from = LocalDate.parse(ask("From date (YYYY-MM-DD)"));
        LocalDate to = LocalDate.parse(ask("To date (YYYY-MM-DD)"));

        List<TimeSlot> slots = console.searchAvailability(new AvailabilityCriteria(st, ms, hp), from, to);
        System.out.println(slots.size() + " available slots:");
        for (TimeSlot ts : slots){
            System.out.println("  " + ts);
        }
    }


    private void makeAvailability() {
        for (HealthcareProfessional hp : console.getProfessionals()) {
            System.out.println("  " + hp);
        }
        HealthcareProfessional hp = console.startAvailability(ask("Professional id"));
        System.out.println("Availability for " + hp.getFullName());

        try {
            String location = ask("Clinic location");
            LocalDate from = LocalDate.parse(ask("From date (YYYY-MM-DD)"));
            LocalDate to = LocalDate.parse(ask("To date (YYYY-MM-DD)"));

            String rep = ask("Repetition (D = daily, W = weekly, M = monthly)").toUpperCase();
            Repetition repetition;
            Set<DayOfWeek> days = new HashSet<>();
            Integer dayOfMonth = null;

            if (rep.equals("D")) {
                repetition = Repetition.DAILY;
            } else if (rep.equals("W")) {
                repetition = Repetition.WEEKLY;
                for (String d : ask("Days of week (ex: MON,WED)").split(",")) {
                    days.add(parseDay(d));
                }
            } else if (rep.equals("M")) {
                repetition = Repetition.MONTHLY;
                dayOfMonth = Integer.parseInt(ask("Day of month (1-31)"));
            } else {
                throw new DomainException("unknown repetition " + rep);
            }

            LocalTime start = parseTime(ask("Start time (HH:MM)"));
            LocalTime end = parseTime(ask("End time (HH:MM)"));
            int duration = Integer.parseInt(ask("Slot duration in minutes"));

            Console.Preview preview = console.specifyAvailability(location, from, to, repetition, days, dayOfMonth, start, end, duration);
            System.out.println("Preview: " + preview.periods().size() + " slots will be generated.");
            if (!preview.overlapping().isEmpty()) {
                System.out.println(preview.overlapping().size() + " slots overlap existing ones and will be skipped.");
            }

            if (ask("Confirm (y/n)").equalsIgnoreCase("y")) {
                int created = console.confirmAvailability();
                System.out.println("Saved: " + created + " time slots created.");
            } else {
                console.cancelAvailability();
                System.out.println("Cancelled.");
            }
        } catch (RuntimeException e) {
            console.cancelAvailability();
            throw e;
        }
    }



    private void bookAppointment(){
        List<Patient> found = console.searchPatients(ask("Search patient (RAMQ, birth date, phone or name)"));
        for (Patient p : found) {
            System.out.println("  " + p);
        }

        String patientId;
        if (found.size() == 1) {
            patientId = found.get(0).getPatientId();
        } else {
            patientId = ask("Patient id");
        }
        System.out.println("Booking for " + console.startNewBooking(patientId));

        try {
            for (ServiceType st : console.getServiceTypes()) {
                System.out.println("  " + st);
            }
            String serviceId = ask("Service type id");
            LocalDate from = LocalDate.parse(ask("From date (YYYY-MM-DD)"));
            LocalDate to = LocalDate.parse(ask("To date (YYYY-MM-DD)"));

            List<TimeSlot> slots = console.specifyService(serviceId, from, to);
            for (TimeSlot ts : slots) {
                System.out.println("  " + ts);
            }

           // ask until a slot is accepted or n to cancel
            while (true) {
                String slotId = ask("Slot id (or n to cancel)");
                if (slotId.equalsIgnoreCase("n")) {
                    console.cancelBooking();
                    System.out.println("Booking cancelled.");
                    return;
                }
                try {
                    System.out.println(console.selectTimeSlot(slotId));
                    break;
                } catch (DomainException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

            if (ask("Confirm (y/n)").equalsIgnoreCase("y")) {
                System.out.println("Appointment confirmed. Number: " + console.confirmAppointment());
            } else {
                console.cancelBooking();
                System.out.println("Booking cancelled.");
            }
        } catch (RuntimeException e) {
            console.cancelBooking();
            throw e;
        }
    }


    // read one line from user
    private String ask(String prompt) {
        System.out.print(prompt + ": ");
        return in.nextLine().trim();
    }

    private LocalTime parseTime(String t){
        t = t.replace(" ", "");
        if (t.length() == 4) {
            t = "0" + t;
        }
        return LocalTime.parse(t);
    }

    private DayOfWeek parseDay(String d) {
        String day = d.trim().toUpperCase();
        for (DayOfWeek dow : DayOfWeek.values()) {
            if (dow.name().startsWith(day)) {
                return dow;
            }
        }
        throw new DomainException("unknown day " + d);
    }

}