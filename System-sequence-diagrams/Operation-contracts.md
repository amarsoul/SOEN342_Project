## Operation Contracts

In the contracts, `u` is the UserAccount of the authenticated user who performs the operation. Postconditions are stated in terms of the domain model and are of three kinds: instance creation or deletion, attribute modification, and association formed or broken.

---

### UC1 Book Appointment

#### Contract CO1: searchPatients
- **Operation:** `searchPatients(criteria: SearchCriteria)`
- **Cross References:** Use Case: Book Appointment (step 2), Search Patient
- **Preconditions:** `u` is authenticated with a role allowed to search patients (AdministrativeStaff or HealthcareProfessional).
- **Postconditions:**
  - An ActivityLogEntry instance `log` was created. *(instance creation)*
  - `log.timestamp` was set to the current date and time, `log.action` to "search patients" and `log.recordType` to "Patient". *(attribute modification)*
  - `log` was associated with `u`. *(association formed)*
  - (No domain object is otherwise changed; the matching Patients, found by insurance number, date of birth, phone number or partial name, are returned.)

#### Contract CO2: startNewBooking
- **Operation:** `startNewBooking(patientId: PatientID)`
- **Cross References:** Use Case: Book Appointment (step 4)
- **Preconditions:** `u` is authenticated as AdministrativeStaff or as the Patient identified by `patientId`. A Patient with `patientId` exists.
- **Postconditions:**
  - An Appointment instance `a` was created. *(instance creation)*
  - `a.status` was set to Requested. *(attribute modification)*
  - `a` was associated with the Patient whose `patientId` matches `patientId` (Booked-for). *(association formed)*
  - `a` was associated with `u` (Booked-by). *(association formed)*

#### Contract CO3: specifyService
- **Operation:** `specifyService(serviceTypeId: String, fromDate: Date, toDate: Date)`
- **Cross References:** Use Case: Book Appointment (steps 6-7)
- **Preconditions:** A booking is underway (an Appointment `a` with status Requested exists for this session). A ServiceType matching `serviceTypeId` exists. `fromDate` ≤ `toDate`.
- **Postconditions:**
  - `a` was associated with the ServiceType matching `serviceTypeId` (Is-for). *(association formed)*
  - (The TimeSlots between `fromDate` and `toDate` with `isAvailable` = true, belonging to HealthcareProfessionals who Provide the ServiceType, are returned.)

#### Contract CO4: selectTimeSlot
- **Operation:** `selectTimeSlot(slotId: String)`
- **Cross References:** Use Case: Book Appointment (steps 8-9)
- **Preconditions:** A booking `a` is underway and associated with a ServiceType `st`. A TimeSlot `ts` matching `slotId` exists and `ts.isAvailable` is true. If `st.requiresReferral` is true, the patient of `a` has a Referral `r` with status Open that requests `st`'s MedicalSpecialty.
- **Postconditions:**
  - `a` was associated with `ts` (Occupies). *(association formed)*
  - `a` was associated with the HealthcareProfessional who is Available-during `ts` (Scheduled-with). *(association formed)*
  - `a.date` was set to `ts.date` and `a.startTime` to `ts.startTime`. *(attribute modification)*
  - If `st.requiresReferral` is true, `r` was associated with `a` (Results-in). *(association formed)*
- **Note:** If the referral precondition does not hold, the system reports "referral required" and none of the postconditions occur (UC1 extension 9b, failure SSD).

#### Contract CO5: confirmAppointment
- **Operation:** `confirmAppointment()`
- **Cross References:** Use Case: Book Appointment (steps 10-11)
- **Preconditions:** A booking `a` is underway and is associated with a TimeSlot `ts`, a HealthcareProfessional and a ServiceType. `ts.isAvailable` is still true.
- **Postconditions:**
  - `a.status` was set to Confirmed. *(attribute modification)*
  - `a.appointmentNumber` was set to a new unique number. *(attribute modification)*
  - `ts.isAvailable` was set to false. *(attribute modification)*
  - If `a` is associated with a Referral `r`, `r.status` was set to Scheduled. *(attribute modification)*
  - An ActivityLogEntry `log` was created, its `timestamp`, `action` ("book appointment") and `recordType` ("Appointment") were set, and `log` was associated with `u` and with the Patient of `a`. *(instance creation, attribute modification, association formed)*

---

### UC2 Record Consultation

#### Contract CO6: startConsultation
- **Operation:** `startConsultation(appointmentId: String)`
- **Cross References:** Use Case: Record Consultation (steps 1-2)
- **Preconditions:** An Appointment `a` with `appointmentNumber` = `appointmentId` exists and `a.status` is Arrived. `u` identifies the HealthcareProfessional with whom `a` is Scheduled-with.
- **Postconditions:**
  - A Consultation instance `c` was created. *(instance creation)*
  - `c.date` and `c.startTime` were set to the current date and time. *(attribute modification)*
  - `c` was associated with `a` (Conducted-during). *(association formed)*
  - `c` was associated with the ClinicalRecord of `a`'s Patient (Contains). *(association formed)*
  - An ActivityLogEntry `log` was created with `action` "open clinical record" and `recordType` "ClinicalRecord", and associated with `u` and with `a`'s Patient. *(instance creation, attribute modification, association formed)*
- **Note:** If `u` is not authorized (UC2 extension 2a, failure SSD), only an ActivityLogEntry with `action` "access denied" is created and associated with `u` and the Patient; no Consultation is created.

