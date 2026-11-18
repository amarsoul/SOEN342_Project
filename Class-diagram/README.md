[README.md](https://github.com/user-attachments/files/35045844/README.md)
# Partial Design Class Diagram (Iteration II)

`Design-class-diagram.puml` is the partial UML design class diagram for the three critical use cases:
- UC1 Book Appointment
- UC2 Record Consultation
- UC3 Issue Referral

It is one integrated diagram and is the authoritative version. The folder `views/` holds three smaller views, one per use case. They are generated from the same model, so they always match the integrated diagram.

```
Design-class-diagram/
├── Design-class-diagram.puml / .png / .svg   authoritative integrated diagram
├── README.md                                  this file
├── DESIGN-VALIDATION.md                       traceability and checks
└── views/
    ├── DCD-UC1-view.puml / .png / .svg
    ├── DCD-UC2-view.puml / .png / .svg
    └── DCD-UC3-view.puml / .png / .svg
```

## Scope

The diagram contains every class, operation, reference and relationship that the 18 interaction diagrams in `Interaction-diagrams/` need:
- UC1: CO1 to CO5, plus CO4a, the shared isBookable rule.
- UC2: CO7 to CO13.
- UC3: CO14 to CO18, including CO16.

Classes, attributes and associations of the domain model that these diagrams do not use are left out, because this is a partial diagram. Examples are Invoice, Availability and InsuranceCoverage. DESIGN-VALIDATION.md, section 4, lists what was left out and why.

Out of scope: CO6 and CO19 (cancel operations), CO20 to CO36 (Iteration II contracts without interaction diagrams), the login that sets `currentUser`, and program start-up that wires the registries into Console.

## How it was derived

Here is the general progression: operation contracts → interaction diagrams → design classes → methods → attributes → associations and navigability.

1. **Design classes.** Every lifeline type in the 18 diagrams becomes a class. A multiobject (`:Patient`, `:Appointment`, …) is not a class. Note that a message to a multiobject is a message to the collection that the sender holds, so it becomes a collection reference of the sender.
2. **Methods.** Every message received by an object becomes a public operation of its class, with the parameters and return value drawn in the diagram. A message an object sends only to itself becomes a private (`-`) operation.
   - A `create(...)` message becomes a `«create»` constructor.
   - A system operation arriving from the actor becomes a public operation of `Console`.
3. **Attributes.** Attributes come from the domain model, with the same types and multiplicities. All are private, except Person's, which are protected (`#`) for its subclasses. Two design-level identifiers were added, `TimeSlot.slotId` and `ServiceType.serviceTypeId`, because the contracts look objects up by those ids.
4. **Associations and navigability.**
   - An association is drawn with an arrow where one object sends a message to, creates, or keeps a reference to another.
   - The role name at the arrow end is the name of the reference attribute in code.
   - Multiplicities are copied from the domain model.
   - Where an object reaches another only through a parameter or local variable, a dashed dependency is drawn instead.

A script (`DESIGN-VALIDATION.md`, sections 2 and 3) re-reads every message of the 18 interaction diagrams. It confirms that each message has a compatible receiving operation (name and number of arguments) and a visibility path in this diagram: 181 of 181 messages pass.

## Principal design classes

- **Controller**
  - `Console` is the single facade controller. It receives the 17 system operations with the exact SSD names and parameters, including `requestFollowUp(serviceTypeId, preferredTimeFrame)`.
- **Registries (collection classes)**
  - `Patients`, `Appointments`, `ServiceTypes`, `TimeSlots`, `HealthcareProfessionals`, `MedicalSpecialties`, `Medications`, `Referrals` and `ActivityLog`.
- **Domain software classes**
  - **UC1:** `Appointment`, `TimeSlot`, `ServiceType`, `ResourceRequirement`, `Resource`, `WaitingListEntry` and `Referral`.
  - **UC2:** `ClinicalRecord`, `Consultation`, `Diagnosis`, `Prescription`, `PrescriptionLineItem` and `Medication`.
  - **UC3:** `Referral` and `MedicalSpecialty`.
  - **Shared:** `Patient`, `HealthcareProfessional`, `UserAccount`, `Person` (abstract), `ClinicLocation` and `ActivityLogEntry`.

## GRASP responsibilities

| Pattern | Where it appears in the diagram |
|---|---|
| **Controller** | `Console` receives all system operations and delegates. It holds the workflow objects `currentAppointment`, `currentConsultation` and `currentReferral`. |
| **Creator** | Each creator owns the created objects through a composition or association:<br>• `Appointments` creates `Appointment` (CO2, CO12).<br>• `Referrals` creates `Referral` (CO14).<br>• `ActivityLog` creates `ActivityLogEntry`.<br>• `ClinicalRecord` creates `Consultation`, because it *Contains* them.<br>• `Consultation` creates `Diagnosis` (*Records*) and `Prescription` (*Issues*).<br>• `Prescription` creates `PrescriptionLineItem` (*Contains*). |
| **Information Expert** | Each class answers from the information it holds:<br>• `TimeSlot.isBookable`: availability, occupant, offer and date.<br>• `Resource.hasCapacity`: quantity and allocations.<br>• `Appointment.selectSlot`: knows its own selection.<br>• `Patient.findValidReferral`: its referrals.<br>• `HealthcareProfessional.getBookableSlots` and `practisesAtClinic`.<br>• `MedicalSpecialty.getClinicProfessionals`.<br>• `Consultation.end` and `hasClinicalEntry`.<br>• `Referral.isComplete`.<br>• `Appointments.nextAppointmentNumber` and `Referrals.nextReferralNumber`: they know the numbers in use. |
| **Low Coupling** | Console couples only to registries and to the four objects it keeps; everything else is reached through them.<br>Associations are navigable one way unless a message is sent both ways (7 two-way associations, each justified in DESIGN-VALIDATION.md).<br>Setters that only the object itself calls are private. |
| **High Cohesion** | Each class keeps only the operations about its own data.<br>Registries only find, create and number objects.<br>Console contains no domain logic. |

## Why registry classes appear

The interaction diagrams follow Quiz 3, where `:VehicleDB`, `:StationDB` and `:SessionDB` hold the objects of one kind. A registry:
- finds an object by its identifier (`find(...)`);
- creates and adds new objects when it is their Creator (`makeAppointment`, `makeFollowUp`, `makeReferral`, `record`);
- hands out unique numbers (`nextAppointmentNumber`, `nextReferralNumber`).

Registries are design classes, not domain concepts, so they are not in the domain model. In code each registry keeps a `List` of its objects ("a container class is necessary to maintain attribute visibility").

The diagram distinguishes three kinds of operation:
- **Registry operations:** `find`, `search`, `make…` and `next…Number`.
- **Instance operations:** for example `Appointment.setStatus(...)` and `Referral.submit(n)`.
- **Constructors:** `«create»`.

`find` and `add` sent to a multiobject are operations of the collection, not of the element class. `quantityInUse(ts, a)` in CO4a is the one container message with project logic. It is realised as the private helper `Resource.quantityInUse` over `Resource.allocatedTo` (DESIGN-VALIDATION.md, section 5, item 1).

## Important object-visibility decisions

- **Console** has attribute visibility to all nine registries, which are created at start-up and passed in. It also holds `currentUser` and the three workflow objects. No global or singleton access is used.
- **Parameter visibility becomes attribute visibility in constructors**. Examples:
  - `Appointment(p, u)` stores *Booked-for* and *Booked-by*.
  - `Referral(p, hp)` stores *Concerns* and *Issued-by*.
  - `Consultation(a)` stores *Conducted-during*.
  - `PrescriptionLineItem(m, …)` stores *Prescribes*.
  - `ActivityLogEntry(…, u[, p])` stores *Performed-by* and *Concerns*.
- **Two-way navigation** is used only where both directions are used:
  - `Appointment`–`TimeSlot` (*Occupies*)
  - `TimeSlot`–`HealthcareProfessional` (*Available-during*)
  - `ServiceType`–`HealthcareProfessional` (*Provides*)
  - `Appointment`–`Resource` (*Is-allocated*)
  - `TimeSlot`–`WaitingListEntry` (*Is-offered*)
  - `Referral`–`Patient` (*Concerns*)
  - `Referral`–`Appointment` (*Results-in*)
- **Dependencies (dashed arrows)** show local or parameter visibility that carries a message and has no association used for it. Examples:
  - Console reaching `p`, `hp`, `st`, `ms`, `cr` and the CO7 appointment;
  - `TimeSlot.isBookable` reaching `st`, `rr` and `R`;
  - `Appointment` reaching `rr` and `w`;
  - `Referrals` calling `p.addReferral(r)`.

## Reading the diagram

- `+` public, `-` private, `#` protected.
- `«create»` marks a constructor.
- An arrow end with a role name and multiplicity is a reference attribute of the class at the other end.
- A filled diamond is composition, a hollow diamond is aggregation, and a dashed arrow is a dependency.
- Association names in the middle of a line, such as *Booked-for*, are the domain model's names.
- The integrated diagram is dense: a few role names overlap at the top of HealthcareProfessional and ServiceType. Zoom into the SVG or use the use-case views. DESIGN-VALIDATION.md section 3 lists every association end in text.

## How to regenerate the images

```bash
# from the Design-class-diagram folder (PlantUML 1.2024.7 and Graphviz were used)
java -DPLANTUML_LIMIT_SIZE=16384 -jar plantuml.jar -tpng Design-class-diagram.puml views/*.puml
java -DPLANTUML_LIMIT_SIZE=16384 -jar plantuml.jar -tsvg Design-class-diagram.puml views/*.puml
# syntax check only
java -jar plantuml.jar -checkonly Design-class-diagram.puml views/*.puml
```

`-DPLANTUML_LIMIT_SIZE=16384` is needed for the PNG. Without it PlantUML cuts images taller or wider than 4096 pixels. Some edges use PlantUML's `-[norank]->` option, which only affects layout and has no UML meaning.

If the integrated `.puml` is edited by hand, edit the matching view as well, or remove the view, so the views never disagree with the authoritative diagram.
