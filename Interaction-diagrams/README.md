# Interaction Diagrams (Iteration II)

This folder holds the UML sequence diagrams (interaction diagrams) for the three critical use cases from Iteration I: UC1 Book Appointment, UC2 Record Consultation and UC3 Issue Referral. There is one diagram per operation contract, following the course method:

Use case → SSD → system operation → operation contract → **interaction diagram** → design class diagram.

Each diagram starts where its SSD message enters the system. The system operation arrives at the controller as a found message (`[->`), and the diagram shows the object messages that make the contract's postconditions true. 

## Scope

| Use case | Contract | Diagram |
|---|---|---|
| UC1 | CO1 searchPatients | `UC1/CO1-searchPatients` |
| UC1 | CO2 startNewBooking | `UC1/CO2-startNewBooking` |
| UC1 | CO3 specifyService | `UC1/CO3-specifyService` |
| UC1 | CO4 selectTimeSlot | `UC1/CO4-selectTimeSlot` |
| UC1 | (shared by CO3 and CO4) the "bookable" rule of CO21 | `UC1/CO4a-isBookable` |
| UC1 | CO5 confirmAppointment | `UC1/CO5-confirmAppointment` |
| UC2 | CO7 startConsultation | `UC2/CO7-startConsultation` |
| UC2 | CO8 recordObservations | `UC2/CO8-recordObservations` |
| UC2 | CO9 addDiagnosis | `UC2/CO9-addDiagnosis` |
| UC2 | CO10 recordRecommendations | `UC2/CO10-recordRecommendations` |
| UC2 | CO11 addPrescriptionItem | `UC2/CO11-addPrescriptionItem` |
| UC2 | CO12 requestFollowUp | `UC2/CO12-requestFollowUp` |
| UC2 | CO13 endConsultation | `UC2/CO13-endConsultation` |
| UC3 | CO14 startReferral | `UC3/CO14-startReferral` |
| UC3 | CO15 setRequestedSpecialty | `UC3/CO15-setRequestedSpecialty` |
| UC3 | CO16 setRequestedProfessional (optional, extension 3a) | `UC3/CO16-setRequestedProfessional` |
| UC3 | CO17 enterReferralDetails | `UC3/CO17-enterReferralDetails` |
| UC3 | CO18 submitReferral | `UC3/CO18-submitReferral` |

Each diagram is stored as `.puml` (source), `.png`

`CO4a-isBookable` is not a separate system operation. It is the object-level design of the "bookable" rule defined in CO21. CO3 and CO4 both use it through a `ref` frame, so the rule is drawn once and not repeated. This is the same technique the lectures use to keep one diagram per operation readable.

**Out of scope for this deliverable:** the cancel operations CO6 (cancelBooking) and CO19 (cancelReferral), the waitlist operation CO20, and the Iteration II contracts CO21 to CO36 (UC4, UC5, CSV, waiting list and resources). 


## Participating classes

- **Controller:** `Console`. This is the single facade controller for the CLI. It represents the whole system, which matches the `:System` lifeline of the SSDs.
- **Registries:** `Patients`, `Appointments`, `ServiceTypes`, `TimeSlots`, `HealthcareProfessionals`, `MedicalSpecialties`, `Medications`, `Referrals` and `ActivityLog`. Each one owns a multiobject of the matching domain class.
- **Domain (software) classes from the domain model:** `UserAccount`, `Patient`, `Appointment`, `TimeSlot`, `HealthcareProfessional`, `ServiceType`, `ResourceRequirement`, `Resource`, `WaitingListEntry`, `Referral`, `MedicalSpecialty`, `ClinicalRecord`, `Consultation`, `Diagnosis`, `Prescription`, `PrescriptionLineItem`, `Medication` and `ActivityLogEntry`.

## Controller

`Console` is the controller for every system operation, a facade controller ("By Controller" note on every diagram). It does no domain work itself. It looks objects up through registries and delegates to the expert.

`Console` keeps the workflow state between operations of one use case:

- `currentUser : UserAccount` is the authenticated *u* of the contracts.
- `currentAppointment : Appointment` is the booking *a* (UC1, CO2 to CO5).
- `currentConsultation : Consultation` is *c* (UC2, CO7 to CO13).
- `currentReferral : Referral` is *r* (UC3, CO14 to CO18).

## GRASP assignments

