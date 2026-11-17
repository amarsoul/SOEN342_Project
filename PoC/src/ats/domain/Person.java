package ats.domain;
// we start with the abstract person of the domain model
public abstract class  Person{
private final String firstName;
private final String lastName;
private final String phone;


protected Person(String firstName, String lastName, String phone){
this.firstName = firstName;
this.lastName = lastName;
this.phone = phone;
}


public String getFirstName(){
return firstName;
}
public String getLastName(){
return lastName;
}
public String getPhone(){      
return phone;
}  

public String getFullName(){
return firstName + " " + lastName;
}

public boolean  hasName (String firstName, String lastName){
return this.firstName.equalsIgnoreCase(firstName.trim()) && this.lastName.equalsIgnoreCase(lastName.trim());
}
}