## System Operations

### UC1 Book Appointment
- searchPatients(criteria: SearchCriteria)
- startNewBooking(patientId: PatientID)
- specifyService(serviceTypeId: String, fromDate: Date, toDate: Date)
- selectTimeSlot(slotId: String)
- confirmAppointment()
- cancelBooking()

### UC2 Record Consultation
- startConsultation(appointmentId: String)
- recordObservations(observations: String)
- addDiagnosis(code: String, description: String)
- recordRecommendations(treatment: String, followUp: String)
- addPrescriptionItem(medicationId: String, dosage: String, frequency: String, duration: String, quantity: Integer)
- requestFollowUp(serviceTypeId: String, preferredTimeFrame: String)
- endConsultation()

### UC3 Issue Referral
- startReferral(patientId: PatientID)
- setRequestedSpecialty(specialtyId: String)
- setRequestedProfessional(professionalId: String)
- enterReferralDetails(clinicalReason: String, priority: Priority)
- submitReferral()
- cancelReferral()
