package ats.domain;

// this is the medicalspeciality for the domain model
public class MedicalSpecialty {
    private final String name;
    private final String code;

    public MedicalSpecialty(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

  @Override
  public String toString(){
    return name;
  }
}