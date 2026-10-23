## System Operations

**UC1 Book Appointment**
- searchPatients(criteria)
- startNewBooking(patientId)
- specifyService(serviceTypeId, fromDate, toDate)
- selectTimeSlot(slotId)
- confirmAppointment()
- cancelBooking()

**UC2 Record Consultation**
- startConsultation(appointmentId)
- recordObservations(observations)
- addDiagnosis(code, description)
- recordRecommendations(treatment, followUp)
- addPrescriptionItem(medicationId, dosage, frequency, duration, quantity)
- requestFollowUp(serviceTypeId, preferredTimeFrame)
- endConsultation()

**UC3 Issue Referral**
- startReferral(patientId)
- setRequestedSpecialty(specialtyId)
- setRequestedProfessional()
- enterReferralDetails(clinicalReason, priority)
- submitReferral()
- cancelReferral()
