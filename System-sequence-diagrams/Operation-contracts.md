## Operation Contracts

In the contracts, `u` is the UserAccount of the authenticated user who performs the operation. Postconditions are stated in terms of the domain model and are of three kinds: instance creation or deletion, attribute modification, and association formed or broken. Preconditions only state what is assumed true when the operation is invoked. Failure outcomes (failure SSDs and use case extensions) are described in the **Note** of each contract.

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
  - (No domain object is otherwise changed; the matching Patients, found by insurance number, date of birth, phone number or partial name, are returned. If none match, the system indicates that no patient was found, extension 3a.)

#### Contract CO2: startNewBooking
- **Operation:** `startNewBooking(patientId: PatientID)`
- **Cross References:** Use Case: Book Appointment (step 4)
- **Preconditions:** `u` is authenticated as AdministrativeStaff. A Patient with `patientId` exists.
- **Postconditions:**
  - An Appointment instance `a` was created. *(instance creation)*
  - `a.status` was set to Requested. *(attribute modification)*
  - `a` was associated with the Patient whose `patientId` matches `patientId` (Booked-for). *(association formed)*
  - `a` was associated with `u` (Booked-by). *(association formed)*

#### Contract CO3: specifyService
- **Operation:** `specifyService(serviceTypeId: String, fromDate: Date, toDate: Date)`
- **Cross References:** Use Case: Book Appointment (steps 6-7), Verify Availability
- **Preconditions:** A booking is underway (an Appointment `a` with status Requested exists for this session). A ServiceType matching `serviceTypeId` exists. `fromDate` ≤ `toDate`.
- **Postconditions:**
  - `a.preferredTimeFrame` was set to `fromDate`–`toDate`. *(attribute modification)*
  - `a` was associated with the ServiceType matching `serviceTypeId` (Is-for). *(association formed)*
  - (The TimeSlots between `fromDate` and `toDate` with `isAvailable` = true, belonging to HealthcareProfessionals who Provide the ServiceType, are returned.)
- **Note:** If no slot is available in the range (UC1 extension 7a), the system indicates that no slots are available; the associations above remain.

