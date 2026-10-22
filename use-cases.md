# Use Case Model
...

## Actors

| Actor | Type | Goal |
| --- | --- | --- |
| Administrative Staff | Primary | Registers patients, books and manages appointments, registers arrivals handles invoices and payments. |
| Patient | Primary | Requests, reschedules or cancels own appointments. |
| Healthcare Professional | Primary | Conducts consultations, records clinical information, issues prescriptions and referrals. |
| External Physician | Primary (specialization of Healthcare Professional) | Refers a patient to the clinic; does not practise at the clinic. |
| Clinic Administrator | Primary | Manages professionals and clinic locations, reviews the activity log. |

RAMQ claims submission, electronic prescribing to pharmacies and provincial systems are external at this phase and are not part of the system boundary.

## Critical and non-critical use cases

| Use case | Critical? | Primary actor(s) |
| --- | --- | --- |
| UC1 Book Appointment | Critical | Administrative Staff, Patient |
| UC2 Record Consultation | Critical | Healthcare Professional |
| UC3 Issue Referral | Critical | Healthcare Professional |
| Search Patient (included) | Non-critical | Administrative Staff |
| Register Patient | Non-critical | Administrative Staff |
| Confirm Appointment | Non-critical | Administrative Staff |
| Reschedule Appointment | Non-critical | Administrative Staff, Patient |
| Cancel Appointment | Non-critical | Administrative Staff, Patient |
| Register Patient Arrival | Non-critical | Administrative Staff |
| Issue Prescription (extends UC2) | Non-critical | Healthcare Professional |
| Request Follow-up Appointment (extends UC2) | Non-critical | Healthcare Professional |
| View Clinical Record | Non-critical | Healthcare Professional |
| Generate Invoice | Non-critical | Administrative Staff |
| Record Payment | Non-critical | Administrative Staff |
| Manage Professional Schedule | Non-critical | Administrative Staff |
| Manage Healthcare Professionals | Non-critical | Clinic Administrator |
| Manage Clinic Locations | Non-critical | Clinic Administrator |
| View Activity Log | Non-critical | Clinic Administrator |
| Log In | Non-critical | All human actors |

The three critical use cases carry the core value of the system (getting a patient in front of the right professional, capturing clinical information, and routing patients between professionals) and the highest risk (scheduling constraints, confidentiality of clinical data, referral rules). They are specified below in fully-dressed format.

---

## UC1: Book Appointment

**Scope:** Appointment Tracking System

**Level:** User goal

**Primary Actor:** Administrative Staff (a Patient may also book on their own behalf)

**Stakeholders and Interests:**

- Patient: Wants an appointment with a suitable professional at a convenient time, without having to repeat information.
- Administrative Staff: Wants to find the patient and a free slot quickly and accurately, with no double bookings.
- Healthcare Professional: Wants appointments only within their schedule and only for services they provide; wants referral-only services booked only with a valid referral.
- Clinic: Wants efficient use of professionals' time and an accountable record of who booked what.

**Preconditions:** Administrative Staff is identified and authenticated. The patient is registered in the system.

**Success Guarantee (Postconditions):** An Appointment is saved for the patient with a professional, a service type and a time slot. The time slot is no longer available. If the service requires a referral, the appointment is linked to the patient's open referral. The appointment is confirmed and the booking is recorded in the activity log.

**Main Success Scenario:**

