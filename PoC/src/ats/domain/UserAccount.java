package ats.domain;

// this is the user account of the domain model, it is the user u in the contracts
public class UserAccount {
    
private final String username;
private final String role;
private final Person person;



public UserAccount(String username, String role, Person person) {
this.username = username;
this.role = role;
this.person = person;
}



public String getUsername() { 
return username; 
}


public String getRole() { 
return role; 
}


public Person getPerson() { 
return person; 
}



@Override
public String toString() {
return person.getFullName() + " (" + username + ")";
}

}