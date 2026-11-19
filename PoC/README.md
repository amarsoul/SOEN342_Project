# PoC - Appointment Tracking System

Console PoC for Iteration 2. It cover the 5 main features:
1. Import availabilty from CSV
2. Export availability to CSV
3. Search availability (UC4)
4. Make availability (UC5)
5. Book an appointment (UC1)

## How to run
Needs Java 17+. From the PoC folder:

    javac -d out src/ats/domain/*.java src/ats/controller/*.java src/ats/ui/*.java
    java -cp out ats.ui.Menu --today 2026-11-17

`--today` is optionnal, it just fix the system date so the sample data is in the future.

## Data
- Availability starts empty, you load it with option 1 (`data/availability.csv`).
- The main clinic, 3 service types and 3 patients are created at startup so we can test booking.
- There is no database so nothing is saved when you close the program.

## Packages
- `ats.domain` : domain classes and registries
- `ats.controller` : Console and CsvFile
- `ats.ui` : Menu (console UI)
