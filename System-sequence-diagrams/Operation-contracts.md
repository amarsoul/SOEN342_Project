## Operation contracts

In the contracts, *u* is the UserAccount of the authenticated user who performs the operation. Postconditions are stated in terms of the domain model and are of three kinds: instance creation or deletion, attribute modification, and association formed or broken.

When a precondition does not hold, the operation reports an error and changes nothing, except where a note says otherwise. Contracts are numbered in the order of the system operations and listed under the use case they belong to. (G) Holding a time slot as soon as it is selected, and removing an unconfirmed booking or a draft referral when a workflow is cancelled (CO6, CO19), are group modeling decisions that make "the slot remains available" and "no appointment/referral is saved" hold in the domain state; the assignment does not prescribe them.

### UC1 Book Appointment

**Contract CO1: searchPatients**
- **Operation:** searchPatients(criteria: SearchCriteria)
- **Cross References:** Use Case: Book Appointment (step 2, extension 3a), Search Patient
- **Preconditions:** u is authenticated with a role allowed to search patients (AdministrativeStaff or HealthcareProfessional).
- **Postconditions:**
  - An ActivityLogEntry instance *log* was created. (instance creation)
  - log.timestamp was set to the current date and time, log.action to "search patients" and log.recordType to "Patient". (attribute modification)
  - log was associated with u. (association formed)
  - (No domain object is otherwise changed; the matching Patients, found by insurance number, date of birth, phone number or partial name, are returned. If none match, the system indicates that no patient was found.)

**Contract CO2: startNewBooking**
- **Operation:** startNewBooking(patientId: PatientID)
- **Cross References:** Use Case: Book Appointment (step 4)
- **Preconditions:** u is authenticated as AdministrativeStaff. A Patient with patientId exists.
- **Postconditions:**
  - An Appointment instance *a* was created. (instance creation)
  - a.status was set to Requested. (attribute modification)
  - a was associated with the Patient whose patientId matches patientId (Booked-for). (association formed)
  - a was associated with u (Booked-by). (association formed)
- **Note:** a has no TimeSlot, HealthcareProfessional or ServiceType yet; these are associated by the following operations. If the booking is abandoned, cancelBooking() (CO6) deletes a.

**Contract CO3: specifyService**
- **Operation:** specifyService(serviceTypeId: String, fromDate: Date, toDate: Date)
- **Cross References:** Use Case: Book Appointment (steps 6-7, extension 7a), Verify Availability
- **Preconditions:** A booking is underway (an Appointment *a* with status Requested exists for this session). A ServiceType matching serviceTypeId exists. fromDate ≤ toDate.
- **Postconditions:**
  - a.preferredTimeFrame was set to fromDate–toDate. (attribute modification)
  - a was associated with the ServiceType matching serviceTypeId (Is-for). (association formed)
  - (The TimeSlots between fromDate and toDate with isAvailable = true, belonging to HealthcareProfessionals who Provide the ServiceType, are returned. If there are none, the system indicates that no slots are available.)

**Contract CO4: selectTimeSlot**
- **Operation:** selectTimeSlot(slotId: String)
- **Cross References:** Use Case: Book Appointment (steps 8-9, extensions 9a, 9b and 10a)
- **Preconditions:** A booking *a* is underway and associated with a ServiceType *st*. A TimeSlot *ts* matching slotId exists and ts.isAvailable is true. Let *hp* be the HealthcareProfessional who is Available-during ts. If st.requiresReferral is true, a valid Referral *r* exists, meaning that:
  - r Concerns the same Patient as a;
  - (G) r.status is Open or Scheduled, i.e. not completed or cancelled (an active referral may result in several appointments);
  - r Requests-specialty the MedicalSpecialty that st Belongs-to, **or** r Requests-professional hp.

  r may have been issued by a clinic HealthcareProfessional (UC3) or by an ExternalPhysician and recorded by staff (Record External Referral); (G) a referral recorded by staff is saved with status Open.