#### Contract CO4: selectTimeSlot
- **Operation:** `selectTimeSlot(slotId: String)`
- **Cross References:** Use Case: Book Appointment (steps 8-9), extensions 9a and 9b
- **Preconditions:** A booking `a` is underway and associated with a ServiceType `st`. A TimeSlot `ts` matching `slotId` exists and was among the slots presented.
- **Postconditions:** (when `ts.isAvailable` is true and, if `st.requiresReferral` is true, the patient of `a` has an active Referral `r`, with status Open or Scheduled, that requests `st`'s MedicalSpecialty or the HealthcareProfessional of `ts`)
  - `ts.isAvailable` was set to false. *(attribute modification)*
  - `a.date` was set to `ts.date` and `a.startTime` to `ts.startTime`. *(attribute modification)*
  - `a` was associated with `ts` (Occupies). *(association formed)*
  - `a` was associated with the HealthcareProfessional who is Available-during `ts` (Scheduled-with). *(association formed)*
  - If `st.requiresReferral` is true, `r` was associated with `a` (Results-in). *(association formed)*
- **Note:** If `ts` is no longer available (UC1 extension 9a), the system reports "slot no longer available". If `st.requiresReferral` is true and the patient has no such referral (UC1 extension 9b, failure SSD), the system reports "referral required, none found". In both cases none of the postconditions occur and `ts.isAvailable` is unchanged.

#### Contract CO5: confirmAppointment
- **Operation:** `confirmAppointment()`
- **Cross References:** Use Case: Book Appointment (steps 10-11)
- **Preconditions:** A booking `a` is underway and is associated with a TimeSlot, a HealthcareProfessional and a ServiceType (CO4 succeeded).
- **Postconditions:**
  - `a.status` was set to Confirmed. *(attribute modification)*
  - `a.appointmentNumber` was set to a new unique number. *(attribute modification)*
  - If `a` is associated with a Referral `r` whose status is Open, `r.status` was set to Scheduled. *(attribute modification)*
  - An ActivityLogEntry `log` was created, its `timestamp`, `action` ("book appointment") and `recordType` ("Appointment") were set, and `log` was associated with `u` and with the Patient of `a`. *(instance creation, attribute modification, association formed)*

#### Contract CO6: cancelBooking
- **Operation:** `cancelBooking()`
- **Cross References:** Use Case: Book Appointment (extension 9b; also extensions 7a and 10a)
- **Preconditions:** A booking `a` is underway and has not been confirmed.
- **Postconditions:**
  - If `a` was associated with a TimeSlot `ts`, that association was broken and `ts.isAvailable` was set to true. *(association broken, attribute modification)*
  - If `a` was associated with a Referral, that association was broken. *(association broken)*
  - `a` was deleted, and its associations with the Patient, `u`, the ServiceType and the HealthcareProfessional were broken. No appointment is saved. *(instance deletion, association broken)*

---

### UC2 Record Consultation

#### Contract CO7: startConsultation
- **Operation:** `startConsultation(appointmentId: String)`
- **Cross References:** Use Case: Record Consultation (steps 1-2), extensions 2a and 2b
- **Preconditions:** `u` is authenticated as a HealthcareProfessional. An Appointment `a` with `appointmentNumber` = `appointmentId` exists, is dated today, and `a.status` is Confirmed or Arrived.
- **Postconditions:** (when `u` is authorized, meaning `u`'s role grants access and `u` identifies the HealthcareProfessional with whom `a` is Scheduled-with, and `a.status` is Arrived)
  - A Consultation instance `c` was created. *(instance creation)*
  - `c.date` and `c.startTime` were set to the current date and time. *(attribute modification)*
  - `c` was associated with `a` (Conducted-during). *(association formed)*
  - `c` was associated with the ClinicalRecord of `a`'s Patient (Contains). *(association formed)*
  - An ActivityLogEntry `log` was created with `action` "open clinical record" and `recordType` "ClinicalRecord", and associated with `u` and with `a`'s Patient. *(instance creation, attribute modification, association formed)*
- **Note:** If `u` is not authorized (UC2 extension 2a, failure SSD), the system reports "access denied", and only an ActivityLogEntry with `action` "access denied" is created and associated with `u` and the Patient; no Consultation is created and the clinical record is not opened. If `u` is authorized but `a.status` is Confirmed (UC2 extension 2b), the system reports "patient has not arrived" and nothing changes.

#### Contract CO8: recordObservations
- **Operation:** `recordObservations(observations: String)`
- **Cross References:** Use Case: Record Consultation (step 3)
- **Preconditions:** A consultation `c` is underway.
- **Postconditions:**
  - `c.observations` was set to `observations`. *(attribute modification)*

#### Contract CO9: addDiagnosis
- **Operation:** `addDiagnosis(code: String, description: String)`
- **Cross References:** Use Case: Record Consultation (step 4)
- **Preconditions:** A consultation `c` is underway.
- **Postconditions:**
  - A Diagnosis instance `d` was created. *(instance creation)*
  - `d.code` was set to `code` and `d.description` to `description`. *(attribute modification)*
  - `d` was associated with `c` (Records). *(association formed)*

#### Contract CO10: recordRecommendations
- **Operation:** `recordRecommendations(treatment: String, followUp: String)`
- **Cross References:** Use Case: Record Consultation (step 5)
- **Preconditions:** A consultation `c` is underway.
- **Postconditions:**
  - `c.treatmentRecommendations` was set to `treatment`. *(attribute modification)*
  - `c.followUpRequirements` was set to `followUp`. *(attribute modification)*

#### Contract CO11: addPrescriptionItem
- **Operation:** `addPrescriptionItem(medicationId: String, dosage: String, frequency: String, duration: String, quantity: Integer)`
- **Cross References:** Use Case: Record Consultation (extension 5a), Issue Prescription
- **Preconditions:** A consultation `c` is underway. A Medication with `din` = `medicationId` exists.
- **Postconditions:**
  - If `c` was not yet associated with a Prescription, a Prescription instance `p` was created, `p.dateIssued` was set to the current date, and `p` was associated with `c` (Issues). *(instance creation, attribute modification, association formed)*
  - A PrescriptionLineItem instance `pli` was created. *(instance creation)*
  - `pli.dosage`, `pli.frequency`, `pli.duration` and `pli.quantity` were set to `dosage`, `frequency`, `duration` and `quantity`. *(attribute modification)*
  - `pli` was associated with `p` (Contains). *(association formed)*
  - `pli` was associated with the Medication whose `din` matches `medicationId` (Prescribes). *(association formed)*

#### Contract CO12: requestFollowUp
- **Operation:** `requestFollowUp(serviceTypeId: String, preferredTimeFrame: String)`
- **Cross References:** Use Case: Record Consultation (extension 5b), Request Follow-up Appointment, Verify Availability
- **Preconditions:** A consultation `c` is underway. A ServiceType matching `serviceTypeId` exists.
- **Postconditions:** (when availability exists for the ServiceType in `preferredTimeFrame`)
  - An Appointment instance `f` was created. *(instance creation)*
  - `f.status` was set to Requested and `f.preferredTimeFrame` to `preferredTimeFrame`. *(attribute modification)*
  - `f` was associated with the ServiceType matching `serviceTypeId` (Is-for), with the Patient of `c`'s Appointment (Booked-for), with `u` (Booked-by) and with `c` (Requests-follow-up). *(association formed)*
  - (`f` is not associated with any TimeSlot; Administrative Staff assign one later through Confirm Pending Appointment.)
- **Note:** If no availability exists in that time frame, the system indicates it and no Appointment is created.

#### Contract CO13: endConsultation
- **Operation:** `endConsultation()`
- **Cross References:** Use Case: Record Consultation (steps 6-7), extension 6a
- **Preconditions:** A consultation `c` is underway.
- **Postconditions:** (when at least one clinical entry was recorded: observations, a Diagnosis, treatment recommendations or follow-up requirements)
  - `c.endTime` was set to the current time. *(attribute modification)*
  - The Appointment `a` that `c` is Conducted-during had `a.status` set to Completed. *(attribute modification)*
  - An ActivityLogEntry `log` was created with `action` "record consultation" and `recordType` "Consultation", and associated with `u` and with `a`'s Patient. *(instance creation, attribute modification, association formed)*
- **Note:** If no clinical entry was recorded (UC2 extension 6a), the system indicates that at least one clinical entry is required and nothing changes. A Diagnosis is not required.

---

### UC3 Issue Referral

#### Contract CO14: startReferral
- **Operation:** `startReferral(patientId: PatientID)`
- **Cross References:** Use Case: Issue Referral (steps 1-2)
- **Preconditions:** `u` is authenticated as a HealthcareProfessional `hp` who practises at the clinic and is authorized to issue referrals. A Patient with `patientId` exists.
- **Postconditions:**
  - A Referral instance `r` was created. *(instance creation)*
  - `r.dateIssued` was set to the current date and `r.status` to Draft. *(attribute modification)*
  - `r` was associated with the Patient matching `patientId` (Concerns). *(association formed)*
  - `r` was associated with `hp` (Issued-by). *(association formed)*

#### Contract CO15: setRequestedSpecialty
- **Operation:** `setRequestedSpecialty(specialtyId: String)`
- **Cross References:** Use Case: Issue Referral (steps 3-4), extension 4a
- **Preconditions:** A referral `r` is underway with status Draft. A MedicalSpecialty `ms` with `code` = `specialtyId` exists.
- **Postconditions:** (when at least one HealthcareProfessional who Specializes-in `ms` Practises-at a ClinicLocation)
  - `r` was associated with `ms` (Requests-specialty). *(association formed)*
  - (The HealthcareProfessionals practising `ms` are returned.)
- **Note:** If no clinic professional practises the specialty (UC3 extension 4a, failure SSD), the system reports "specialty not offered" and `r` is not associated with `ms`.

#### Contract CO16: enterReferralDetails
- **Operation:** `enterReferralDetails(clinicalReason: String, priority: Priority)`
- **Cross References:** Use Case: Issue Referral (step 5)
- **Preconditions:** A referral `r` is underway with status Draft.
- **Postconditions:**
  - `r.clinicalReason` was set to `clinicalReason`. *(attribute modification)*
  - `r.priority` was set to `priority`. *(attribute modification)*

#### Contract CO17: submitReferral
- **Operation:** `submitReferral()`
- **Cross References:** Use Case: Issue Referral (steps 6-7), extension 6a
- **Preconditions:** A referral `r` is underway with status Draft.
- **Postconditions:** (when `r` is associated with a MedicalSpecialty and `r.clinicalReason` and `r.priority` are set)
  - `r.status` was set to Open. *(attribute modification)*
  - `r.referralNumber` was set to a new unique number. *(attribute modification)*
  - An ActivityLogEntry `log` was created with `action` "issue referral" and `recordType` "Referral", and associated with `u` and with `r`'s Patient. *(instance creation, attribute modification, association formed)*
- **Note:** If the specialty, clinical reason or priority is missing (UC3 extension 6a), the system indicates the missing information and `r.status` remains Draft.

#### Contract CO18: cancelReferral
- **Operation:** `cancelReferral()`
- **Cross References:** Use Case: Issue Referral (extension 4a)
- **Preconditions:** A referral `r` is underway with status Draft.
- **Postconditions:**
  - `r` was deleted, and its associations with the Patient, the issuing HealthcareProfessional and any MedicalSpecialty were broken. No referral is saved. *(instance deletion, association broken)*
