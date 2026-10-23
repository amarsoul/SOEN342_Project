## UC1 Book Appointment Contracts

**Name:** searchPatients(criteria)  
**Responsability:** Search and return patients matching the criteria.  
**Cross References:** UC1  
**Pre-conditons:** None.  
**Post-conditons:** None.

---

**Name:** startNewBooking(patientId)  
**Responsability:** Initialize a new booking session for a patient.  
**Cross References:** UC1  
**Pre-conditons:** The Patient exists.  
**Post-conditons:**  
- A new Booking instance 'b' was sucessfully created.  
- 'b' was assocaited with the Patient.

---

**Name:** specifyService(serviceTypeId, fromDate, toDate)  
**Responsability:** Set the desired service and time range.  
**Cross References:** UC1  
**Pre-conditons:** Booking 'b' exists.  
**Post-conditons:**  
- 'b' service type, fromDate, and toDate attributes were updated.

---

**Name:** selectTimeSlot(slotId)  
**Responsability:** Select an available time slot.  
**Cross References:** UC1  
**Pre-conditons:** Booking 'b' exists.  
**Post-conditons:**  
- 'b' was assocaited with the selected TimeSlot.

---

**Name:** confirmAppointment()  
**Responsability:** Finalize the booking and create the appointment.  
**Cross References:** UC1  
**Pre-conditons:** Booking 'b' is complete and valid.  
**Post-conditons:**  
- A new Appointment instance 'a' was succesfly created.  
- 'a' was assocaited with the Patient and the timeSlot.  
- The gimeSlot status became unavaliable.

---

**Name:** cancelBooking()  
**Responsability:** Cancel the curent booking process.  
**Cross References:** UC1  
**Pre-conditons:** Booking 'b' exist.  
**Post-conditons:**  
- Booking 'b' was deleted.



## UC2 Record Consultation Contracts

**Name:** startConsultation(appointmentId)  
**Responsability:** Open a patient clinical record for a consultation.  
**Cross References:** UC2  
**Pre-conditons:** Appointment status is arrived. The Professional is authorized.  
**Post-conditons:**  
- A consultation instance 'c' was sucessfully created.  
- 'c' was assocaited with the Appointment.

---

**Name:** recordObservations(observations)  
**Responsability:** Save clinical observations.  
**Cross References:** UC2  
**Pre-conditons:** Consultation 'c' exists.  
**Post-conditons:**  
- 'c.observations' attribute was updated.

---

**Name:** addDiagnosis(code, description)  
**Responsability:** Add a diagnosis to the consultation.  
**Cross References:** UC2  
**Pre-conditons:** Consultation 'c' exists.  
**Post-conditons:**  
- A Diagnosis instance 'd' was created.  
- 'd' was assocated with Consultation 'c'.

---

**Name:** recordRecommendations(treatment, followUp)  
**Responsability:** Save treatment and follow-up notes.  
**Cross References:** UC2  
**Pre-conditons:** Consultation ',c' exists.  
**Post-conditons:**  
- 'c.treatment' and 'c.followUp' attribute were updated.

---

**Name:** addPrescriptionItem(medicationId, dosage, frequency, duration, quantity)  
**Responsability:** Add a medication to the prescription.  
**Cross References:** UC2  
**Pre-conditons:** Consultation 'c' exists.  
**Post-conditons:**  
- A PrescriptionItem 'p' was sucessfully created.  
- 'p' was assocaited with Consultation 'c'.

---

**Name:** requestFollowUp(serviceTypeId, preferredTimeFrame)  
**Responsability:** Request a future follow-up appointment.  
**Cross References:** UC2  
**Pre-conditons:** Consultation 'c' exists.  
**Post-conditons:**  
- An Appointment request 'r' was created with status Requested.  
- 'r' was assocaited with Consultation 'c'.

---

**Name:** endConsultation()  
**Responsability:** Mark the consultation and appointment as completed.  
**Cross References:** UC2  
**Pre-conditons:** Consultation 'c' exists.  
**Post-conditons:**  
- 'c' status became Completed.  
- The assocaited Apointment status became Completed.


## UC3 Issue Referral Contracts

**Name:** startReferral(patientId)  
**Responsability:** Initialize a new referral session for a patient.  
**Cross References:** UC3  
**Pre-conditons:** The patient exist. The Professional is authorized .  
**Post-conditons:**  
- A new Referral instance 'ref' was sucessfully created.  
- 'ref' was assocaited with the Patient.

---

**Name:** setRequestedSpecialty(specialtyId)  
**Responsability:** Set the desired specialty for the referral.  
**Cross References:** UC3  
**Pre-conditons:** Referral 'ref' exists.  
**Post-conditons:**  
- 'ref' was assocaited with the Specialty.

---

**Name:** enterReferralDetails(clinicalReason, priority)  
**Responsability:** Record clinical notes and priority level for the referral.  
**Cross References:** UC3  
**Pre-conditons:** Referral 'ref'' exists.  
**Post-conditons:**  
- 'ref.clinicalReason' and 'ref..priority' attributes were updated.

---

**Name:** submitReferral()  
**Responsability:** Finalize and submit the referal to issue a reference number.  
**Cross References:** UC3  
**Pre-conditons:** Referral 'ref' detail are complete and valid.  
**Post-conditons:**  
- 'ref' status became Submitted.  
- A unique referral number was generated.

---

**Name:** cancelReferral()  
**Responsability:** Cancel the current referral proces.  
**Cross References:** UC3  
**Pre-conditons:** Referral 'ref' exist.  
**Post-conditons:**  
- Referral 'ref' was deleted.


