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
- A new Appointment instance 'a' was sucessfully created.  
- 'a' was assocaited with the Patient and the TimeSlot.  
- The TimeSlot status became unavaliable.

---

**Name:** cancelBooking()  
**Responsability:** Cancel the curent booking process.  
**Cross References:** UC1  
**Pre-conditons:** Booking 'b' exist.  
**Post-conditons:**  
- Booking 'b' was deleted.