- **Postconditions:**
  - If a was already associated with a TimeSlot *ts'*, a HealthcareProfessional or a Referral through an earlier selection (e.g. the patient declined the proposed slot, UC1 extension 10a), those Occupies, Scheduled-with and Results-in associations were broken and ts'.isAvailable was set to true. (association broken, attribute modification)
  - ts.isAvailable was set to false. (G) (attribute modification)
  - a was associated with ts (Occupies). (association formed)
  - a was associated with hp (Scheduled-with). (association formed)
  - a.date was set to ts.date and a.startTime to ts.startTime. (attribute modification)
  - If st.requiresReferral is true, r was associated with a (Results-in). (association formed)
- **Note:** If ts is no longer available (UC1 extension 9a), the system reports "slot no longer available". If st.requiresReferral is true and no valid referral exists, the system reports "referral required" (UC1 extension 9b, failure SSD). In both cases none of the postconditions occur and the staff member selects another slot or calls cancelBooking() (CO6).

**Contract CO5: confirmAppointment**
- **Operation:** confirmAppointment()
- **Cross References:** Use Case: Book Appointment (steps 10-11)
- **Preconditions:** A booking *a* is underway and is associated with a TimeSlot *ts*, a HealthcareProfessional and a ServiceType (a slot was successfully selected).
- **Postconditions:**
  - a.status was set to Confirmed. (attribute modification)
  - a.appointmentNumber was set to a new unique number. (attribute modification)
  - (G) If a is associated with a Referral *r*, r.status was set to Scheduled. (attribute modification)
  - An ActivityLogEntry *log* was created, its timestamp, action ("book appointment") and recordType ("Appointment") were set, and log was associated with u and with the Patient of a. (instance creation, attribute modification, association formed)
- **Note:** ts.isAvailable remains false: it was set when the slot was selected (CO4).

**Contract CO6: cancelBooking**
- **Operation:** cancelBooking()
- **Cross References:** Use Case: Book Appointment (extensions 7a, 9b, 10a; failure SSD)
- **Preconditions:** A booking *a* is underway and has not been confirmed (a.status is Requested and a.appointmentNumber is not set).
- **Postconditions:**
  - If a is associated with a TimeSlot *ts*, ts.isAvailable was set to true. (attribute modification)
  - All associations of a (Booked-for, Booked-by, Is-for, and, if formed, Occupies, Scheduled-with, Results-in) were broken. (association broken)
  - a was deleted. (instance deletion)

### UC2 Record Consultation

**Contract CO7: startConsultation**
- **Operation:** startConsultation(appointmentId: String)
- **Cross References:** Use Case: Record Consultation (steps 1-2, extensions 2a and 2b)
- **Preconditions:** An Appointment *a* with appointmentNumber = appointmentId exists for the current day and a.status is Arrived. u's role grants access to clinical information, and (G) u identifies the HealthcareProfessional with whom a is Scheduled-with. (Authorization is checked first, then arrival.)
- **Postconditions:**
  - A Consultation instance *c* was created. (instance creation)
  - c.date and c.startTime were set to the current date and time. (attribute modification)
  - c was associated with a (Conducted-during). (association formed)
  - c was associated with the ClinicalRecord of a's Patient (Contains). (association formed)
  - An ActivityLogEntry *log* was created with action "open clinical record" and recordType "ClinicalRecord", and associated with u and with a's Patient. (instance creation, attribute modification, association formed)
- **Note:** If u is not authorized (UC2 extension 2a, failure SSD), the system reports "access denied", only an ActivityLogEntry with action "access denied" is created and associated with u and the Patient, no Consultation is created and no clinical information is returned. If u is authorized but a.status is still Confirmed (extension 2b), the system reports that the patient has not arrived and nothing changes.

**Contract CO8: recordObservations**
- **Operation:** recordObservations(observations: String)
- **Cross References:** Use Case: Record Consultation (step 3, optional)
- **Preconditions:** A consultation *c* is underway.
- **Postconditions:**
  - c.observations was set to observations. (attribute modification)

**Contract CO9: addDiagnosis**
- **Operation:** addDiagnosis(code: String, description: String)
- **Cross References:** Use Case: Record Consultation (step 4, repeated zero or more times; see extension 4a)
- **Preconditions:** A consultation *c* is underway.
- **Postconditions:**
  - A Diagnosis instance *d* was created. (instance creation)
  - d.code was set to code and d.description to description. (attribute modification)
  - d was associated with c (Records). (association formed)

