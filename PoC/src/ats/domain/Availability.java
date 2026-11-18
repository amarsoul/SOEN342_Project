package ats.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

// this is the availability from the domain model. handles the repetition pattern and generates timeslots

public class Availability {
private final HealthcareProfessional professional; 
private ClinicLocation location;                   
private LocalDate fromDate;
private LocalDate toDate;
private Repetition repetition;
private Set<DayOfWeek> daysOfWeek = EnumSet.noneOf(DayOfWeek.class);
private Integer dayOfMonth;
private LocalTime startTime;
private LocalTime endTime;
private int slotDurationMinutes;
private boolean specified;

Availability(HealthcareProfessional professional){
this.professional = professional;
}

public HealthcareProfessional getProfessional(){
return professional;
}
public ClinicLocation getLocation(){
return location;
}
public LocalDate getFromDate(){
return fromDate;
}
public LocalDate getToDate(){
return toDate;
}
public Repetition getRepetition(){
return repetition;
}
public Set<DayOfWeek> getDaysOfWeek(){
return daysOfWeek;
}
public Integer getDayOfMonth(){
return dayOfMonth;
}
public LocalTime getStartTime(){      
return startTime;
}
public LocalTime getEndTime(){
return endTime;
}
public int getSlotDurationMinutes(){
return slotDurationMinutes;
}
public boolean isSpecified(){
return specified;
}
// it specifies contract validates inputs before setting the attributes
public void specify(ClinicLocation cl, LocalDate from, LocalDate to, Repetition rep, Set<DayOfWeek> days,
Integer dom, LocalTime start, LocalTime end, int duration) {
    
LocalDate today = SystemDate.today();
if (!professional.practisesAt(cl)){
throw new DomainException("invalid availability period: " + professional.getFullName()
+ " does not practise at " + cl.getName());
}
if (from == null || to == null || from.isAfter(to) || to.isBefore(today)){

throw new DomainException("invalid availability period: invalid date range (4a)");

}
if (rep == Repetition.WEEKLY && (days == null || days.isEmpty())){
throw new DomainException("invalid availability period: weekly needs at least one day of the week (5a)");
}
if (rep == Repetition.MONTHLY && (dom == null || dom < 1 || dom > 31)){
throw new DomainException("invalid availability period: monthly needs a day of the month 1-31 (5a)");
}
if (duration <= 0 || start == null || end == null || !start.isBefore(end)){
throw new DomainException("invalid availability period: end time must be after start time (6a)");
}

long minutes = java.time.Duration.between(start, end).toMinutes();
if (minutes % duration != 0){
throw new DomainException("invalid availability period: the period is not a whole number of "
+ duration + "-minute slots (6a)");
}

List<SlotPeriod> periods = computeSlotPeriods(from, to, rep, days, dom, start, end, duration);
if (periods.isEmpty()){
throw new DomainException("invalid availability period: no date in the range matches the pattern (7a)");
}

this.location = cl;
this.fromDate = from;
this.toDate = to;
this.repetition = rep;
this.daysOfWeek = EnumSet.noneOf(DayOfWeek.class);
if (days != null) {
this.daysOfWeek.addAll(days);
}
this.dayOfMonth = dom;
this.startTime = start;
this.endTime = end;
this.slotDurationMinutes = duration;
this.specified = true;
}

// The periods this availability generates: every matching date, every slot between start and end. 
public List<SlotPeriod> computeSlotPeriods(){
return computeSlotPeriods(fromDate, toDate, repetition, daysOfWeek, dayOfMonth, startTime, endTime, slotDurationMinutes);
}

private static List<SlotPeriod> computeSlotPeriods(LocalDate from, LocalDate to, Repetition rep, Set<DayOfWeek> days,
Integer dom, LocalTime start, LocalTime end, int duration){
List<SlotPeriod> periods = new ArrayList<>();
for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
boolean matches = switch (rep) {
case DAILY -> true;
case WEEKLY -> days != null && days.contains(d.getDayOfWeek());
case MONTHLY -> dom != null && d.getDayOfMonth() == dom;
};
        
if (!matches) continue;
        
for (LocalTime t = start; !t.plusMinutes(duration).isAfter(end) && t.isBefore(end); t = t.plusMinutes(duration)) {
periods.add(new SlotPeriod(d, t, t.plusMinutes(duration)));
}
}
return periods;
}


// slot of the professional (Creator: an Availability Generates its TimeSlots). Returns the slots created.
public List<TimeSlot> confirm(){
if (!specified){
throw new DomainException("specify the availability first");
}
List<TimeSlot> created = new ArrayList<>();
for (SlotPeriod period : computeSlotPeriods()) {
if (professional.overlapsExistingSlot(period)) {
continue; // overlapping periods are skipped
}
TimeSlot ts = new TimeSlot(TimeSlot.nextId(), period.date(), period.start(), period.end(),
professional, location, this);
professional.addTimeSlot(ts);
created.add(ts);
}
professional.addAvailability(this);
return created;
}

// Periods that overlap a TimeSlot the professional is already Available
public List<SlotPeriod> findOverlappingPeriods(){
List<SlotPeriod> overlapping = new ArrayList<>();
for (SlotPeriod p : computeSlotPeriods()) {
if (professional.overlapsExistingSlot(p)) {
overlapping.add(p);
}
}
return overlapping;
}

public String describePattern(){
return switch (repetition) {
case DAILY -> "daily";
case WEEKLY -> "weekly on " + daysOfWeek;
case MONTHLY -> "monthly on day " + dayOfMonth;
};
}
}