1. Patient arrives at the clinic and asks to book an appointment.
2. Administrative Staff searches for the patient by insurance number, date of birth, phone number, or partial first and/or last name (include *Search Patient*).
3. System presents the matching patient.
4. Administrative Staff starts a new booking for the patient.
5. System presents the patient's summary (name, date of birth, coverage).
6. Administrative Staff specifies the requested service type and a preferred date range.
7. System presents the professionals who provide the service and their available time slots in that range, applying the scheduling constraints (professional's schedule, clinic location, service duration).
8. Administrative Staff selects a time slot with the patient.
9. System verifies that the slot is still available and that the service's referral requirement is met, and presents the appointment details (professional, location, date, time, service).
10. Administrative Staff confirms the appointment.
11. System confirms the appointment, records the booking in the activity log, and presents the appointment number.

**Extensions (Alternative Flows):**

- 3a. No patient matches the criteria:
  1. System indicates that no patient was found.
  2. Administrative Staff refines the search, or registers the patient (*Register Patient*), and the scenario resumes at step 3.
- 3b. Several patients match:
  1. System lists the matching patients with date of birth and phone number.
  2. Administrative Staff selects the correct patient.
- 7a. No professional has an available slot for the service in the requested range:
  1. System indicates that no slots are available.
  2. Administrative Staff specifies another date range (resume at step 7), or the use case ends with no appointment booked.
- 9a. The selected slot was taken by another booking in the meantime:
  1. System indicates that the slot is no longer available.
  2. Scenario resumes at step 7.
- 9b. The service requires a referral and the patient has no open referral for its specialty: **(failure scenario, see SSD)**
  1. System indicates that a referral is required and that none was found.
  2. The booking is abandoned; no appointment is saved and the slot remains available.
- 10a. Patient declines the proposed appointment before confirmation:
  1. Administrative Staff selects another slot (resume at step 8) or abandons the booking; no appointment is saved.

**Special Requirements:**

- Availability results presented in under 2 seconds.
- Only authorized roles (Administrative Staff, or the Patient for their own record) may book.
- All bookings are recorded in the activity log for accountability.

**Technology and Data Variations List:**

- 2a. Patient identified from the RAMQ card number or a private insurance number.
- 1a. The Patient may book on their own (step 1 is then a self-service request through the command-line interface).

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

**Preconditions:** Healthcare Professional is identified and authenticated. The appointment exists and the patient's arrival has been registered (status Arrived).

**Success Guarantee (Postconditions):** A Consultation is saved in the patient's clinical record and linked to the appointment. Observations, diagnoses, treatment recommendations and follow-up requirements are recorded. Any prescription and its medications are recorded. The appointment is marked Completed. Access to and changes in the clinical record are recorded in the activity log.

**Main Success Scenario:**

1. Healthcare Professional selects the patient's appointment from the day's list and starts the consultation.
2. System verifies that the professional is authorized for this appointment, opens the patient's clinical record, and presents the patient's summary and history.
3. Healthcare Professional records observations.
4. Healthcare Professional records a diagnosis.
   Healthcare Professional repeats step 4 for each diagnosis.
5. Healthcare Professional records treatment recommendations and follow-up requirements.
6. Healthcare Professional indicates that the consultation is complete.
7. System saves the consultation in the clinical record, marks the appointment Completed, records the activity, and presents a consultation summary.

**Extensions (Alternative Flows):**

- 2a. The professional is not the one assigned to the appointment, or their role does not grant access to clinical information: **(failure scenario, see SSD)**
  1. System denies access and records the denied attempt in the activity log.
  2. The use case ends; no consultation is started.
- 2b. The patient's arrival has not been registered:
  1. System indicates that the patient has not arrived.
  2. The use case ends; the professional may retry once arrival is registered.
- 5a. Medication is needed (*Issue Prescription*, extension point Prescription):
  1. Healthcare Professional adds a medication with dosage, frequency, duration and quantity.
  2. System records the prescription line item.
  Steps 1 and 2 repeat for each medication.
- 5b. A follow-up appointment is required (*Request Follow-up Appointment*, extension point Follow-up):
  1. Healthcare Professional records the follow-up requirement.
  2. System flags the patient for a follow-up booking (later booked through UC1).
- 6a. No diagnosis has been recorded:
  1. System indicates that at least one diagnosis is required.
  2. Scenario resumes at step 4.

**Special Requirements:**

- Clinical information is visible only to authorized healthcare professionals, never to Administrative Staff.
- Every access to a clinical record is logged.

**Technology and Data Variations List:**

- 4a. Diagnosis entered as a standard code (like ICD-10) with a free-text description.

**Frequency of Occurrence:** Once per completed appointment.

**Open Issues:**

- Should the electronic prescription be transmitted to pharmacies in a later iteration (currently external)?

---

## UC3: Issue Referral

**Scope:** Appointment Tracking System

**Level:** User goal

**Primary Actor:** Healthcare Professional (referring professional like a family physician)

**Stakeholders and Interests:**

- Referring Professional: Wants the patient to see the right specialty or professional with the stated urgency.
- Patient: Wants timely access to the specialist.
- Specialist / Receiving Professional: Wants a clear clinical reason and priority before seeing the patient.
- Administrative Staff: Wants open referrals visible so referral-only appointments can be booked (UC1).

**Preconditions:** Healthcare Professional is identified and authenticated. The patient is registered in the system.

**Success Guarantee (Postconditions):** A Referral is saved for the patient, recording the referring professional, the requested specialty (or professional), the clinical reason, the priority and status Open. The referral is recorded in the activity log and becomes usable when booking (UC1).

**Main Success Scenario:**

1. Healthcare Professional starts a new referral for a patient.
2. System presents the patient's summary.
3. Healthcare Professional specifies the requested specialty.
4. System verifies that the specialty is offered at a clinic location and presents the professionals practising it.
5. Healthcare Professional enters the clinical reason and the priority.
6. Healthcare Professional submits the referral.
7. System saves the referral with status Open, records the activity, and presents the referral number.

**Extensions (Alternative Flows):**

- 1a. The referring professional is an External Physician who does not practise at the clinic:
  1. Administrative Staff enters the referral on the external physician's behalf, identifying the external physician (registered if unknown).
  2. Scenario resumes at step 2.
- 3a. Healthcare Professional requests a specific professional instead of a specialty:
  1. Healthcare Professional identifies the professional.
  2. System verifies the professional practises at the clinic; scenario resumes at step 5.
- 4a. The requested specialty is not offered at any clinic location: **(failure scenario, see SSD)**
  1. System indicates that the specialty is not available at the clinic.
  2. Healthcare Professional specifies another specialty (resume at step 3), or abandons the referral; no referral is saved.
- 6a. The clinical reason or priority is missing:
  1. System indicates the missing information.
  2. Scenario resumes at step 5.
- 6b. The patient already has an open referral for the same specialty:
  1. System warns of the existing referral.
  2. Healthcare Professional either abandons the new referral or confirms it.

**Special Requirements:**

- Urgent referrals are listed first for Administrative Staff when booking.

**Technology and Data Variations List:** None.

**Frequency of Occurrence:** Several times per day per physician.

**Open Issues:**

- Does a referral expire after a period of time?
- Can a referral be addressed to a professional outside the clinic (outgoing referral)?

