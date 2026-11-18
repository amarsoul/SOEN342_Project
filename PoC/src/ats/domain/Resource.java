package ats.domain;

import java.util.ArrayList;
import java.util.List;

// this is the resource of the domain model , it is allocated to appointments
public class Resource {
    
private final String name;
private final int quantity;
private final List<Appointment> allocated = new ArrayList<>();



public Resource(String name, int quantity) {
this.name = name;
this.quantity = quantity;
}



public String getName() { 
return name; 
}


public int getQuantity() { 
return quantity; 
}



public void allocate(Appointment a) {
if (!allocated.contains(a)) {
allocated.add(a);
}
}


public void deallocate(Appointment a) {
allocated.remove(a);
}



// it is to check if there is enough quantity left for this slot
public boolean hasCapacity(TimeSlot ts, int q, Appointment a) {
return quantityInUse(ts, a) + q <= quantity;
}



// the quantity used by the other active appointments on an overlapping slot
private int quantityInUse(TimeSlot ts, Appointment a) {
    
int used = 0;

for (Appointment other : allocated) {
    
if (other == a || !other.isActive() || other.getTimeSlot() == null) {
continue;
}

TimeSlot o = other.getTimeSlot();

if (o.overlaps(ts.getDate(), ts.getStartTime(), ts.getEndTime())) {
used += other.getServiceType().getQuantityNeeded(this);
}

}

return used;
}



@Override
public String toString() {
return name + " (x" + quantity + ")";
}

}