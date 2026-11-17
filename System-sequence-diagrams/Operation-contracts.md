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
  - (The TimeSlots between fromDate and toDate that are *bookable* for the ServiceType, for the Patient of a and for the booking a, as defined in CO21, belonging to HealthcareProfessionals who Provide the ServiceType, are returned. If there are none, the system indicates that no slots are available.)

**Contract CO4: selectTimeSlot**
- **Operation:** selectTimeSlot(slotId: String)
- **Cross References:** Use Case: Book Appointment (steps 8-9, extensions 9a, 9b and 10a)
- **Preconditions:** A booking *a* is underway and associated with a ServiceType *st*. A TimeSlot *ts* matching slotId exists and ts is *bookable* for st, for the Patient of a and for the booking a, as defined in CO21 (it is still available or already held by a, not offered to another patient's waiting list entry, and every Resource st Requires has enough remaining quantity when a's own current allocations are not counted). Let *hp* be the HealthcareProfessional who is Available-during ts; hp Provides st. If st.requiresReferral is true, a valid Referral *r* exists, meaning that:
  - r Concerns the same Patient as a;
  - (G) r.status is Open or Scheduled, i.e. not completed or cancelled (an active referral may result in several appointments);
  - r Requests-specialty the MedicalSpecialty that st Belongs-to, **or** r Requests-professional hp.

  r may have been issued by a clinic HealthcareProfessional (UC3) or by an ExternalPhysician and recorded by staff (Record External Referral); (G) a referral recorded by staff is saved with status Open.
- **Postconditions:**
  - If a was already associated with a TimeSlot *ts'*, a HealthcareProfessional, a Referral or Resources through an earlier selection (e.g. the patient declined the proposed slot, UC1 extension 10a), those Occupies, Scheduled-with, Results-in and Is-allocated associations were broken and, if ts' is not ts, ts'.isAvailable was set to true. (association broken, attribute modification)
  - ts.isAvailable was set to false. (G) (attribute modification)
  - a was associated with ts (Occupies). (association formed)
  - a was associated with hp (Scheduled-with). (association formed)
  - a.date was set to ts.date and a.startTime to ts.startTime. (attribute modification)
  - If st.requiresReferral is true, r was associated with a (Results-in). (association formed)
  - For every Resource *R* that st Requires, a was associated with R (Is-allocated). The quantity allocated is the quantityNeeded of st's ResourceRequirement for R. (association formed)
- **Note:** If ts is no longer available or no longer bookable (UC1 extension 9a), the system reports "slot no longer available"; this includes a required Resource no longer having enough remaining quantity. If st.requiresReferral is true and no valid referral exists, the system reports "referral required" (UC1 extension 9b, failure SSD). In both cases none of the postconditions occur: an earlier selection of a, with its TimeSlot, Referral and Is-allocated associations, stays exactly as it was, and the staff member selects another slot or calls cancelBooking() (CO6). (G) Resources are allocated when the slot is selected, the same moment the slot itself is held, so the capacity check and the allocation happen together and the allocation is released by a new selection or by cancelBooking() (CO6). A ServiceType that Requires no Resource forms no Is-allocated association.

**Contract CO5: confirmAppointment**
- **Operation:** confirmAppointment()
- **Cross References:** Use Case: Book Appointment (steps 10-11)
- **Preconditions:** A booking *a* is underway and is associated with a TimeSlot *ts*, a HealthcareProfessional and a ServiceType (a slot was successfully selected).
- **Postconditions:**
  - a.status was set to Confirmed. (attribute modification)
  - a.appointmentNumber was set to a new unique number. (attribute modification)
  - (G) If a is associated with a Referral *r*, r.status was set to Scheduled. (attribute modification)
  - (G) If ts Is-offered to a WaitingListEntry *w* that Concerns the Patient of a (the patient accepted a phone offer), the Is-offered association between w and ts was broken, w was associated with a (Results-in), and w.status was set to Scheduled. (association broken, association formed, attribute modification)
  - An ActivityLogEntry *log* was created, its timestamp, action ("book appointment") and recordType ("Appointment") were set, and log was associated with u and with the Patient of a. (instance creation, attribute modification, association formed)
- **Note:** ts.isAvailable remains false and the Is-allocated associations remain: both were formed when the slot was selected (CO4). The offer is cleared only on confirmation, so an abandoned booking (CO6) leaves the patient's offer in place.

