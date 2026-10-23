## System Operations

| # | System operation | Parameters | Returns | Use case(s) | Scenario(s) |
|---|------------------|------------|---------|-------------|-------------|
| 1 | `searchPatients` | `criteria` | Matching patients | UC1 | Success, Failure (9b) |
| 2 | `startNewBooking` | `patientId` | Patient summary | UC1 | Success, Failure (9b) |
| 3 | `specifyService` | `serviceTypeId`, `fromDate`, `toDate` | Professionals and available time slots | UC1 | Success, Failure (9b) |
| 4 | `selectTimeSlot` | `slotId` | Appointment details (success) or error: referral required, none found (9b) | UC1 | Success, Failure (9b) |
| 5 | `confirmAppointment` | none | Appointment number | UC1 | Success |
| 6 | `cancelBooking` | none | Booking cancelled | UC1 | Failure (9b) |
| 7 | `startConsultation` | `appointmentId` | Patient summary and clinical history (success) or error: access denied (2a) | UC2 | Success, Failure (2a) |
| 8 | `recordObservations` | `observations` | Observations saved | UC2 | Success |
| 9 | `addDiagnosis` | `code`, `description` | Diagnosis added | UC2 | Success |
| 10 | `recordRecommendations` | `treatment`, `followUp` | Recommendations saved | UC2 | Success |
| 11 | `addPrescriptionItem` | `medicationId`, `dosage`, `frequency`, `duration`, `quantity` | Prescription line item | UC2 | Success |
| 12 | `requestFollowUp` | `serviceTypeId`, `preferredTimeFrame` | Follow-up appointment (Requested) | UC2 | Success |
| 13 | `endConsultation` | none | Consultation summary | UC2 | Success |
| 14 | `startReferral` | `patientId` | Patient summary | UC3 | Success, Failure (4a) |
| 15 | `setRequestedSpecialty` | `specialtyId` | Professionals practising the specialty (success) or error: specialty not offered (4a) | UC3 | Success, Failure (4a) |
| 16 | `enterReferralDetails` | `clinicalReason`, `priority` | Details recorded | UC3 | Success |
| 17 | `submitReferral` | none | Referral number | UC3 | Success |
| 18 | `cancelReferral` | none | Referral cancelled | UC3 | Failure (4a) |