**Contract CO10: recordRecommendations**
- **Operation:** recordRecommendations(treatment: String, followUp: String)
- **Cross References:** Use Case: Record Consultation (step 5)
- **Preconditions:** A consultation *c* is underway.
- **Postconditions:**
  - c.treatmentRecommendations was set to treatment. (attribute modification)
  - c.followUpRequirements was set to followUp. (attribute modification)

**Contract CO11: addPrescriptionItem**
- **Operation:** addPrescriptionItem(medicationId: String, dosage: String, frequency: String, duration: String, quantity: Integer)
- **Cross References:** Use Case: Record Consultation (extension 5a), Issue Prescription
- **Preconditions:** A consultation *c* is underway. A Medication with din = medicationId exists.
- **Postconditions:**
  - If c was not yet associated with a Prescription, a Prescription instance was created, its dateIssued was set to the current date, and it was associated with c (Issues). (instance creation, attribute modification, association formed)
  - Let *p* be the Prescription that c Issues (the existing one, or the one just created).
  - A PrescriptionLineItem instance *pli* was created. (instance creation)
  - pli.dosage, pli.frequency, pli.duration and pli.quantity were set to dosage, frequency, duration and quantity. (attribute modification)
  - pli was associated with p (Contains). (association formed)
  - pli was associated with the Medication whose din matches medicationId (Prescribes). (association formed)

**Contract CO12: requestFollowUp**
- **Operation:** requestFollowUp(serviceTypeId: String, preferredTimeFrame: String)
- **Cross References:** Use Case: Record Consultation (extension 5b), Request Follow-up Appointment, Verify Availability
- **Preconditions:** A consultation *c* is underway, Conducted-during Appointment *a*. A ServiceType *st* matching serviceTypeId exists. Availability has been verified: at least one HealthcareProfessional who Provides st has a TimeSlot with isAvailable = true within preferredTimeFrame (otherwise the system reports that no availability exists and nothing changes).
- **Postconditions:**
  - An Appointment instance *fa* was created. (instance creation)
  - fa.status was set to Requested and fa.preferredTimeFrame to preferredTimeFrame. (attribute modification)
  - fa was associated with the Patient of a (Booked-for). (association formed)
  - fa was associated with st (Is-for). (association formed)
  - fa was associated with c (Requests-follow-up). (association formed)
  - fa was associated with u (Booked-by). (association formed)
  - (fa is not associated with any TimeSlot or HealthcareProfessional; it stays pending until Administrative Staff assign a slot and confirm it through Confirm Pending Appointment.)

**Contract CO13: endConsultation**
- **Operation:** endConsultation()
- **Cross References:** Use Case: Record Consultation (steps 6-7, extension 6a)
- **Preconditions:** A consultation *c* is underway and at least one clinical entry has been recorded in c: c.observations, c.treatmentRecommendations or c.followUpRequirements is set, or at least one Diagnosis is associated with c. No particular category (in particular, no Diagnosis) is required. (G) *This minimum-entry rule is a group modeling assumption, not an assignment requirement.*
- **Postconditions:**
  - c.endTime was set to the current time. (attribute modification)
  - The Appointment *a* that c is Conducted-during had a.status set to Completed. (attribute modification)
  - An ActivityLogEntry *log* was created with action "record consultation" and recordType "Consultation", and associated with u and with a's Patient. (instance creation, attribute modification, association formed)
- **Note:** If no clinical entry has been recorded (extension 6a), the system indicates that at least one clinical entry is required and nothing changes.

### UC3 Issue Referral

**Contract CO14: startReferral**
- **Operation:** startReferral(patientId: PatientID)
- **Cross References:** Use Case: Issue Referral (steps 1-2)
- **Preconditions:** u identifies an authenticated HealthcareProfessional *hp* who practises at the clinic and is authorized to issue referrals. A Patient with patientId exists.
- **Postconditions:**
  - A Referral instance *r* was created. (instance creation)
  - r.dateIssued was set to the current date and r.status to Draft. (attribute modification)
  - r was associated with the Patient matching patientId (Concerns). (association formed)
  - r was associated with hp (Issued-by). (association formed)
