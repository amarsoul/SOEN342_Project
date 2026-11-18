[DESIGN-VALIDATION.md](https://github.com/user-attachments/files/35045890/DESIGN-VALIDATION.md)
# Design Validation: Partial Design Class Diagram (UC1–UC3)

This file checks `Design-class-diagram.puml` against the 18 interaction diagrams in `Interaction-diagrams/` and against the domain model. Sections 2 and 3 were produced by a script that reads every message in the 18 `.puml` interaction diagrams and looks up the receiving class, operation name, number of arguments and the visibility the sender needs, in the same model that generates the class diagram. The script found **181 object messages; 181 have a compatible receiving operation or constructor, and 0 problems**. A clean script run is not proof that the design is correct; the judgement calls are in sections 4 and 5.

## 1. Participating design classes

| Class | Kind | Source | Reference attributes (role: Type [multiplicity]) | Shown in views |
|---|---|---|---|---|
| Console | Controller (facade) | Design only (not in domain model) | patients: Patients [1]<br>appointments: Appointments [1]<br>serviceTypes: ServiceTypes [1]<br>timeSlots: TimeSlots [1]<br>professionals: HealthcareProfessionals [1]<br>specialties: MedicalSpecialties [1]<br>medications: Medications [1]<br>referrals: Referrals [1]<br>activityLog: ActivityLog [1]<br>currentUser: UserAccount [0..1]<br>currentAppointment: Appointment [0..1]<br>currentConsultation: Consultation [0..1]<br>currentReferral: Referral [0..1] | UC1, UC2, UC3 |
| Patients | Registry / collection (design class) | Design only (not in domain model) | patients: Patient [*] | UC1, UC3 |
| Appointments | Registry / collection (design class) | Design only (not in domain model) | appointments: Appointment [*] | UC1, UC2 |
| ServiceTypes | Registry / collection (design class) | Design only (not in domain model) | serviceTypes: ServiceType [*] | UC1, UC2 |
| TimeSlots | Registry / collection (design class) | Design only (not in domain model) | slots: TimeSlot [*] | UC1 |
| HealthcareProfessionals | Registry / collection (design class) | Design only (not in domain model) | professionals: HealthcareProfessional [*] | UC3 |
| MedicalSpecialties | Registry / collection (design class) | Design only (not in domain model) | specialties: MedicalSpecialty [*] | UC3 |
| Medications | Registry / collection (design class) | Design only (not in domain model) | medications: Medication [*] | UC2 |
| Referrals | Registry / collection (design class) | Design only (not in domain model) | referrals: Referral [*] | UC3 |
| ActivityLog | Registry / collection (design class) | Design only (not in domain model) | entries: ActivityLogEntry [*] | UC1, UC2, UC3 |
| UserAccount | Domain software class | Domain model | person: Person [1] | UC1, UC2, UC3 |
| Person | Abstract domain class | Domain model | (none) | UC2, UC3 |
| Patient | Domain software class | Domain model | referrals: Referral [*]<br>clinicalRecord: ClinicalRecord [1] | UC1, UC2, UC3 |
| HealthcareProfessional | Domain software class | Domain model | locations: ClinicLocation [0..*]<br>slots: TimeSlot [*]<br>services: ServiceType [*] | UC1, UC2, UC3 |
| ClinicLocation | Domain software class | Domain model | (none) | UC3 |
| MedicalSpecialty | Domain software class | Domain model | professionals: HealthcareProfessional [*] | UC1, UC3 |
| Appointment | Domain software class | Domain model | timeSlot: TimeSlot [0..1]<br>patient: Patient [1]<br>bookedBy: UserAccount [1]<br>professional: HealthcareProfessional [0..1]<br>serviceType: ServiceType [1]<br>resources: Resource [*]<br>referral: Referral [0..1] | UC1, UC2 |
| TimeSlot | Domain software class | Domain model | professional: HealthcareProfessional [1]<br>occupant: Appointment [0..1]<br>offer: WaitingListEntry [0..1] | UC1, UC2 |
| ServiceType | Domain software class | Domain model | providers: HealthcareProfessional [*]<br>requirements: ResourceRequirement [*]<br>specialty: MedicalSpecialty [0..1] | UC1, UC2 |
| ResourceRequirement | Domain software class | Domain model | resource: Resource [1] | UC1 |
| Resource | Domain software class | Domain model | allocatedTo: Appointment [*] | UC1 |
| WaitingListEntry | Domain software class | Domain model | offeredSlot: TimeSlot [0..1]<br>patient: Patient [1]<br>appointment: Appointment [0..1] | UC1 |
| Referral | Domain software class | Domain model | patient: Patient [1]<br>issuedBy: HealthcareProfessional [1]<br>requestedSpecialty: MedicalSpecialty [0..1]<br>requestedProfessional: HealthcareProfessional [0..1]<br>appointments: Appointment [*] | UC1, UC3 |
| ClinicalRecord | Domain software class | Domain model | consultations: Consultation [*] | UC2 |
| Consultation | Domain software class | Domain model | appointment: Appointment [1]<br>diagnoses: Diagnosis [*]<br>prescription: Prescription [0..1]<br>followUps: Appointment [*] | UC2 |
| Diagnosis | Domain software class | Domain model | (none) | UC2 |
| Prescription | Domain software class | Domain model | lineItems: PrescriptionLineItem [1..*] | UC2 |
| PrescriptionLineItem | Domain software class | Domain model | medication: Medication [1] | UC2 |
| Medication | Domain software class | Domain model | (none) | UC2 |
| ActivityLogEntry | Domain software class | Domain model | performedBy: UserAccount [1]<br>patient: Patient [0..1] | UC1, UC2, UC3 |

30 classes: 1 controller, 9 registries, 20 domain software classes (Person is abstract and included only because UserAccount *Identifies* a Person and `getProfessional()` must reach the HealthcareProfessional).

## 2. Operation-to-class traceability

Every operation in the class diagram, with the messages in the interaction diagrams that it receives. "Found" means the system operation arriving at `:Console` from the actor (the SSD message). Private (`-`) operations are only ever sent by the object to itself.

### Console

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ searchPatients(criteria: SearchCriteria): Patient [*]` | CO1: actor (SSD message) | `searchPatients(criteria)` |
| `+ startNewBooking(patientId: PatientID): String` | CO2: actor (SSD message) | `startNewBooking(patientId)` |
| `+ specifyService(serviceTypeId: String, fromDate: Date, toDate: Date): TimeSlot [*]` | CO3: actor (SSD message) | `specifyService(serviceTypeId, fromDate, toDate)` |
| `+ selectTimeSlot(slotId: String): String` | CO4: actor (SSD message) | `selectTimeSlot(slotId)` |
| `+ confirmAppointment(): String` | CO5: actor (SSD message) | `confirmAppointment()` |
| `+ startConsultation(appointmentId: String): String` | CO7: actor (SSD message) | `startConsultation(appointmentId)` |
| `+ recordObservations(observations: String)` | CO8: actor (SSD message) | `recordObservations(observations)` |
| `+ addDiagnosis(code: String, description: String)` | CO9: actor (SSD message) | `addDiagnosis(code, description)` |
| `+ recordRecommendations(treatment: String, followUp: String)` | CO10: actor (SSD message) | `recordRecommendations(treatment, followUp)` |
| `+ addPrescriptionItem(medicationId: String, dosage: String, frequency: String, duration: String, quantity: Integer)` | CO11: actor (SSD message) | `addPrescriptionItem(medicationId, dosage,  frequency, duration, quantity)` |
| `+ requestFollowUp(serviceTypeId: String, preferredTimeFrame: String): String` | CO12: actor (SSD message) | `requestFollowUp(serviceTypeId, preferredTimeFrame)` |
| `+ endConsultation(): String` | CO13: actor (SSD message) | `endConsultation()` |
| `+ startReferral(patientId: PatientID): String` | CO14: actor (SSD message) | `startReferral(patientId)` |
| `+ setRequestedSpecialty(specialtyId: String): HealthcareProfessional [*]` | CO15: actor (SSD message) | `setRequestedSpecialty(specialtyId)` |
| `+ setRequestedProfessional(professionalId: String): Boolean` | CO16: actor (SSD message) | `setRequestedProfessional(professionalId)` |
| `+ enterReferralDetails(clinicalReason: String, priority: Priority)` | CO17: actor (SSD message) | `enterReferralDetails(clinicalReason, priority)` |
| `+ submitReferral(): String` | CO18: actor (SSD message) | `submitReferral()` |

### Patients

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ search(criteria: SearchCriteria): Patient [*]` | CO1: Console | `matches := search(criteria)` |
| `+ find(patientId: PatientID): Patient` | CO2: Console, CO14: Console | `p := find(patientId)` |

### Appointments

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ find(appointmentId: String): Appointment` | CO7: Console | `a := find(appointmentId)` |
| `+ makeAppointment(p: Patient, u: UserAccount): Appointment` | CO2: Console | `a := makeAppointment(p, u)` |
| `+ makeFollowUp(p: Patient, st: ServiceType, timeFrame: String, u: UserAccount): Appointment` | CO12: Console | `fa := makeFollowUp(p, st, preferredTimeFrame, u)` |
| `+ nextAppointmentNumber(): String` | CO5: Console | `n := nextAppointmentNumber()` |

### ServiceTypes

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ find(serviceTypeId: String): ServiceType` | CO3: Console, CO12: Console | `st := find(serviceTypeId)` |

### TimeSlots

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ find(slotId: String): TimeSlot` | CO4: Console | `ts := find(slotId)` |

### HealthcareProfessionals

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ find(licenceNumber: String): HealthcareProfessional` | CO16: Console | `hp := find(professionalId)` |

### MedicalSpecialties

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ find(code: String): MedicalSpecialty` | CO15: Console | `ms := find(specialtyId)` |

### Medications

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ find(din: String): Medication` | CO11: Console | `m := find(medicationId)` |

### Referrals

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ makeReferral(p: Patient, hp: HealthcareProfessional): Referral` | CO14: Console | `r := makeReferral(p, hp)` |
| `+ nextReferralNumber(): String` | CO18: Console | `n := nextReferralNumber()` |

### ActivityLog

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ record(u: UserAccount, action: String, recordType: String)` | CO1: Console | `record(u, "search patients", "Patient")` |
| `+ record(u: UserAccount, action: String, recordType: String, p: Patient)` | CO5: Console, CO7: Console, CO13: Console, CO18: Console | `record(u, "book appointment", "Appointment", p)` |

### UserAccount

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ hasClinicalAccess(): Boolean` | CO7: Console | `clinical := hasClinicalAccess()` |
| `+ canIssueReferrals(): Boolean` | CO14: Console | `allowed := canIssueReferrals()` |
| `+ getProfessional(): HealthcareProfessional [0..1]` | CO7: Console, CO14: Console | `hp := getProfessional()` |

### Person

No operations: only referenced (target of navigable associations); no message is sent to it in the diagrams.

### Patient

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ getSummary(): String` | CO2: Console, CO14: Console | `summary := getSummary()` |
| `+ getClinicalRecord(): ClinicalRecord` | CO7: Console | `cr := getClinicalRecord()` |
| `+ findValidReferral(ms: MedicalSpecialty, hp: HealthcareProfessional): Referral [0..1]` | CO4: Appointment | `r := findValidReferral(ms, hp)` |
| `+ addReferral(r: Referral)` | CO14: Referrals | `addReferral(r)` |

### HealthcareProfessional

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ getBookableSlots(fromDate: Date, toDate: Date, st: ServiceType, a: Appointment): TimeSlot [*]` | CO3: Console | `slots := getBookableSlots(fromDate, toDate, st, a)` |
| `+ hasAvailableSlot(timeFrame: String): Boolean` | CO12: Console | `found := hasAvailableSlot(preferredTimeFrame)` |
| `+ provides(st: ServiceType): Boolean` | CO4: Appointment | `offers := provides(st)` |
| `+ practisesAtClinic(): Boolean` | CO14: Console, CO15: MedicalSpecialty, CO16: Console | `atClinic := practisesAtClinic()` |

### ClinicLocation

No operations: only referenced (target of navigable associations); no message is sent to it in the diagrams.

### MedicalSpecialty

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ getClinicProfessionals(): HealthcareProfessional [*]` | CO15: Console | `pros := getClinicProfessionals()` |

### Appointment

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ «create» Appointment(p: Patient, u: UserAccount)` | CO2: Appointments, CO12: Appointments | `create(p, u)` |
| `+ setStatus(s: AppointmentStatus)` | CO2: Appointment, CO5: Appointment, CO12: Appointment, CO13: Consultation | `setStatus(Requested)` |
| `+ setServiceType(st: ServiceType)` | CO3: Console, CO12: Appointments | `setServiceType(st)` |
| `+ setPreferredTimeFrame(fromDate: Date, toDate: Date)` | CO3: Console | `setPreferredTimeFrame(fromDate, toDate)` |
| `+ setPreferredTimeFrame(timeFrame: String)` | CO12: Appointments | `setPreferredTimeFrame(preferredTimeFrame)` |
| `+ selectSlot(ts: TimeSlot): Boolean` | CO4: Console | `ok := selectSlot(ts)` |
| `+ confirm(n: String)` | CO5: Console | `confirm(n)` |
| `+ getPatient(): Patient` | CO4a: TimeSlot, CO5: Console, CO7: Console | `p := getPatient()` |
| `+ isScheduledWith(hp: HealthcareProfessional): Boolean` | CO7: Console | `mine := isScheduledWith(hp)` |
| `+ isArrived(): Boolean` | CO7: Console | `arrived := isArrived()` |
| `- clearSelection()` | CO4: Appointment | `clearSelection()` |
| `- setTimeSlot(ts: TimeSlot)` | CO4: Appointment | `setTimeSlot(ts)` |
| `- setProfessional(hp: HealthcareProfessional)` | CO4: Appointment | `setProfessional(hp)` |
| `- setDate(d: Date)` | CO4: Appointment | `setDate(d)` |
| `- setStartTime(t: Time)` | CO4: Appointment | `setStartTime(t)` |
| `- setReferral(r: Referral)` | CO4: Appointment | `setReferral(r)` |
| `- addResource(R: Resource)` | CO4: Appointment | `addResource(R)` |
| `- setAppointmentNumber(n: String)` | CO5: Appointment | `setAppointmentNumber(n)` |

### TimeSlot

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ isBookable(st: ServiceType, a: Appointment): Boolean` | CO4: Appointment, CO4a: CO4a entry point (ref from CO3, CO4) | `bookable := isBookable(st, a)` |
| `+ getProfessional(): HealthcareProfessional` | CO4: Appointment | `hp := getProfessional()` |
| `+ getDate(): Date` | CO4: Appointment | `d := getDate()` |
| `+ getStartTime(): Time` | CO4: Appointment | `t := getStartTime()` |
| `+ setAvailable(b: Boolean)` | CO4: Appointment | `setAvailable(true)` |
| `+ setOccupant(a: Appointment [0..1])` | CO4: Appointment | `setOccupant(null)` |
| `+ getOffer(): WaitingListEntry [0..1]` | CO5: Appointment | `w := getOffer()` |
| `+ clearOffer()` | CO5: WaitingListEntry | `clearOffer()` |

### ServiceType

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ getProviders(): HealthcareProfessional [*]` | CO3: Console, CO12: Console | `providers := getProviders()` |
| `+ getRequirements(): ResourceRequirement [*]` | CO4: Appointment, CO4a: TimeSlot | `reqs := getRequirements()` |
| `+ getSpecialty(): MedicalSpecialty [0..1]` | CO4: Appointment | `ms := getSpecialty()` |
| `+ getDurationMinutes(): Integer` | CO4a: TimeSlot | `dur := getDurationMinutes()` |
| `+ requiresReferral(): Boolean` | CO4: Appointment (guard `opt st.requiresReferral`; see section 5, item 4) | — |

### ResourceRequirement

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ getResource(): Resource` | CO4: Appointment, CO4a: TimeSlot | `R := getResource()` |
| `+ getQuantityNeeded(): Integer` | CO4a: TimeSlot | `q := getQuantityNeeded()` |

### Resource

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ hasCapacity(ts: TimeSlot, q: Integer, a: Appointment): Boolean` | CO4a: TimeSlot | `enough := hasCapacity(ts, q, a)` |
| `+ allocate(a: Appointment)` | CO4: Appointment | `allocate(a)` |
| `+ deallocate(a: Appointment)` | CO4: Appointment | `deallocate(a)` |
| `- quantityInUse(ts: TimeSlot, a: Appointment): Integer` | CO4a: Resource (message drawn to the `:Appointment` multiobject; see section 5, item 1) | `used := quantityInUse(ts, a)` |

### WaitingListEntry

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ concerns(p: Patient): Boolean` | CO4a: TimeSlot | `forPatient := concerns(p)` |
| `+ accept(a: Appointment)` | CO5: Appointment | `accept(a)` |
| `- setOfferedSlot(ts: TimeSlot [0..1])` | CO5: WaitingListEntry | `setOfferedSlot(null)` |
| `- setAppointment(a: Appointment)` | CO5: WaitingListEntry | `setAppointment(a)` |
| `- setStatus(s: WaitlistStatus)` | CO5: WaitingListEntry | `setStatus(Scheduled)` |

### Referral

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ «create» Referral(p: Patient, hp: HealthcareProfessional)` | CO14: Referrals | `create(p, hp)` |
| `+ setStatus(s: ReferralStatus)` | CO5: Appointment, CO14: Referral, CO18: Referral | `setStatus(Scheduled)` |
| `+ setRequestedSpecialty(ms: MedicalSpecialty)` | CO15: Console | `setRequestedSpecialty(ms)` |
| `+ setRequestedProfessional(hp: HealthcareProfessional)` | CO16: Console | `setRequestedProfessional(hp)` |
| `+ setClinicalReason(reason: String)` | CO17: Console | `setClinicalReason(clinicalReason)` |
| `+ setPriority(priority: Priority)` | CO17: Console | `setPriority(priority)` |
| `+ isComplete(): Boolean` | CO18: Console | `complete := isComplete()` |
| `+ submit(n: String)` | CO18: Console | `submit(n)` |
| `+ getPatient(): Patient` | CO18: Console | `p := getPatient()` |
| `+ addAppointment(a: Appointment)` | CO4: Appointment | `addAppointment(a)` |
| `+ removeAppointment(a: Appointment)` | CO4: Appointment | `removeAppointment(a)` |
| `- setDateIssued(d: Date)` | CO14: Referral | `setDateIssued(today)` |
| `- setReferralNumber(n: String)` | CO18: Referral | `setReferralNumber(n)` |

### ClinicalRecord

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ startConsultation(a: Appointment): Consultation` | CO7: Console | `c := startConsultation(a)` |
| `+ getHistory(): Consultation [*]` | CO7: Console | `history := getHistory()` |

### Consultation

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ «create» Consultation(a: Appointment)` | CO7: ClinicalRecord | `create(a)` |
| `+ setObservations(observations: String)` | CO8: Console | `setObservations(observations)` |
| `+ setTreatmentRecommendations(treatment: String)` | CO10: Console | `setTreatmentRecommendations(treatment)` |
| `+ setFollowUpRequirements(followUp: String)` | CO10: Console | `setFollowUpRequirements(followUp)` |
| `+ addDiagnosis(code: String, description: String)` | CO9: Console | `addDiagnosis(code, description)` |
| `+ addPrescriptionItem(m: Medication, dosage: String, frequency: String, duration: String, quantity: Integer)` | CO11: Console | `addPrescriptionItem(m, dosage, frequency, duration, quantity)` |
| `+ addFollowUp(fa: Appointment)` | CO12: Console | `addFollowUp(fa)` |
| `+ getPatient(): Patient` | CO12: Console, CO13: Console | `p := getPatient()` |
| `+ end(): Boolean` | CO13: Console | `ended := end()` |
| `- setDate(d: Date)` | CO7: Consultation | `setDate(today)` |
| `- setStartTime(t: Time)` | CO7: Consultation | `setStartTime(now)` |
| `- setEndTime(t: Time)` | CO13: Consultation | `setEndTime(now)` |
| `- hasClinicalEntry(): Boolean` | CO13: Consultation | `entry := hasClinicalEntry()` |
| `- getPrescription(): Prescription [0..1]` | CO11: Consultation | `pr := getPrescription()` |
| `- setPrescription(pr: Prescription)` | CO11: Consultation | `setPrescription(pr)` |

### Diagnosis

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ «create» Diagnosis(code: String, description: String)` | CO9: Consultation | `create(code, description)` |

### Prescription

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ «create» Prescription(dateIssued: Date)` | CO11: Consultation | `create(today)` |
| `+ addLineItem(m: Medication, dosage: String, frequency: String, duration: String, quantity: Integer)` | CO11: Consultation | `addLineItem(m, dosage, frequency, duration, quantity)` |

### PrescriptionLineItem

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ «create» PrescriptionLineItem(m: Medication, dosage: String, frequency: String, duration: String, quantity: Integer)` | CO11: Prescription | `create(m, dosage, frequency, duration, quantity)` |

### Medication

No operations: only referenced (target of navigable associations); no message is sent to it in the diagrams.

### ActivityLogEntry

| Operation | Received from (contract: sender) | Example message |
|---|---|---|
| `+ «create» ActivityLogEntry(timestamp: DateTime, action: String, recordType: String, u: UserAccount)` | CO1: ActivityLog | `create(now, "search patients", "Patient", u)` |
| `+ «create» ActivityLogEntry(timestamp: DateTime, action: String, recordType: String, u: UserAccount, p: Patient)` | CO5: ActivityLog, CO7: ActivityLog, CO13: ActivityLog, CO18: ActivityLog | `create(now, "book appointment", "Appointment", u, p)` |

### Messages to multiobjects (container operations)

Following Lecture 13, a message to a multiobject is a message to the collection object held by the sender, not an operation of the element class. In Java each collection is a `List` field named after the role (Lecture 19).

| Contract | Sender (owner) | Collection (role) | Message |
|---|---|---|---|
| CO1 | Patients | patients: Patient [*] | `find(criteria)` |
| CO1 | ActivityLog | entries: ActivityLogEntry [*] | `add(log)` |
| CO2 | Patients | patients: Patient [*] | `find(patientId)` |
| CO2 | Appointments | appointments: Appointment [*] | `add(a)` |
| CO3 | ServiceTypes | serviceTypes: ServiceType [*] | `find(serviceTypeId)` |
| CO3 | ServiceType | providers: HealthcareProfessional [*] | `getAll()` |
| CO3 | HealthcareProfessional | slots: TimeSlot [*] | `find(fromDate, toDate)` |
| CO4 | TimeSlots | slots: TimeSlot [*] | `find(slotId)` |
| CO4 | Patient | referrals: Referral [*] | `find(Open or Scheduled,\n requests ms or hp)` |
| CO4a | Resource | allocatedTo: Appointment [*] | `quantityInUse(ts, a)` |
| CO5 | ActivityLog | entries: ActivityLogEntry [*] | `add(log)` |
| CO7 | Appointments | appointments: Appointment [*] | `find(appointmentId)` |
| CO7 | ClinicalRecord | consultations: Consultation [*] | `add(c)` |
| CO7 | ActivityLog | entries: ActivityLogEntry [*] | `add(log)` |
| CO7 | ActivityLog | entries: ActivityLogEntry [*] | `add(log)` |
| CO9 | Consultation | diagnoses: Diagnosis [*] | `add(d)` |
| CO11 | Medications | medications: Medication [*] | `find(medicationId)` |
| CO11 | Prescription | lineItems: PrescriptionLineItem [1..*] | `add(li)` |
| CO12 | ServiceTypes | serviceTypes: ServiceType [*] | `find(serviceTypeId)` |
| CO12 | Appointments | appointments: Appointment [*] | `add(fa)` |
| CO13 | ActivityLog | entries: ActivityLogEntry [*] | `add(log)` |
| CO14 | Patients | patients: Patient [*] | `find(patientId)` |
| CO14 | Referrals | referrals: Referral [*] | `add(r)` |
| CO15 | MedicalSpecialties | specialties: MedicalSpecialty [*] | `find(specialtyId)` |
| CO16 | HealthcareProfessionals | professionals: HealthcareProfessional [*] | `find(professionalId)` |
| CO18 | ActivityLog | entries: ActivityLogEntry [*] | `add(log)` |

## 3. Reference and visibility assessment

How each of the 181 messages reaches its receiver:

| Visibility | Messages | Shown in the class diagram as |
|---|---|---|
| Attribute (reference held by the sender) | 64 | navigable association end with role name |
| Attribute, to a collection (multiobject) | 26 | navigable association, multiplicity `*`, role name |
| Self (message to itself) | 26 | operation of the same class |
| Local (object returned by an earlier message) | 19 | dependency (dashed) or an existing association |
| Found message from the actor | 18 | public operation of Console |
| Creation (`create(...)`) | 13 | composition or association from creator to created class |
| Parameter | 10 | dependency (dashed) or an existing association between the two classes |
| Parameter or local, via a dependency | 5 | dependency (dashed) |

No message uses global visibility. Visibility from Console to the registries is attribute visibility; the registries are created at start-up and passed to Console (Tutorial 5, RailCompany example).

### Every relationship in the diagram

| From → To | Kind | Ends (from / to) | Used by | Why it is needed |
|---|---|---|---|---|
| Console → Patients | association, navigable → | 1 / 1 patients | CO1 CO2 CO14 | Console sends find/make/record messages to the registry |
| Console → Appointments | association, navigable → | 1 / 1 appointments | CO2 CO5 CO7 CO12 | Console sends find/make/record messages to the registry |
| Console → ServiceTypes | association, navigable → | 1 / 1 serviceTypes | CO3 CO12 | Console sends find/make/record messages to the registry |
| Console → TimeSlots | association, navigable → | 1 / 1 timeSlots | CO4 | Console sends find/make/record messages to the registry |
| Console → HealthcareProfessionals | association, navigable → | 1 / 1 professionals | CO16 | Console sends find/make/record messages to the registry |
| Console → MedicalSpecialties | association, navigable → | 1 / 1 specialties | CO15 | Console sends find/make/record messages to the registry |
| Console → Medications | association, navigable → | 1 / 1 medications | CO11 | Console sends find/make/record messages to the registry |
| Console → Referrals | association, navigable → | 1 / 1 referrals | CO14 CO18 | Console sends find/make/record messages to the registry |
| Console → ActivityLog | association, navigable → | 1 / 1 activityLog | CO1 CO5 CO7 CO13 CO18 | Console sends find/make/record messages to the registry |
| Console → UserAccount | association, navigable → | — / 0..1 currentUser | CO1 CO2 CO5 CO7 CO12 CO13 CO14 CO18 | u in every contract; Console -> u : hasClinicalAccess(), getProfessional(), canIssueReferrals(); passed as u |
| Console → Appointment | association, navigable → | — / 0..1 currentAppointment | CO2 CO3 CO4 CO5 | CO2 currentAppointment := a; CO3-CO5 messages to a |
| Console → Consultation | association, navigable → | — / 0..1 currentConsultation | CO7 CO8 CO9 CO10 CO11 CO12 CO13 | CO7 currentConsultation := c; CO8-CO13 messages to c |
| Console → Referral | association, navigable → | — / 0..1 currentReferral | CO14 CO15 CO16 CO17 CO18 | CO14 currentReferral := r; CO15-CO18 messages to r |
| Patients → Patient | aggregation, navigable → | 1 / * patients | CO1 CO2 CO14 | multiobject :Patient (find, search) |
| Appointments → Appointment | composition, navigable → | 1 / * appointments | CO2 CO7 CO12 | Creator: create(p, u) then add(a); find(appointmentId) |
| ServiceTypes → ServiceType | aggregation, navigable → | 1 / * serviceTypes | CO3 CO12 | multiobject :ServiceType (find) |
| TimeSlots → TimeSlot | aggregation, navigable → | 1 / * slots | CO4 | multiobject :TimeSlot (find) |
| HealthcareProfessionals → HealthcareProfessional | aggregation, navigable → | 1 / * professionals | CO16 | multiobject :HealthcareProfessional (find) |
| MedicalSpecialties → MedicalSpecialty | aggregation, navigable → | 1 / * specialties | CO15 | multiobject :MedicalSpecialty (find) |
| Medications → Medication | aggregation, navigable → | 1 / * medications | CO11 | multiobject :Medication (find) |
| Referrals → Referral | composition, navigable → | 1 / * referrals | CO14 | Creator: create(p, hp) then add(r) |
| ActivityLog → ActivityLogEntry | composition, navigable → | 1 / * entries | CO1 CO5 CO7 CO13 CO18 | Creator: create(now, action, recordType, u[, p]) then add(log) |
| Patient → Person | generalization |  | CO7 CO14 | domain model generalization |
| HealthcareProfessional → Person | generalization |  | CO7 CO14 | domain model generalization |
| UserAccount → Person *Identifies* | association, navigable → | 0..1 / 1 person | CO7 CO14 | getProfessional() returns the HealthcareProfessional the account identifies |
| ActivityLogEntry → UserAccount *Performed-by* | association, navigable → | * / 1 performedBy | CO1 CO5 CO7 CO13 CO18 | create(..., u) stores u |
| ActivityLogEntry → Patient *Concerns* | association, navigable → | * / 0..1 patient | CO5 CO7 CO13 CO18 | create(..., u, p) stores p |
| HealthcareProfessional → ClinicLocation *Practises-at* | association, navigable → | * / 0..* locations | CO14 CO15 CO16 | practisesAtClinic() |
| MedicalSpecialty → HealthcareProfessional *Specializes-in* | association, navigable → | 0..* / * professionals | CO15 | getClinicProfessionals(): ms -> hp : practisesAtClinic() |
| HealthcareProfessional → TimeSlot *Available-during* | association, navigable both ways | 1 professional / * slots | CO3 CO4 CO12 | hp -> :TimeSlot find(from, to), ts.isBookable (CO3), hasAvailableSlot (CO12); ts.getProfessional() (CO4) |
| HealthcareProfessional → ServiceType *Provides* | association, navigable both ways | * providers / * services | CO3 CO4 CO12 | st.getProviders() (CO3, CO12); hp.provides(st) (CO4) |
| Appointment → TimeSlot *Occupies* | association, navigable both ways | 0..1 occupant / 0..1 timeSlot | CO4 CO4a CO5 | a -> ts : setAvailable, setOccupant, getDate (CO4), getOffer (CO5); ts reads occupant == a (CO4a) |
| Appointment → Patient *Booked-for* | association, navigable → | * / 1 patient | CO2 CO4 CO4a CO5 CO7 CO12 | create(p, u); getPatient(); a -> p : findValidReferral |
| Appointment → UserAccount *Booked-by* | association, navigable → | * / 1 bookedBy | CO2 CO12 | create(p, u) stores u |
| Appointment → HealthcareProfessional *Scheduled-with* | association, navigable → | * / 0..1 professional | CO4 CO7 | setProfessional(hp) (CO4); isScheduledWith(hp) (CO7) |
| Appointment → ServiceType *Is-for* | association, navigable → | * / 1 serviceType | CO3 CO4 CO12 | setServiceType(st); a -> st : getSpecialty, getRequirements (CO4) |
| Appointment → Resource *Is-allocated* | association, navigable both ways | * allocatedTo / * resources | CO4 CO4a | a -> R : allocate/deallocate (CO4); R -> :Appointment quantityInUse (CO4a) |
| ServiceType → ResourceRequirement *Requires* | composition, navigable → | 1 / * requirements | CO4 CO4a | getRequirements(); association class ServiceType-Resource reified |
| ResourceRequirement → Resource | association, navigable → | * / 1 resource | CO4 CO4a | rr.getResource() |
| ServiceType → MedicalSpecialty *Belongs-to* | association, navigable → | * / 0..1 specialty | CO4 | st.getSpecialty() (CO4) |
| TimeSlot → WaitingListEntry *Is-offered* | association, navigable both ways | 0..1 offeredSlot / 0..1 offer | CO4a CO5 | ts.getOffer(), ts -> w : concerns (CO4a); w -> ts : clearOffer (CO5) |
| WaitingListEntry → Patient *Concerns* | association, navigable → | * / 1 patient | CO4a | concerns(p) |
| WaitingListEntry → Appointment *Results-in* | association, navigable → | 0..1 / 0..1 appointment | CO5 | setAppointment(a) (CO5) |
| Referral → Patient *Concerns* | association, navigable both ways | * referrals / 1 patient | CO4 CO14 CO18 | create(p, hp); getPatient() (CO18); p.addReferral(r) (CO14); p -> :Referral find (CO4) |
| Referral → HealthcareProfessional *Issued-by* | association, navigable → | * / 1 issuedBy | CO14 | create(p, hp) stores hp |
| Referral → MedicalSpecialty *Requests-specialty* | association, navigable → | * / 0..1 requestedSpecialty | CO15 | setRequestedSpecialty(ms) |
| Referral → HealthcareProfessional *Requests-professional* | association, navigable → | * / 0..1 requestedProfessional | CO16 | setRequestedProfessional(hp) |
| Referral → Appointment *Results-in* | association, navigable both ways | 0..1 referral / * appointments | CO4 CO5 | a -> r : addAppointment/removeAppointment (CO4), setStatus (CO5); a.setReferral(r) |
| Patient → ClinicalRecord *Has* | association, navigable → | 1 / 1 clinicalRecord | CO7 | p.getClinicalRecord() (CO7) |
| ClinicalRecord → Consultation *Contains* | composition, navigable → | 1 / * consultations | CO7 | Creator: cr -> c : create(a); add(c) |
| Consultation → Appointment *Conducted-during* | association, navigable → | 0..1 / 1 appointment | CO7 CO12 CO13 | create(a); c -> a : setStatus(Completed) (CO13); getPatient() |
| Consultation → Diagnosis *Records* | composition, navigable → | 1 / * diagnoses | CO9 | Creator: c -> d : create(code, description); add(d) |
| Consultation → Prescription *Issues* | association, navigable → | 1 / 0..1 prescription | CO11 | Creator: c -> pr : create(today); setPrescription(pr) |
| Consultation → Appointment *Requests-follow-up* | association, navigable → | 0..1 / * followUps | CO12 | addFollowUp(fa) (CO12) |
| Prescription → PrescriptionLineItem *Contains* | composition, navigable → | 1 / 1..* lineItems | CO11 | Creator: pr -> li : create(...); add(li) |
| PrescriptionLineItem → Medication *Prescribes* | association, navigable → | * / 1 medication | CO11 | create(m, ...) stores m |
| Console → Patient | dependency (non-attribute visibility) |  | CO2 CO7 CO14 | local p: getSummary() (CO2, CO14), getClinicalRecord() (CO7) |
| Console → HealthcareProfessional | dependency (non-attribute visibility) |  | CO3 CO12 CO14 CO16 | local hp: getBookableSlots (CO3), hasAvailableSlot (CO12), practisesAtClinic (CO14, CO16) |
| Console → ServiceType | dependency (non-attribute visibility) |  | CO3 CO12 | local st: getProviders() (CO3, CO12) |
| Console → MedicalSpecialty | dependency (non-attribute visibility) |  | CO15 | local ms: getClinicProfessionals() (CO15) |
| Console → ClinicalRecord | dependency (non-attribute visibility) |  | CO7 | local cr: startConsultation(a), getHistory() (CO7) |
| Referrals → Patient | dependency (non-attribute visibility) |  | CO14 | parameter p: addReferral(r) (CO14) |
| TimeSlot → ServiceType | dependency (non-attribute visibility) |  | CO4a | parameter st: getDurationMinutes(), getRequirements() (CO4a) |
| TimeSlot → ResourceRequirement | dependency (non-attribute visibility) |  | CO4a | local rr: getResource(), getQuantityNeeded() (CO4a) |
| TimeSlot → Resource | dependency (non-attribute visibility) |  | CO4a | local R: hasCapacity(ts, q, a) (CO4a) |
| Appointment → ResourceRequirement | dependency (non-attribute visibility) |  | CO4 | local rr: getResource() (CO4) |
| Appointment → WaitingListEntry | dependency (non-attribute visibility) |  | CO5 | local w := ts.getOffer(): accept(a) (CO5) |
| Console → Appointment | dependency (non-attribute visibility) |  | CO7 | local a := Appointments.find(appointmentId): isScheduledWith, getPatient, isArrived (CO7); a is not currentAppointment |

## 4. Comparison with the conceptual domain model

### Associations: multiplicities

Each association carried over from the domain model was compared, end by end, with `domain-model-iterationII.puml` by script.

| Design association | Name | Multiplicity in design (from / to) | In domain model | Result |
|---|---|---|---|---|
| UserAccount -> Person | Identifies | 0..1 / 1 | 0..1 / 1 | same |
| ActivityLogEntry -> UserAccount | Performed-by | * / 1 | * / 1 | same |
| ActivityLogEntry -> Patient | Concerns | * / 0..1 | * / 0..1 | same |
| HealthcareProfessional -> ClinicLocation | Practises-at | * / 0..* | * / 0..* | same |
| MedicalSpecialty -> HealthcareProfessional | Specializes-in | 0..* / * | 0..* / * | same |
| HealthcareProfessional -> TimeSlot | Available-during | 1 / * | 1 / * | same |
| HealthcareProfessional -> ServiceType | Provides | * / * | * / * | same |
| Appointment -> TimeSlot | Occupies | 0..1 / 0..1 | 0..1 / 0..1 | same |
| Appointment -> Patient | Booked-for | * / 1 | * / 1 | same |
| Appointment -> UserAccount | Booked-by | * / 1 | * / 1 | same |
| Appointment -> HealthcareProfessional | Scheduled-with | * / 0..1 | * / 0..1 | same |
| Appointment -> ServiceType | Is-for | * / 1 | * / 1 | same |
| Appointment -> Resource | Is-allocated | * / * | * / * | same |
| ServiceType -> ResourceRequirement | Requires | 1 / * | ServiceType * -- * Resource (association class ResourceRequirement) | reified |
| ResourceRequirement -> Resource | (reified Requires) | * / 1 | association class ServiceType * -- * Resource | reified |
| ServiceType -> MedicalSpecialty | Belongs-to | * / 0..1 | * / 0..1 | same |
| TimeSlot -> WaitingListEntry | Is-offered | 0..1 / 0..1 | 0..1 / 0..1 | same |
| WaitingListEntry -> Patient | Concerns | * / 1 | * / 1 | same |
| WaitingListEntry -> Appointment | Results-in | 0..1 / 0..1 | 0..1 / 0..1 | same |
| Referral -> Patient | Concerns | * / 1 | * / 1 | same |
| Referral -> HealthcareProfessional | Issued-by | * / 1 | * / 1 | same |
| Referral -> MedicalSpecialty | Requests-specialty | * / 0..1 | * / 0..1 | same |
| Referral -> HealthcareProfessional | Requests-professional | * / 0..1 | * / 0..1 | same |
| Referral -> Appointment | Results-in | 0..1 / * | 0..1 / * | same |
| Patient -> ClinicalRecord | Has | 1 / 1 | 1 / 1 | same |
| ClinicalRecord -> Consultation | Contains | 1 / * | 1 / * | same |
| Consultation -> Appointment | Conducted-during | 0..1 / 1 | 0..1 / 1 | same |
| Consultation -> Diagnosis | Records | 1 / * | 1 / * | same |
| Consultation -> Prescription | Issues | 1 / 0..1 | 1 / 0..1 | same |
| Consultation -> Appointment | Requests-follow-up | 0..1 / * | 0..1 / * | same |
| Prescription -> PrescriptionLineItem | Contains | 1 / 1..* | 1 / 1..* | same |
| PrescriptionLineItem -> Medication | Prescribes | * / 1 | * / 1 | same |

30 associations have the same multiplicities as the domain model; the 2 ResourceRequirement rows are the association class reified as a class (see below). No multiplicity was changed.

### Classes

| Domain model element | In the design class diagram | Reason |
|---|---|---|
| Patient, HealthcareProfessional, UserAccount, Appointment, TimeSlot, ServiceType, Resource, WaitingListEntry, Referral, MedicalSpecialty, ClinicalRecord, Consultation, Diagnosis, Prescription, PrescriptionLineItem, Medication, ActivityLogEntry | Included as software classes | Each one sends or receives a message in UC1–UC3. |
| ResourceRequirement (association class on *Requires*) | Included as an ordinary class: `ServiceType *--> ResourceRequirement --> Resource` | An association class cannot be navigated directly in code. `st.getRequirements()`, `rr.getResource()` and `rr.getQuantityNeeded()` in CO4 and CO4a need it as an object. |
| Person (abstract) | Included with its attributes as `#` (protected) | *Identifies* links UserAccount to Person, and `u.getProfessional()` (CO7, CO14) must reach the HealthcareProfessional. Subclasses inherit the protected name and phone fields (Lecture 19, Vehicle/Car example). |
| ClinicLocation | Included with no operations | `hp.practisesAtClinic()` (CO14, CO15, CO16) reads the *Practises-at* links. |
| Clinic, AdministrativeStaff, Physician, Nurse, ExternalPhysician, InsuranceCoverage and subclasses, Invoice, InvoiceLineItem, Payment, Availability | Not included | No message in the UC1–UC3 interaction diagrams reaches them. This is a partial diagram. |

### Attributes

- **Kept as in the domain model:** names, types and `[0..1]` multiplicities of the attributes shown. Every attribute is private (`-`), except Person's, which are protected (`#`).
- **Added (design-level identifiers):**
  - `TimeSlot.slotId : String` and `ServiceType.serviceTypeId : String`.
  - The contracts look these up "matching slotId" and "matching serviceTypeId", but the domain model gives these two classes no identifying attribute.
  - The other lookups use attributes that already exist: `Patient.patientId`, `Appointment.appointmentNumber`, `HealthcareProfessional.licenceNumber`, `MedicalSpecialty.code` and `Medication.din`.
- **Omitted because no UC1–UC3 message needs them:**
  - `ServiceType.ramqCovered` and `fee`.
  - `WaitingListEntry.fromDate` and `toDate`.
  - `ClinicLocation.phone`.
- **Reference attributes:**
  - For domain classes, references are shown as role names at the navigable association end (Lecture 13: "attributes as association types").
  - Console lists its 13 references in its attribute compartment as well as on the association lines, because they are its whole state (Lecture 13, "thorough and unambiguous" form).

### Associations and navigability

- **Associations kept:** 30 domain associations are kept unchanged, with the domain model's multiplicities, as the table above shows. The *Requires* association class is reified into two associations.
- **Navigability:** each association is navigable in the direction(s) where a message is sent, a created object is stored, or a guard reads the other object (Lecture 13: "A sends a message to B, A creates B, A must keep a connection to B").
- **Navigable both ways (7):**
  - *Available-during*: hp finds its slots; `ts.getProfessional()`.
  - *Provides*: `st.getProviders()`; `hp.provides(st)`.
  - *Occupies*: a sets its slot; ts checks `occupant == a`.
  - *Is-allocated*: `R.allocate(a)`; a keeps `resources`.
  - *Is-offered*: `ts.getOffer()`; `w.clearOffer` on its offered slot.
  - Referral *Concerns* Patient: `r.getPatient()`; `p.addReferral(r)` and `p.findValidReferral`.
  - Referral *Results-in* Appointment: `a.setReferral(r)`; `r.addAppointment(a)`.
- **Dependencies (12, dashed):** non-attribute visibility, where the sender reaches the receiver only through a parameter or a local variable and no association is used for it in that contract (Lecture 13, slide 12).
- **Associations left out:** domain associations that no UC1–UC3 message uses. Examples: *Located-at*, *Covered-by*, *Recorded-by*, *Based-on*, *Requests-service*, *Added-by*, *Bills*, *Generates*. They stay in the domain model and will be added when the interaction diagrams that use them exist.

## 5. Unresolved inconsistencies

None of these was fixed by changing an existing artifact. Where a correction would be needed, the smallest one is described and left for the team to decide.

1. **`quantityInUse(ts, a)` in CO4a.**
   - **What the diagram shows:** `R` sends `quantityInUse(ts, a)` to the `:Appointment` multiobject.
   - **Receiving object and owner:** the multiobject is the collection of Appointments that `R` is *Is-allocated* to. In this design that is `Resource.allocatedTo`, so the owning software class is **Resource**.
   - **Why it can't stay a container message:** a Java `List` has no such operation. The design therefore realises it as the private helper `- Resource.quantityInUse(ts: TimeSlot, a: Appointment): Integer`, which iterates over `allocatedTo`.
   - **Minimal diagram correction (not applied):** in CO4a, replace `R -> :Appointment : quantityInUse(ts, a)` with `R -> R : quantityInUse(ts, a)` plus a loop over `:Appointment`.
   - **What the helper needs that isn't drawn:** the note on CO4a describes the rule. For each other active Appointment whose slot overlaps `ts`, it adds the `quantityNeeded` of `R` for that Appointment's ServiceType. Doing this requires reading each Appointment's status, TimeSlot and ServiceType, and the TimeSlot's times. No drawn message does this, so the class diagram adds no getters for it (see item 2).
2. **Container searches with conditions.** Four multiobject messages filter on attributes of the elements without drawing the reads:
   - `p -> :Referral find(Open or Scheduled, requests ms or hp)` (CO4);
   - `hp -> :TimeSlot find(fromDate, toDate)` (CO3);
   - `Patients -> :Patient find(criteria)` (CO1);
   - `hp.hasAvailableSlot(timeFrame)` reads `isAvailable` of its slots (CO12).

   In code these are loops inside the owner's method. They will need simple read accessors, such as `Referral.getStatus()`, `TimeSlot.isAvailable()` or `Appointment.getStatus()`. These were **not** added to the diagram, because no interaction diagram sends them. If the team wants them visible, the minimal fix is to add them as public getters, with a note that they support the container searches.
3. **Two signatures of `setPreferredTimeFrame`.**
   - CO3 sends `setPreferredTimeFrame(fromDate, toDate)`, and CO12 sends `setPreferredTimeFrame(preferredTimeFrame)` with a String.
   - The domain model attribute is `preferredTimeFrame : String`, so the design keeps both as overloads. The (Date, Date) version stores the range as text.
   - `HealthcareProfessional.hasAvailableSlot(timeFrame : String)` must likewise interpret a String.
   - A `DateRange` type would remove the ambiguity, but it would change the contracts' parameter types, so it was not introduced.
4. **`ServiceType.requiresReferral(): Boolean` is the one operation without a drawn message.**
   - In CO4, Appointment evaluates the guard `opt st.requiresReferral` on another object, so it needs this read accessor.
   - The name mirrors the guard text. Java allows a field and a method to share a name.
5. **CO7 uses a local Appointment.** In CO7, `a := Appointments.find(appointmentId)` is a local variable of `Console.startConsultation`. It is not `currentAppointment`, which belongs to the UC1 booking. A dependency `Console ..> Appointment` is therefore drawn next to the `currentAppointment` association.
6. **Multiplicity 1 versus unfinished objects.**
   - `Appointment.serviceType [1]`, `Consultation` fields and `Referral.referralNumber : String` follow the domain model. During an unfinished UC1 booking or a Draft referral, these references and values are still empty.
   - The domain model's note covers Appointment ("multiplicities apply once it is saved").
   - `Referral.referralNumber` is not marked `[0..1]` in the domain model, although `Appointment.appointmentNumber` was changed to `[0..1]` for the same reason. This is flagged for the team and not changed here.
7. **Private setters on WaitingListEntry.** `setOfferedSlot`, `setAppointment` and `setStatus` are private because, within UC1–UC3, only `w.accept(a)` uses them (CO5). The waiting-list operations CO28 to CO36 have no interaction diagrams yet, and they will probably need public operations on WaitingListEntry.
8. **Return values of system operations are a design choice.**
   - The SSDs return user-facing text, such as "patient summary and history", "appointment details" or "consultation summary". Console formats that text from objects it already holds, such as `p.getSummary()` and `cr.getHistory()`. CO13's "consultation summary" is formatted from `c` with no extra message.
   - **Corrected:** three Console operations now return `String`:
     - `selectTimeSlot(slotId : String) : String` (CO4) returns the appointment details, or "slot no longer available" / "referral required".
     - `requestFollowUp(serviceTypeId : String, preferredTimeFrame : String) : String` (CO12) returns the requested follow-up appointment, or "no availability".
     - `endConsultation() : String` (CO13) returns the consultation summary, or "at least one clinical entry is required".
   - Earlier they returned `Boolean`, which could not carry the response the SSDs and interaction diagrams show for both the success and the failure flow. Their names and parameters are unchanged, and no domain behaviour changed.
   - **Unchanged:** the internal domain operations `Appointment.selectSlot(ts : TimeSlot) : Boolean` and `Consultation.end() : Boolean` keep their Boolean results. Console reads that result and builds the String response.
   - The other Console operations keep the return types listed in section 2.
9. **Registry ownership.**
   - Composition is used where the registry is the Creator and owns the objects' lifetime: Appointments, Referrals and ActivityLog.
   - Aggregation is used where the objects are shared with other references and the registry only finds them: Patients, ServiceTypes, TimeSlots, HealthcareProfessionals, MedicalSpecialties and Medications.
   - CO2, CO12 and CO14 add new objects to registries, which has no matching contract postcondition. This was already reported in TRACEABILITY.md (item 1).
10. **Integrated diagram density.**
    - The authoritative diagram has 30 classes and 68 relationships. Several role names overlap at the top edges of HealthcareProfessional and ServiceType, which have the most incoming associations.
    - The SVG can be zoomed, and the three use-case views in `views/` show the same classes, operations and relationships, split by use case.
    - Section 3 lists every association end, so no information exists only in an overlapped label.

## 6. Existing artifacts unchanged

- The 18 interaction diagrams (`.puml`, `.png`, `.svg`), `Interaction-diagrams/README.md` and `TRACEABILITY.md` were only read. The validation script read them from the shared project folder, and those files are byte-identical to the copies produced in the interaction-diagram task.
- No file under `soen342-iter2/` other than the new `Design-class-diagram/` folder was written. A search for files modified after this task started returned nothing. That covers the use cases, SSDs, `System-operations.md`, `Operation-contracts.md`, the domain model and the use-case diagram.
- CO12 keeps its exact name: `Console.requestFollowUp(serviceTypeId: String, preferredTimeFrame: String)`.
- No business rule, appointment status, referral workflow, persistence layer or architectural framework was added.
- Nothing was committed or pushed to Git.
