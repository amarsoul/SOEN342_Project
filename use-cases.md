# Use Case Model
...

## Actors

| Actor | Type | Goal |
| --- | --- | --- |
| Administrative Staff | Primary | Registers patients, requests, books, confirms, reschedules and cancels appointments, registers arrivals, enters referrals from external physicians, handles invoices and payments. |
| Patient | Primary | Requests appointments (a request is confirmed by Administrative Staff). |
| Healthcare Professional | Primary | Conducts consultations, records clinical information, issues prescriptions and referrals. |
| Clinic Administrator | Primary | Manages professionals and clinic locations, reviews the activity log. |

External physicians who refer patients do not use the system. When the clinic receives their referral, Administrative Staff record it (*Record External Referral*); staff do not issue the referral themselves. RAMQ claims submission, electronic prescribing to pharmacies and provincial systems are external at this phase and are not part of the system boundary.

## Critical and non-critical use cases

| Use case | Critical? | Primary actor(s) |
| --- | --- | --- |
| UC1 Book Appointment | Critical | Administrative Staff |
| UC2 Record Consultation | Critical | Healthcare Professional |
| UC3 Issue Referral | Critical | Healthcare Professional |
| Request Appointment | Non-critical | Patient, Administrative Staff |
| Verify Availability (included) | Non-critical | (subfunction of UC1, Request Appointment, Reschedule Appointment) |
| Search Patient (also included) | Non-critical | Administrative Staff, Healthcare Professional |
| Register Patient | Non-critical | Administrative Staff |
| Confirm Pending Appointment | Non-critical | Administrative Staff |
| Reschedule Appointment | Non-critical | Administrative Staff |
| Cancel Appointment | Non-critical | Administrative Staff |
| Register Patient Arrival | Non-critical | Administrative Staff |
| Record External Referral | Non-critical | Administrative Staff |
| Issue Prescription (extends UC2) | Non-critical | Healthcare Professional |
| Request Follow-up Appointment (extends UC2) | Non-critical | Healthcare Professional |
| View Clinical Record | Non-critical | Healthcare Professional |
| Generate Invoice | Non-critical | Administrative Staff |
| Register Payment | Non-critical | Administrative Staff |
| Manage Professional Schedule | Non-critical | Administrative Staff |
| Manage Healthcare Professionals | Non-critical | Clinic Administrator |
| Manage Clinic Locations | Non-critical | Clinic Administrator |
| View Activity Log | Non-critical | Clinic Administrator |
| Log In | Non-critical | All human actors |

The three critical use cases carry the core value of the system (getting a patient in front of the right professional, capturing clinical information, and routing patients between professionals) and the highest risk (scheduling constraints, confidentiality of clinical data, referral rules). They are specified below in fully-dressed format. The non-critical use cases have brief scenarios.

**How to read requirements and decisions.** Statements taken from the assignment are written plainly. Statements marked **(G)** are modeling decisions made by our group to fill gaps in the assignment. They are not requirements imposed by the assignment.

## Brief use cases (non-critical)

### Appointments

**Request Appointment.** A Patient requests an appointment for themselves, or Administrative Staff request one on a patient's behalf.
1. Patient or Administrative Staff specifies the service type and a preferred time frame.
2. System verifies professional availability and applicable scheduling constraints (include *Verify Availability*).
3. System presents suitable available time slots.
4. Patient or Administrative Staff selects an available slot.
5. System handles any applicable referral requirement and records the appointment with status Requested.
6. Administrative Staff may later confirm the request through *Confirm Pending Appointment*.

(G) A Patient requests appointments only for themselves. (G) The selected slot is held for the pending request and is released if the request is rejected or cancelled.