- **Note:** If the referral is abandoned, cancelReferral() (CO19) deletes r.

**Contract CO15: setRequestedSpecialty**
- **Operation:** setRequestedSpecialty(specialtyId: String)
- **Cross References:** Use Case: Issue Referral (steps 3-4, extension 4a)
- **Preconditions:** A referral *r* is underway and r.status is Draft. A MedicalSpecialty *ms* with code = specialtyId exists, and (G) at least one HealthcareProfessional who Specializes-in ms Practises-at a ClinicLocation.
- **Postconditions:**
  - r was associated with ms (Requests-specialty). (association formed)
  - (The HealthcareProfessionals practising ms are returned.)
- **Note:** If no clinic professional practises the specialty (UC3 extension 4a, failure SSD), the system reports "specialty not offered" and r is not associated with ms; the professional then specifies another specialty or calls cancelReferral() (CO19).

**Contract CO16: setRequestedProfessional**
- **Operation:** setRequestedProfessional(professionalId: String)
- **Cross References:** Use Case: Issue Referral (extension 3a)
- **Preconditions:** A referral *r* is underway and r.status is Draft. A HealthcareProfessional *hp* with licenceNumber = professionalId exists (professionalId is the professional's existing licenceNumber). (G) hp Practises-at a ClinicLocation.
- **Postconditions:**
  - r was associated with hp (Requests-professional). (association formed)
- **Note:** CO16 is used instead of setRequestedSpecialty() (CO15) when the referring professional requests a specific professional; the scenario then continues with enterReferralDetails() (CO17). The domain model also allows a referral to request both a specialty and a professional. CO16 has no SSD, since only the main success scenario and the selected failure scenario of each critical use case have SSDs.

**Contract CO17: enterReferralDetails**
- **Operation:** enterReferralDetails(clinicalReason: String, priority: Priority)
- **Cross References:** Use Case: Issue Referral (step 5)
- **Preconditions:** A referral *r* is underway and r.status is Draft.
- **Postconditions:**
  - r.clinicalReason was set to clinicalReason. (attribute modification)
  - r.priority was set to priority. (attribute modification)

**Contract CO18: submitReferral**
- **Operation:** submitReferral()
- **Cross References:** Use Case: Issue Referral (steps 6-7, extension 6a)
- **Preconditions:** A referral *r* is underway, r.status is Draft, r is associated with a MedicalSpecialty (or a requested HealthcareProfessional), and r.clinicalReason and r.priority are set.
- **Postconditions:**
  - r.status was set to Open. (attribute modification)
  - r.referralNumber was set to a new unique number. (attribute modification)
  - An ActivityLogEntry *log* was created with action "issue referral" and recordType "Referral", and associated with u and with r's Patient. (instance creation, attribute modification, association formed)
- **Note:** If the specialty (or professional), the clinical reason or the priority is missing (extension 6a), the system indicates the missing information and r remains Draft.

**Contract CO19: cancelReferral**
- **Operation:** cancelReferral()
- **Cross References:** Use Case: Issue Referral (extensions 4a, 6b; failure SSD)
- **Preconditions:** A referral *r* is underway and r.status is Draft.
- **Postconditions:**
  - All associations of r (Concerns, Issued-by, and, if formed, Requests-specialty or Requests-professional) were broken. (association broken)
  - r was deleted. (instance deletion)

  ### UC: Register Patient
**Contract CO20: registerPatient**
- **Operation:** registerPatient(firstName: String, lastName: String, dob: Date, phoneNumber: String, insuranceNumber: String)
- **Cross References:** Use Case: Register Patient
- **Preconditions:** u is authenticated as AdministrativeStaff.
- **Postconditions:**
  - A Patient instance p was created.
  - p attributes (firstName, lastName, dateOfBirth, phoneNumber, insuranceNumber) were set.
  - p.patientId was initialized.
  - A ClinicalRecord instance cr was created and associated with p.