#### Contract CO7: recordObservations
- **Operation:** `recordObservations(observations: String)`
- **Cross References:** Use Case: Record Consultation (step 3)
- **Preconditions:** A consultation `c` is underway.
- **Postconditions:**
  - `c.observations` was set to `observations`. *(attribute modification)*

#### Contract CO8: addDiagnosis
- **Operation:** `addDiagnosis(code: String, description: String)`
- **Cross References:** Use Case: Record Consultation (step 4)
- **Preconditions:** A consultation `c` is underway.
- **Postconditions:**
  - A Diagnosis instance `d` was created. *(instance creation)*
  - `d.code` was set to `code` and `d.description` to `description`. *(attribute modification)*
  - `d` was associated with `c` (Records). *(association formed)*

#### Contract CO9: recordRecommendations
- **Operation:** `recordRecommendations(treatment: String, followUp: String)`
- **Cross References:** Use Case: Record Consultation (step 5, extension 5b)
- **Preconditions:** A consultation `c` is underway.
- **Postconditions:**
  - `c.treatmentRecommendations` was set to `treatment`. *(attribute modification)*
  - `c.followUpRequirements` was set to `followUp`. *(attribute modification)*

#### Contract CO10: addPrescriptionItem
- **Operation:** `addPrescriptionItem(medicationId: String, dosage: String, frequency: String, duration: String, quantity: Integer)`
- **Cross References:** Use Case: Record Consultation (extension 5a), Issue Prescription
- **Preconditions:** A consultation `c` is underway. A Medication with `din` = `medicationId` exists.
- **Postconditions:**
  - If `c` was not yet associated with a Prescription, a Prescription instance `p` was created, `p.dateIssued` was set to the current date, and `p` was associated with `c` (Issues). *(instance creation, attribute modification, association formed)*
  - A PrescriptionLineItem instance `pli` was created. *(instance creation)*
  - `pli.dosage`, `pli.frequency`, `pli.duration` and `pli.quantity` were set to `dosage`, `frequency`, `duration` and `quantity`. *(attribute modification)*
  - `pli` was associated with `p` (Contains). *(association formed)*
  - `pli` was associated with the Medication whose `din` matches `medicationId` (Prescribes). *(association formed)*

#### Contract CO11: endConsultation
- **Operation:** `endConsultation()`
- **Cross References:** Use Case: Record Consultation (steps 6-7)
- **Preconditions:** A consultation `c` is underway and at least one Diagnosis is associated with `c`.
- **Postconditions:**
  - `c.endTime` was set to the current time. *(attribute modification)*
  - The Appointment `a` that `c` is Conducted-during had `a.status` set to Completed. *(attribute modification)*
  - An ActivityLogEntry `log` was created with `action` "record consultation" and `recordType` "Consultation", and associated with `u` and with `a`'s Patient. *(instance creation, attribute modification, association formed)*

---

### UC3 Issue Referral

#### Contract CO12: startReferral
- **Operation:** `startReferral(patientId: PatientID)`
- **Cross References:** Use Case: Issue Referral (steps 1-2)
- **Preconditions:** `u` identifies a HealthcareProfessional `hp` (or, in extension 1a, AdministrativeStaff entering on behalf of an ExternalPhysician `hp`). A Patient with `patientId` exists.
- **Postconditions:**
  - A Referral instance `r` was created. *(instance creation)*
  - `r.dateIssued` was set to the current date and `r.status` to Draft. *(attribute modification)*
  - `r` was associated with the Patient matching `patientId` (Concerns). *(association formed)*
  - `r` was associated with `hp` (Issued-by). *(association formed)*

#### Contract CO13: setRequestedSpecialty
- **Operation:** `setRequestedSpecialty(specialtyId: String)`
- **Cross References:** Use Case: Issue Referral (steps 3-4)
- **Preconditions:** A referral `r` is underway. A MedicalSpecialty `ms` with `code` = `specialtyId` exists, and at least one Physician who Specializes-in `ms` Practises-at a ClinicLocation.
- **Postconditions:**
  - `r` was associated with `ms` (Requests-specialty). *(association formed)*
- **Note:** If no clinic professional practises the specialty (UC3 extension 4a, failure SSD), the system reports "specialty not offered" and `r` is not associated with `ms`.

#### Contract CO14: enterReferralDetails
- **Operation:** `enterReferralDetails(clinicalReason: String, priority: Priority)`
- **Cross References:** Use Case: Issue Referral (step 5)
- **Preconditions:** A referral `r` is underway.
- **Postconditions:**
  - `r.clinicalReason` was set to `clinicalReason`. *(attribute modification)*
  - `r.priority` was set to `priority`. *(attribute modification)*

#### Contract CO15: submitReferral
- **Operation:** `submitReferral()`
- **Cross References:** Use Case: Issue Referral (steps 6-7)
- **Preconditions:** A referral `r` is underway, associated with a MedicalSpecialty (or a requested HealthcareProfessional), and `r.clinicalReason` and `r.priority` are set.
- **Postconditions:**
  - `r.status` was set to Open. *(attribute modification)*
  - `r.referralNumber` was set to a new unique number. *(attribute modification)*
  - An ActivityLogEntry `log` was created with `action` "issue referral" and `recordType` "Referral", and associated with `u` and with `r`'s Patient. *(instance creation, attribute modification, association formed)*