**Verify Availability (included by UC1, Request Appointment and Reschedule Appointment).** Given a service type and a time frame, the system determines which professionals provide the service and which of their time slots are available, applying the applicable scheduling constraints (professional's schedule, clinic location, service duration).

**Confirm Pending Appointment.** Administrative Staff select an appointment that was requested earlier (through *Request Appointment*, or as a follow-up request from UC2) and is still pending. If the request has no time slot yet, staff select an available one. Staff confirm the request and its status becomes Confirmed, or they reject it and its status becomes Cancelled. (G) The system checks again that the slot is still available before confirming. This use case covers only appointments requested earlier. It does not cover the confirmation inside UC1 Book Appointment, where staff book and confirm in the same session for a patient who is present at the clinic.

**Reschedule Appointment.** Administrative Staff select an existing appointment and specify a new preferred time frame. The system verifies availability and scheduling constraints (include *Verify Availability*) and presents available slots. Staff select a new slot, and the system moves the appointment to it and frees the previous slot.

**Cancel Appointment.** Administrative Staff select an appointment and cancel it. The system sets its status to Cancelled, frees its time slot, and logs the activity.

**Register Patient Arrival.** On the day of the appointment, the Patient presents at the clinic. Administrative Staff always search for the patient first (include *Search Patient*). The system lists the patient's appointments for that day; staff select the appointment, and the system sets its status to Arrived, records the arrival time, and logs the activity. (G) Registering an arrival directly from an appointment identifier, without the search, is not part of this workflow.

### Patients

**Search Patient (also included by UC1 and Register Patient Arrival).** Administrative Staff or a Healthcare Professional search for a patient by insurance number, date of birth, phone number, or partial first and/or last name. The system lists the matching patients and logs the search.

**Register Patient.** Administrative Staff enter a new patient's identification and contact information. They also enter the health insurance number, from RAMQ or a private insurer, when the patient has one. The system assigns the patient a unique identifier and opens the patient's clinical record.

### Clinical

**Issue Prescription (extends UC2 at extension point Prescription, if medication is needed).** During a consultation, the Healthcare Professional prescribes one or more medications, giving the dosage, frequency, duration and quantity of each. The system records the prescription as part of the consultation. Electronic transmission to pharmacies is external and out of scope.

**Request Follow-up Appointment (extends UC2 at extension point Follow-up, if follow-up is required).** During a consultation, the Healthcare Professional requests a follow-up appointment, giving the service type and a preferred time frame. The system verifies availability and scheduling constraints for that service and time frame, and records the appointment with status Requested, linked to the consultation. (G) No time slot is assigned yet; Administrative Staff assign one and confirm the request through *Confirm Pending Appointment*.

**View Clinical Record.** An authorized Healthcare Professional opens a patient's clinical record to read past consultations, diagnoses and prescriptions. The system checks the user's role before showing clinical information and logs the access. Administrative Staff do not have the same access to clinical information.

### Referrals

**Record External Referral.** The clinic receives a referral (consultation request) issued by an external physician who does not practise at the clinic. (G) External physicians do not use the system. Administrative Staff identify the patient and the external physician, then record the referral as received: the requested specialty or professional, clinical reason, priority, and date issued. The system saves the referral with an initial status, recording the external physician as the referring professional and the staff member who recorded it, and logs the activity. Staff only transcribe an existing referral; they do not medically issue one. Issuing a referral is a clinical act performed through UC3.

### Billing (uninsured services)

**Generate Invoice.** For an uninsured (paid) service, Administrative Staff record the applicable fees and generate an invoice for the patient. RAMQ claims submission is external and out of scope.

**Register Payment.** Administrative Staff register a payment against an invoice, made directly by the patient or through private insurance. The system records the payment and updates the invoice's status.

### Administration and access

**Manage Professional Schedule.** Administrative Staff create or change a professional's schedule, which defines when and at which clinic location the professional is available. These availabilities are the time slots used by *Verify Availability*.

**Manage Healthcare Professionals.** (G) The Clinic Administrator adds or updates healthcare professionals: their professional information, specialties, and the clinic locations where they practise. Professionals include family physicians, specialist physicians, nurses, and other healthcare professionals.

**Manage Clinic Locations.** (G) The Clinic Administrator adds or updates the clinic's locations.

**View Activity Log.** (G) The Clinic Administrator reviews the recorded activities: who accessed or changed which patient's information, and when. The log supports accountability and the protection of patients' confidentiality.

**Log In.** A Patient, Administrative Staff member, Healthcare Professional or Clinic Administrator identifies and authenticates themselves. The system grants the access associated with the user's role.

---

## UC1: Book Appointment

**Scope:** Appointment Tracking System

**Level:** User goal

**Primary Actor:** Administrative Staff

**Note:** In UC1, staff book for a patient who is present at the clinic, and the appointment is requested and confirmed in the same session. Confirming an appointment that was requested earlier is a separate use case, *Confirm Pending Appointment*.

**Stakeholders and Interests:**

- Patient: Wants an appointment with a suitable professional at a convenient time, without having to repeat information.
- Administrative Staff: Wants to find the patient and a free slot quickly and accurately, with no double bookings.
- Healthcare Professional: Wants appointments only within their schedule and only for services they provide; wants referral-only services booked only with a valid referral.
- Clinic: Wants efficient use of professionals' time and an accountable record of who booked what.

**Preconditions:** Administrative Staff is identified and authenticated. The patient is present at the clinic.

**Success Guarantee (Postconditions):** An Appointment is saved for the patient with a professional, a service type and a time slot. The time slot is no longer available. If the service requires a referral, the appointment is linked to the patient's valid referral (by specialty or by requested professional). The appointment is confirmed and the booking is recorded in the activity log.

**Main Success Scenario:**

1. Patient arrives at the clinic and asks to book an appointment.
2. Administrative Staff searches for the patient by insurance number, date of birth, phone number, or partial first and/or last name (include *Search Patient*).
3. System presents the matching patient.
4. Administrative Staff starts a new booking for the patient.
5. System presents the patient's summary (name, date of birth, coverage).
6. Administrative Staff specifies the requested service type and a preferred date range.
7. System verifies availability (include *Verify Availability*) and presents the professionals who provide the service and their available time slots in that range, applying the scheduling constraints (professional's schedule, clinic location, service duration).
8. Administrative Staff selects a time slot with the patient.
9. System verifies that the slot is still available and that the service's referral requirement is met, and presents the appointment details (professional, location, date, time, service).
10. Administrative Staff confirms the appointment.
11. System confirms the appointment, records the booking in the activity log, and presents the appointment number.

**Extensions (Alternative Flows):**

- 3a. No patient matches the criteria:
  1. System indicates that no patient was found.
  2. Administrative Staff either:
     - refines the search criteria, and the scenario resumes at step 2 (the search is repeated); or
     - registers the patient through *Register Patient*, and the scenario continues at step 4 with the newly registered patient.
- 3b. Several patients match:
  1. System lists the matching patients with date of birth and phone number.
  2. Administrative Staff selects the correct patient.
- 7a. No professional has an available slot for the service in the requested range:
  1. System indicates that no slots are available.
  2. Administrative Staff specifies another date range (resume at step 6), or cancels the booking; no appointment is saved.
- 9a. The selected slot was taken by another booking in the meantime:
  1. System indicates that the slot is no longer available.
  2. Scenario resumes at step 7.
- 9b. The service requires a referral and the patient has no valid referral for it: **(failure scenario, see SSD)**
  An acceptable referral concerns this patient and requests either the service's specialty or the professional of the selected slot. It may have been issued by a clinic professional (UC3) or by an external physician and recorded by staff (*Record External Referral*). (G) It must still be active, meaning not completed or cancelled.
  1. System indicates that a referral is required and that none was found.
  2. Administrative Staff cancel the booking. No appointment is saved and the slot remains available.
- 10a. Patient declines the proposed appointment before confirmation:
  1. Administrative Staff selects another slot (resume at step 8) or cancels the booking; no appointment is saved.

**Special Requirements:**

- (G) Only Administrative Staff book and confirm in a single session; a Patient submits a request (*Request Appointment*), which staff may confirm later.
- Relevant activities, including bookings, are recorded for accountability.
- (G) Availability results are presented promptly (target: under 2 seconds).

**Technology and Data Variations List:**

- 2a. Patient identified from the RAMQ card number or a private insurance number.

**Frequency of Occurrence:** Continuous during clinic hours.

**Open Issues:**

- How far in advance may appointments be booked?
- May a patient hold more than one future appointment with the same professional?

---

## UC2: Record Consultation

**Scope:** Appointment Tracking System

**Level:** User goal

**Primary Actor:** Healthcare Professional

**Stakeholders and Interests:**

- Healthcare Professional: Wants fast, complete recording of observations, diagnoses, recommendations and prescriptions.
- Patient: Wants an accurate clinical record and confidentiality of their information.
- Administrative Staff: Wants the appointment marked completed so follow-ups and billing can proceed.
- Clinic: Wants every access to clinical information traceable for accountability and confidentiality.

**Preconditions:** Healthcare Professional is identified and authenticated. An appointment exists for the patient on the current day, with status Confirmed or Arrived.

**Success Guarantee (Postconditions):** A Consultation is saved in the patient's clinical record and linked to the appointment. The clinical information entered (observations, any diagnoses, treatment recommendations and follow-up requirements) is recorded. Any prescription and its medications are recorded. The appointment is marked Completed. Access to and changes in the clinical record are recorded in the activity log.

**Main Success Scenario:**

1. Healthcare Professional selects the patient's appointment from the day's list and starts the consultation.
2. System first verifies that the professional is authorized for this appointment and that the patient's arrival has been registered (status Arrived). Only after both checks succeed does the system open the patient's clinical record and present the patient's summary and history.
3. Healthcare Professional records relevant observations, if any.
4. If a diagnosis is made, Healthcare Professional records the diagnosis. This step repeats for each additional diagnosis.
5. Healthcare Professional records any applicable treatment recommendations and follow-up requirements.
6. Healthcare Professional indicates that the consultation is complete.
7. System saves the consultation in the clinical record, marks the appointment Completed, records the activity, and presents a consultation summary.

**Extensions (Alternative Flows):**

- 2a. The professional is not authorized to access this patient's clinical information for this appointment (their role does not grant access, or (G) they are not the professional assigned to the appointment): **(failure scenario, see SSD)**
  1. System denies access without opening or displaying the clinical record, and records the denied attempt in the activity log.
  2. The use case ends; no consultation is started.
- 2b. The professional is authorized, but the patient's arrival has not been registered (status still Confirmed):
  1. System indicates that the patient has not arrived; the clinical record is not opened.
  2. The use case ends; the professional may retry once arrival is registered.
- 4a. No diagnosis is made (e.g. a nursing consultation with observations or treatment recommendations only):
  1. Healthcare Professional skips step 4; scenario continues at step 5. This is a valid, complete consultation.
- 5a. Medication is needed (*Issue Prescription*, extension point Prescription):
  1. Healthcare Professional adds a medication with dosage, frequency, duration and quantity.
  2. System records the prescription line item.
  Steps 1 and 2 repeat for each medication.
- 5b. A follow-up appointment is required (*Request Follow-up Appointment*, extension point Follow-up):
  Recording that follow-up care is needed is part of step 5. This extension covers actually requesting a follow-up appointment.
  1. Healthcare Professional requests a follow-up appointment, giving the service type and a preferred time frame.
  2. System verifies professional availability and scheduling constraints for that service and time frame, and records a follow-up appointment for the patient with status Requested, linked to this consultation.
     If no availability exists in that time frame, the system indicates it; the professional gives another time frame or does not request the follow-up.
  3. (G) No time slot is assigned yet. The request remains pending until Administrative Staff assign a slot and confirm it through *Confirm Pending Appointment*; it is not booked through UC1.
- 6a. No clinical information has been recorded:
  1. If no observations, diagnoses, treatment recommendations, or follow-up requirements have been recorded, the system indicates that at least one clinical entry is required.
  2. The scenario resumes at step 3.
  (G) This minimum of one clinical entry is a validation rule chosen by our group; the assignment does not impose it. No specific category of clinical information is required, and in particular no diagnosis.

**Special Requirements:**

- Access to clinical information depends on the user's role; Administrative Staff do not have the same access to clinical information as authorized healthcare professionals.
- Relevant activities, including access to clinical records, are recorded to support accountability and confidentiality.

**Technology and Data Variations List:**

- 4b. Diagnosis entered as a standard code (e.g. ICD-10) with a free-text description.

**Frequency of Occurrence:** Once per completed appointment.

**Open Issues:**

- Should the electronic prescription be transmitted to pharmacies in a later iteration (currently external)?

---

## UC3: Issue Referral

**Scope:** Appointment Tracking System

**Level:** User goal

**Primary Actor:** Healthcare Professional (referring professional at the clinic, e.g. a family physician). A referral received from an external physician is not issued through UC3; Administrative Staff record it through *Record External Referral*.

**Stakeholders and Interests:**

- Referring Professional: Wants the patient to see the right specialty or professional with the stated urgency.
- Patient: Wants timely access to the specialist.
- Specialist / Receiving Professional: Wants a clear clinical reason and priority before seeing the patient.
- Administrative Staff: Wants open referrals visible so referral-only appointments can be booked (UC1).

**Preconditions:** Healthcare Professional is identified and authenticated, practises at the clinic, and is authorized to issue referrals. The patient is registered in the system.

**Success Guarantee (Postconditions):** A Referral is saved for the patient, recording the referring professional, the requested specialty (or professional), the clinical reason, the priority and status Open. The referral is recorded in the activity log and becomes usable when booking (UC1).

**Main Success Scenario:**

1. Healthcare Professional starts a new referral for a patient.
2. System presents the patient's summary.
3. Healthcare Professional specifies the requested specialty.
4. (G) System verifies that the specialty is offered at a clinic location and presents the professionals practising it. (In this iteration, referrals are for services offered at the clinic; see Open Issues.)
5. Healthcare Professional enters the clinical reason and the priority.
6. Healthcare Professional submits the referral.
7. System saves the referral with status Open, records the activity, and presents the referral number.

**Extensions (Alternative Flows):**

- 3a. Healthcare Professional requests a specific professional instead of a specialty:
  1. Healthcare Professional identifies the professional.
  2. (G) System verifies the professional practises at the clinic; scenario resumes at step 5.
- 4a. The requested specialty is not offered at any clinic location: **(failure scenario, see SSD)**
  1. System indicates that the specialty is not available at the clinic.
  2. Healthcare Professional specifies another specialty (resume at step 3), or cancels the referral; no referral is saved.
- 6a. The clinical reason or priority is missing:
  1. System indicates the missing information.
  2. Scenario resumes at step 5.
- 6b. (G) The patient already has an active referral for the same specialty:
  1. System informs the professional of the existing referral.
  2. Healthcare Professional either cancels the new referral or submits it anyway (no restriction is imposed).

**Special Requirements:**

- (G) Referral priority is shown to Administrative Staff when they book referral-dependent appointments.

**Technology and Data Variations List:** None.

**Frequency of Occurrence:** Several times per day per physician.

**Open Issues:**

- Does a referral expire after a period of time?
- Can a referral be addressed to a professional outside the clinic (outgoing referral)?
