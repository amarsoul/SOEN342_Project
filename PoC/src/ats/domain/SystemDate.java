package ats.domain;

import java.time.LocalDate;

// this is the date of the system
public final class SystemDate {
    
private static LocalDate fixed;



private SystemDate() {
}



public static LocalDate today() {
if (fixed != null) {
return fixed;
}
return LocalDate.now();
}


public static void setToday(LocalDate date) {
fixed = date;
}

}