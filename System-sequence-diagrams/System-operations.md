## System Operations

### UC1 Book Appointment
- searchPatients(criteria)
- startNewBooking(patientId)
- specifyService(serviceTypeId, fromDate, toDate)
- selectTimeSlot(slotId)
- confirmAppointment()
- cancelBooking()
- addToWaitingList(reason, priority, professionalId [0..1]) (extension 7a, Iteration II)

### UC2 Record Consultation
- startConsultation(appointmentId)
- recordObservations(observations)
- addDiagnosis(code, description)
- recordRecommendations(treatment, followUp)
- addPrescriptionItem(medicationId, dosage, frequency, duration, quantity)
- requestFollowUp(serviceTypeId, preferredTimeFrame)
- endConsultation()

### UC3 Issue Referral
- startReferral(patientId)
- setRequestedSpecialty(specialtyId)
- setRequestedProfessional(professionalId)
- enterReferralDetails(clinicalReason, priority)
- submitReferral()
- cancelReferral()

### UC4 Verify Availability (Iteration II)
- searchAvailability(criteria, fromDate, toDate)

### UC5 Make Availability (Iteration II)
- startAvailability(professionalId)
- specifyAvailability(locationId, fromDate, toDate, repetition, daysOfWeek, dayOfMonth, startTime, endTime, slotDurationMinutes)
- confirmAvailability()
- cancelAvailability()

### Import Availability from CSV (Iteration II)
- importAvailability(filePath)

### Export Availability to CSV (Iteration II)
- exportAvailability(filePath)

### Request Waiting List Placement (Iteration II)
- requestWaitingListPlacement(patientId, professionalId [0..1], specialtyId [0..1], serviceTypeId [0..1], fromDate [0..1], toDate [0..1], reason, priority [0..1])

### Cancel Appointment and Identify Eligible Waitlisted Patients (Iteration II)
- cancelAppointment(appointmentId)

### Offer Appointment by Phone, Remove Patient, Update Priority (Iteration II)
- offerAppointment(entryId, slotId)
- declineOffer(entryId)
- recordOfferNotAnswered(entryId)
- removeFromWaitingList(entryId)
- updateWaitingListPriority(entryId, priority)

### Resources (Iteration II)
- addResource(name, type, quantity, locationId [0..1])
- defineResourceRequirement(serviceTypeId, resourceName, quantityNeeded)
- Pending (no contract yet): updateResource, retireResource