**Contract CO6: cancelBooking**
- **Operation:** cancelBooking()
- **Cross References:** Use Case: Book Appointment (extensions 7a, 9b, 10a; failure SSD)
- **Preconditions:** A booking *a* is underway and has not been confirmed (a.status is Requested and a.appointmentNumber is not set).
- **Postconditions:**
  - If a is associated with a TimeSlot *ts*, ts.isAvailable was set to true. (attribute modification)
  - All associations of a (Booked-for, Booked-by, Is-for, and, if formed, Occupies, Scheduled-with, Results-in, Is-allocated) were broken. (association broken)
  - a was deleted. (instance deletion)

**Contract CO20: addToWaitingList** (Iteration II)
- **Operation:** addToWaitingList(reason: String, priority: Priority, professionalId: String [0..1])
- **Cross References:** Use Case: Book Appointment (extension 7a), Request Waiting List Placement
- **Preconditions:** u is authenticated as AdministrativeStaff. A booking *a* is underway (a.status is Requested and a.appointmentNumber is not set), a is associated with a ServiceType *st*, and specifyService() found no available TimeSlot. If professionalId is given, a HealthcareProfessional *hp* with licenceNumber = professionalId exists and Provides st.
- **Postconditions:**
  - A WaitingListEntry instance *w* was created. (instance creation)
  - w.dateRequested was set to the current date and time, w.reason to reason, w.priority to priority and w.status to Waiting. (attribute modification)
  - w.fromDate and w.toDate were set to the date range given in specifyService(). (attribute modification)
  - w was associated with the Patient of a (Concerns), with st (Requests-service) and with u (Added-by). (association formed)
  - If st Belongs-to a MedicalSpecialty *ms*, w was associated with ms (Requests-specialty). (association formed)
  - If professionalId was given, w was associated with hp (Requests-professional). (association formed)
  - All associations of a were broken and a was deleted, as in cancelBooking() (CO6). (association broken, instance deletion)
  - An ActivityLogEntry *log* was created with action "add to waiting list" and recordType "WaitingListEntry", and associated with u and with the Patient of w. (instance creation, attribute modification, association formed)
- **Note:** The underway booking held no TimeSlot (none was available), so no slot is released.

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

### UC4 Verify Availability (Iteration II)

**Contract CO21: searchAvailability**
- **Operation:** searchAvailability(criteria: AvailabilityCriteria, fromDate: Date, toDate: Date)
- **Cross References:** Use Case: Verify Availability (steps 1-6, extensions 2a, 3a, 4a, 5a); included by Book Appointment, Request Appointment, Reschedule Appointment, Request Follow-up Appointment
- **Preconditions:** u is authenticated as AdministrativeStaff. criteria gives at least one of a ServiceType, a MedicalSpecialty or a HealthcareProfessional, each of which exists. fromDate ≤ toDate.
- **Postconditions:**
  - None: no domain object is changed. (G) The search is not logged because it touches no patient information.
  - (The *bookable* TimeSlots whose date is between fromDate and toDate are returned, sorted by date and time, for the HealthcareProfessionals who are Available-during them and who Provide the ServiceType, Specialize-in the MedicalSpecialty, or are the given HealthcareProfessional.)
- **Definition (bookable),** used by CO3, CO4 and CO21. A TimeSlot *ts* is bookable for an optional ServiceType *st*, an optional Patient *p* and an optional unfinished booking *a* (an Appointment being booked in UC1; none in UC4 used directly) when:
  1. ts.date is not in the past, and ts.isAvailable is true or ts is the TimeSlot that a already Occupies (a slot held by a booking does not block that same booking);
  2. (G) ts is not Is-offered to a WaitingListEntry, unless that entry Concerns p (a held offer is shown only to the patient it was offered to; a search without a patient, as in UC4 used directly, never shows it);
  3. if st is given, ts lasts at least st.durationMinutes;
  4. if st is given, for every Resource *R* that st Requires with quantityNeeded *q* (ResourceRequirement), q plus, for every Appointment other than a that Is-allocated R, has status Requested, Confirmed or Arrived, and Occupies a TimeSlot overlapping ts, the quantityNeeded of R for that Appointment's ServiceType, does not exceed R.quantity. a's own current allocations are not counted, so a reselection never counts the same booking twice.