| Responsibility | Assigned to | Pattern and reason |
|---|---|---|
| Receive every system operation | `Console` | Controller (facade, represents the system) |
| Find Patient / TimeSlot / ServiceType / Medication / MedicalSpecialty / HealthcareProfessional / Appointment by id | the matching registry | Expert (it holds the collection) |
| Create `Appointment` (CO2, CO12) | `Appointments` | Creator (aggregates/records Appointments, Quiz 3 SessionDB style) |
| Create `Referral` (CO14) | `Referrals` | Creator (aggregates Referrals) |
| Create `ActivityLogEntry` (CO1, CO5, CO7, CO13, CO18) | `ActivityLog` | Creator (records the entries) |
| Create `Consultation` (CO7) | `ClinicalRecord` | Creator (ClinicalRecord *Contains* Consultations) |
| Create `Diagnosis` (CO9) | `Consultation` | Creator (Consultation *Records* Diagnoses) |
| Create `Prescription` (CO11) | `Consultation` | Creator (Consultation *Issues* the Prescription) |
| Create `PrescriptionLineItem` (CO11) | `Prescription` | Creator (Prescription *Contains* line items, has the initializing data) |
| Decide whether a slot is bookable (CO21 rule) | `TimeSlot` | Expert (knows its date, length, availability, occupant and offer), helped by `ServiceType`, `ResourceRequirement` and `Resource` |
| Check remaining resource capacity | `Resource` | Expert (knows its quantity and its *Is-allocated* Appointments) |
| Form or replace a slot selection (CO4) | `Appointment` | Expert (knows its ServiceType, Patient and current selection) |
| Find a valid referral (CO4) | `Patient` | Expert (the Patient's Referrals *Concern* it) |
| List a professional's bookable or available slots (CO3, CO12) | `HealthcareProfessional` | Expert (*Available-during* its TimeSlots) |
| Know who provides a service (CO3, CO12) | `ServiceType` | Expert (*Provides*) |
| Allocate or release a resource for a booking (CO4) | `Resource` | Expert (*Is-allocated*) |
| Accept a phone offer (CO5) | `WaitingListEntry` | Expert (knows its offered slot and status) |
| Confirm a booking (CO5) | `Appointment`, with the number from `Appointments` | Expert (Appointments knows the numbers in use) |
| Next unique referral number (CO18) | `Referrals` | Expert |
| Check clinical access and the user's professional (CO7, CO14) | `UserAccount` | Expert (knows its role and the professional it identifies) |
| Check practises at the clinic (CO14 to CO16) | `HealthcareProfessional` | Expert (*Practises-at*) |
| List clinic professionals of a specialty (CO15) | `MedicalSpecialty` | Expert (*Specializes-in*) |
| Record clinical entries, end consultation (CO8 to CO13) | `Consultation` | Expert (owns the attributes; knows its Appointment) |
| Referral completeness and submission (CO17, CO18) | `Referral` | Expert |

## Visibility assumptions

- **Attribute visibility**
  - `Console` holds references to every registry. They are created when the program starts and passed to the Console constructor, so no global (singleton) access is needed.
  - `Console` also holds `currentUser`, `currentAppointment`, `currentConsultation` and `currentReferral`.
  - Each registry holds its multiobject.
  - Domain objects hold references for the associations they navigate. Examples: `Appointment` → its Patient, ServiceType, TimeSlot, HealthcareProfessional, Referral and allocated Resources. `TimeSlot` → its HealthcareProfessional, its occupant Appointment and its offered WaitingListEntry. `ServiceType` → its providers and ResourceRequirements. `Patient` → its ClinicalRecord and Referrals. `Consultation` → its Appointment, Prescription and Diagnoses. `Resource` → its allocated Appointments.
- **Parameter visibility.** The receiver sees an object passed as an argument. Examples: `ts` in `a.selectSlot(ts)`, `st` and `a` in `ts.isBookable(st, a)`, `p` and `u` in `makeAppointment(p, u)`, `a` in `cr.startConsultation(a)`, and `m` in `addLineItem(m, …)`.
- **Local visibility.** The sender sees an object returned by an earlier message in the same operation. Examples: `p := Patients.find(id)`, `ts := TimeSlots.find(slotId)`, `hp := ts.getProfessional()`, `r := p.findValidReferral(ms, hp)`, `R := rr.getResource()` and `cr := p.getClinicalRecord()`.
- **Global visibility** is **not used**.


## How to regenerate the images

The `.puml` files are the source. To render them (PlantUML 1.2024.7 and Graphviz were used here):

```bash
# from the Interaction-diagrams folder
java -jar plantuml.jar -tpng UC1/*.puml UC2/*.puml UC3/*.puml
java -jar plantuml.jar -tsvg UC1/*.puml UC2/*.puml UC3/*.puml
# optional syntax check only
java -jar plantuml.jar -checkonly UC1/*.puml UC2/*.puml UC3/*.puml
```

Each `@startuml <name>` line matches the file name, so each image gets the same name as its source.

## Notation used

- Object names are underlined (`a:Appointment`). Multiobjects (`collections`) show the collection a registry holds.
- `**` or `create` marks the new object, and its lifeline starts at the create message.
- Conditions use `opt`, `alt`/`else` and `loop` frames, with the guard written in words.
- Return values are shown as `x := message(...)`, with dashed return arrows where they help to read the diagram.
- Notes "By Controller", "By Creator", "By Expert" name the GRASP pattern that justifies the receiver (Tutorial 3 style). Notes also name the domain-model association used, such as *(Is-for)* or *(Requests-follow-up)*.

