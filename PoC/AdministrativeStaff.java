package ats.domain;

//this is the administrative staff of the domain model, in the contract it is the user u authentificationn

public class AdministrativeStaff extends Person{
private final String employeeId;

public AdministrativeStaff(String employeeId,String firstName, String lastName){
super(firstName, lastName, null);
this.employeeId = employeeId;
}

public String getEmployeeId(){
return employeeId;
}   

}