- **Note:** If no HealthcareProfessional matches (extension 3a) the system reports "no professional matches the criteria"; if professionals match but no slot is returned (extension 5a) it reports "no slots available". An offered slot keeps isAvailable = true because no Appointment occupies it; rule 2 is what holds it for the offered patient. (G) Rule 4 counts only active Appointments, matching the domain-model rule that a TimeSlot is unavailable only while a Requested, Confirmed or Arrived Appointment occupies it.

### UC5 Make Availability (Iteration II)

**Contract CO22: startAvailability**
- **Operation:** startAvailability(professionalId: String)
- **Cross References:** Use Case: Make Availability (steps 1-3)
- **Preconditions:** u is authenticated as AdministrativeStaff. A HealthcareProfessional *hp* with licenceNumber = professionalId exists and Practises-at at least one ClinicLocation.
- **Postconditions:**
  - An Availability instance *av* was created. (instance creation)
  - av was associated with hp (Has). (association formed)
  - (hp's ClinicLocations and existing Availabilities are returned.)
- **Note:** If the workflow is abandoned, cancelAvailability() (CO25) deletes av. (G) As in UC1, the in-progress Availability forms its associations step by step.

**Contract CO23: specifyAvailability**
- **Operation:** specifyAvailability(locationId: String, fromDate: Date, toDate: Date, repetition: Repetition, daysOfWeek: DayOfWeek [0..7], dayOfMonth: Integer [0..1], startTime: Time, endTime: Time, slotDurationMinutes: Integer)
- **Cross References:** Use Case: Make Availability (steps 4-7, extensions 4a, 5a, 6a, 7a, 7b)
- **Preconditions:** An availability *av* is underway for HealthcareProfessional *hp*. A ClinicLocation *cl* with name = locationId exists and hp Practises-at cl. fromDate ≤ toDate and toDate is not in the past. If repetition is Weekly, daysOfWeek is not empty; if Monthly, 1 ≤ dayOfMonth ≤ 31. slotDurationMinutes > 0, startTime < endTime and (endTime − startTime) is a multiple of slotDurationMinutes. At least one date in the range matches the pattern.
- **Postconditions:**
  - av.fromDate, av.toDate, av.repetition, av.daysOfWeek, av.dayOfMonth, av.startTime, av.endTime and av.slotDurationMinutes were set to the given values. (attribute modification)
  - av was associated with cl (Located-at); any earlier Located-at association of av was broken. (association formed, association broken)
  - (A preview of the slots av would generate is returned: the dates, the times and their number. If some of them overlap TimeSlots that hp is already Available-during, at any location, those overlapping slots are listed (extension 7b).)
- **Note:** No TimeSlot is created yet; slots are created on confirmAvailability() (CO24). If a precondition fails, the system reports the invalid input and av is unchanged.

**Contract CO24: confirmAvailability**
- **Operation:** confirmAvailability()
- **Cross References:** Use Case: Make Availability (steps 8-9, extension 7b)
- **Preconditions:** An availability *av* is underway, specifyAvailability() succeeded, and av is associated with a ClinicLocation *cl*.
- **Postconditions:**
  - For every date d from av.fromDate to av.toDate that matches av.repetition (every day if Daily, a day in av.daysOfWeek if Weekly, av.dayOfMonth if Monthly), and every period of av.slotDurationMinutes from av.startTime to av.endTime that does not overlap a TimeSlot hp is already Available-during: (G)
    - a TimeSlot instance *ts* was created; (instance creation)
    - ts.date, ts.startTime and ts.endTime were set to d and the period, and ts.isAvailable to true; (attribute modification)
    - ts was associated with av (Generates), with hp (Available-during) and with cl (Located-at). (association formed)
  - An ActivityLogEntry *log* was created with action "make availability" and recordType "Availability", and associated with u. (instance creation, attribute modification, association formed)
- **Note:** (G) Overlapping periods are skipped, so confirming after extension 7b saves only the non-overlapping slots.

**Contract CO25: cancelAvailability**
- **Operation:** cancelAvailability()
- **Cross References:** Use Case: Make Availability (extensions 7b, 8a; failure SSD)
- **Preconditions:** An availability *av* is underway and confirmAvailability() has not been called.
- **Postconditions:**
  - All associations of av (Has and, if formed, Located-at) were broken. (association broken)
  - av was deleted. (instance deletion)

### Import Availability from CSV (Iteration II)

**Contract CO26: importAvailability**
- **Operation:** importAvailability(filePath: String)
- **Cross References:** Use Case: Import Availability from CSV
- **Preconditions:** u is authenticated as AdministrativeStaff. The file at filePath exists and can be read, and its header has at least the assignment's columns First Name, Last Name, Title, Specialty, Date, Start and End. (G) Two optional columns are recognized: Location and Status (the Status column is the one written by exportAvailability(), CO27).
- **Postconditions:** For every *accepted* row (see the note for the rejection rules):
  - *Professional.* (G) The HealthcareProfessional *hp* whose firstName and lastName match the row was found; if none exists, hp was created with firstName, lastName and profession (= Title) set. (instance creation, attribute modification)
  - *Specialty.* The MedicalSpecialty *ms* whose name matches Specialty was found; if none exists, ms was created with its name set. In either case, hp was associated with ms (Specializes-in) if not already. (instance creation, attribute modification, association formed)
  - *Location.* If the row has a Location, the ClinicLocation *cl* whose name matches it was found; if none exists, cl was created with its name set and associated with the Clinic (Operates). (G) If the file has no Location column, *cl* is the Clinic's only ClinicLocation. In either case, hp was associated with cl (Practises-at) if not already. (instance creation, attribute modification, association formed)
  - *Slot.* A TimeSlot *ts* was created with date, startTime and endTime from the row and isAvailable = true, and associated with hp (Available-during) and with cl (Located-at). ts has no Availability. (instance creation, attribute modification, association formed)
- Once per import, an ActivityLogEntry was created with action "import availability" and recordType "TimeSlot", and associated with u. (instance creation, attribute modification, association formed)
- **Note:** A row is rejected, changes nothing and is reported with its reason when:
  - it is malformed (missing value, invalid date or time, or Start not before End);
  - it overlaps a TimeSlot hp is already Available-during;
  - (G) the file has no Location column and the Clinic does not have exactly one ClinicLocation (reason "location required");
  - (G) its Status is Booked: the file holds no patient or appointment, so a booked slot cannot be rebuilt; it is reported instead of being imported as an available slot.

  A row with Status Available, or with no Status column, is imported as available. The system returns the number of slots imported and each rejected row with its reason. If the file cannot be read, nothing is imported. Attributes that the CSV does not carry (a professional's licenceNumber, a specialty's code, a location's address and phone) are left unset; they are not invented. (G) A professional is identified by first and last name because the CSV has no licence number.
- **Fresh start (G):** The system starts empty. A file in the assignment's format (no Location column) can be imported only after exactly one ClinicLocation exists, created with the clinic's own name, address and phone through *Manage Clinic Locations* (Iteration I). A file with the Location column needs no setup: each location named in the file is found or created. In both cases nothing is invented: attributes the file does not carry stay unset.

### Export Availability to CSV (Iteration II)

**Contract CO27: exportAvailability**
- **Operation:** exportAvailability(filePath: String)
- **Cross References:** Use Case: Export Availability to CSV
- **Preconditions:** u is authenticated as AdministrativeStaff. filePath can be written.
- **Postconditions:**
  - An ActivityLogEntry was created with action "export availability" and recordType "TimeSlot", and associated with u. (instance creation, attribute modification, association formed)
  - (No other domain object is changed. Every TimeSlot is written to the file with the columns First Name, Last Name, Title, Specialty, Date, Start, End, (G) Location (the name of the ClinicLocation the slot is Located-at) and (G) Status: Available if isAvailable is true, otherwise Booked. Specialty is the name of a MedicalSpecialty the professional Specializes-in. The number of rows written is returned.)
- **Note:** A file written by exportAvailability() can be read by importAvailability() (CO26): its available slots are imported and its booked slots are reported as rejected. (G) Rebuilding bookings from an availability file is not supported, because the file holds no patient or appointment.

### Request Waiting List Placement (Iteration II)

**Contract CO28: requestWaitingListPlacement**
- **Operation:** requestWaitingListPlacement(patientId: PatientID, professionalId: String [0..1], specialtyId: String [0..1], serviceTypeId: String [0..1], fromDate: Date [0..1], toDate: Date [0..1], reason: String, priority: Priority [0..1])
- **Cross References:** Use Case: Request Waiting List Placement (outside a booking; inside UC1 extension 7a the operation is addToWaitingList(), CO20)
- **Preconditions:** u is authenticated as a Patient or as AdministrativeStaff. A Patient *p* with patientId exists; if u is a Patient, u Identifies p (a Patient requests only for themselves). At least one of professionalId, specialtyId or a date range (fromDate and toDate) is given; (G) serviceTypeId may also be given. Each given professional (by licenceNumber), specialty (by code) and service type exists. If a date range is given, fromDate ≤ toDate. If u is AdministrativeStaff, priority is given.
- **Postconditions:**
  - A WaitingListEntry instance *w* was created. (instance creation)
  - w.dateRequested was set to the current date and time, w.reason to reason, w.status to Waiting, and w.fromDate and w.toDate to the given dates, if any. (attribute modification)
  - w.priority was set to priority; (G) if u is a Patient, w.priority was set to Routine. (attribute modification)
  - w was associated with p (Concerns) and with u (Added-by), and, for each one given, with the HealthcareProfessional (Requests-professional), the MedicalSpecialty (Requests-specialty) and the ServiceType (Requests-service). (association formed)
  - An ActivityLogEntry was created with action "add to waiting list" and recordType "WaitingListEntry", and associated with u and p. (instance creation, attribute modification, association formed)

### Cancel Appointment, Identify Eligible Waitlisted Patients (Iteration II)

**Contract CO29: cancelAppointment**
- **Operation:** cancelAppointment(appointmentId: String)
- **Cross References:** Use Cases: Cancel Appointment (Iteration I), Identify Eligible Waitlisted Patients (included)
- **Preconditions:** u is authenticated as AdministrativeStaff. An Appointment *a* with appointmentNumber = appointmentId exists and a.status is Requested, Confirmed or Arrived.
- **Postconditions:**
  - a.status was set to Cancelled. (attribute modification)
  - If a Occupies a TimeSlot *ts*, that Occupies association was broken and ts.isAvailable was set to true. (association broken, attribute modification)
  - An ActivityLogEntry was created with action "cancel appointment" and recordType "Appointment", and associated with u and with a's Patient. (instance creation, attribute modification, association formed)
  - (If a TimeSlot ts was freed, the eligible WaitingListEntries are returned, ordered by priority and (G) then by dateRequested: those with status Waiting whose requested HealthcareProfessional, if any, is the one Available-during ts; whose requested MedicalSpecialty, if any, is one that professional Specializes-in; (G) whose requested ServiceType, if any, is a's ServiceType; and whose date range, if any, contains ts.date.)
- **Note:** The Is-allocated associations of a are kept as a record; a Cancelled Appointment is not counted by the capacity rule (CO21, rule 4). Identify Eligible Waitlisted Patients has no operation of its own: it is the result of cancelAppointment().

### Offer Appointment by Phone, Remove Patient from Waiting List, Update Waiting List Priority (Iteration II)

**Contract CO30: offerAppointment**
- **Operation:** offerAppointment(entryId: String, slotId: String)
- **Cross References:** Use Case: Offer Appointment by Phone
- **Preconditions:** u is authenticated as AdministrativeStaff. A TimeSlot *ts* matching slotId was freed by cancelAppointment() (CO29), ts.isAvailable is true and ts is not Is-offered to any WaitingListEntry. A WaitingListEntry *w* identified by entryId exists with status Waiting, is eligible for ts, and is the **next** entry in the order returned by CO29 (priority Urgent, then High, then Routine; (G) equal priorities by oldest dateRequested): every entry ranked before w in that list is no longer eligible, no longer Waiting, or (G) has already been offered ts in this session and did not answer (CO32).
- **Postconditions:**
  - w.status was set to Offered. (attribute modification)
  - w was associated with ts (Is-offered). (association formed)
- **Note:** The patient accepts by being booked through UC1: confirmAppointment() (CO5) breaks Is-offered, links w to the Appointment (Results-in) and sets w.status to Scheduled. While ts Is-offered to w, it is bookable only for w's Patient (CO21, rule 2).

**Contract CO31: declineOffer**
- **Operation:** declineOffer(entryId: String)
- **Cross References:** Use Case: Offer Appointment by Phone (step 2)
- **Preconditions:** u is authenticated as AdministrativeStaff. A WaitingListEntry *w* identified by entryId exists with status Offered.
- **Postconditions:**
  - The Is-offered association between w and its TimeSlot was broken (the slot is released). (association broken)
  - w.status was set to OfferDeclined. (attribute modification)
- **Note:** Staff may then call removeFromWaitingList() (CO33) and offerAppointment() (CO30) for the next eligible entry.

**Contract CO32: recordOfferNotAnswered**
- **Operation:** recordOfferNotAnswered(entryId: String)
- **Cross References:** Use Case: Offer Appointment by Phone (step 3, (G))
- **Preconditions:** u is authenticated as AdministrativeStaff. A WaitingListEntry *w* identified by entryId exists with status Offered.
- **Postconditions:**
  - The Is-offered association between w and its TimeSlot was broken. (association broken)
  - w.status was set to Waiting. (attribute modification)
  - An ActivityLogEntry was created with action "offer not answered" and recordType "WaitingListEntry", and associated with u and with w's Patient. (instance creation, attribute modification, association formed)

**Contract CO33: removeFromWaitingList**
- **Operation:** removeFromWaitingList(entryId: String)
- **Cross References:** Use Case: Remove Patient from Waiting List
- **Preconditions:** u is authenticated as AdministrativeStaff. A WaitingListEntry *w* identified by entryId exists and w.status is not Scheduled or Removed.
- **Postconditions:**
  - If w Is-offered a TimeSlot, that association was broken. (association broken)
  - w.status was set to Removed. (attribute modification)
  - An ActivityLogEntry was created with action "remove from waiting list" and recordType "WaitingListEntry", and associated with u and with w's Patient. (instance creation, attribute modification, association formed)

**Contract CO34: updateWaitingListPriority**
- **Operation:** updateWaitingListPriority(entryId: String, priority: Priority)
- **Cross References:** Use Case: Update Waiting List Priority
- **Preconditions:** u is authenticated as AdministrativeStaff. A WaitingListEntry *w* identified by entryId exists with status Waiting.
- **Postconditions:**
  - w.priority was set to priority. (attribute modification)
  - An ActivityLogEntry was created with action "update waiting list priority" and recordType "WaitingListEntry", and associated with u and with w's Patient. (instance creation, attribute modification, association formed)

### Manage Predefined Resources, Define Service Resource Requirements (Iteration II)

**Contract CO35: addResource**
- **Operation:** addResource(name: String, type: ResourceType, quantity: Integer, locationId: String [0..1])
- **Cross References:** Use Case: Manage Predefined Resources
- **Preconditions:** u is authenticated with role ClinicAdministrator. No Resource with this name exists. quantity > 0. If locationId is given, a ClinicLocation with name = locationId exists.
- **Postconditions:**
  - A Resource instance *R* was created with name, type and quantity set. (instance creation, attribute modification)
  - If locationId is given, R was associated with that ClinicLocation (Located-at). (association formed)

**Contract CO36: defineResourceRequirement**
- **Operation:** defineResourceRequirement(serviceTypeId: String, resourceName: String, quantityNeeded: Integer)
- **Cross References:** Use Case: Define Service Resource Requirements
- **Preconditions:** u is authenticated with role ClinicAdministrator. A ServiceType *st* matching serviceTypeId and a Resource *R* with name = resourceName exist. 0 < quantityNeeded ≤ R.quantity.
- **Postconditions:**
  - If st did not yet Require R, st was associated with R (Requires) and a ResourceRequirement *rr* was created for that link. (association formed, instance creation)
  - rr.quantityNeeded was set to quantityNeeded. (attribute modification)

### Operations identified but not yet specified

- **updateResource / retireResource** (Manage Predefined Resources: "adds, updates or retires"). Not given contracts: Resource has no attribute that records a retired resource, and the use case does not say which attributes may be updated. To be settled by the group.
- **Allocate Resources to Appointment** and **Check Resource Availability** have no operations of their own: allocation is part of selectTimeSlot() (CO4) and the check is rule 4 of the bookable definition (CO21).
- **Accepting an offer** has no operation of its own: the patient is booked through UC1 and confirmAppointment() (CO5) records the acceptance.

