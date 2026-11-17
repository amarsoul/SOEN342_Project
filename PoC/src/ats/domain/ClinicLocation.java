package ats.domain;

// this is for the clinic location of the domainn model

public class ClinicLocation {
    private final String name;
    private final String address;
    private final String phone;

    public ClinicLocation(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

  @Override
  public String toString(){
    return name;
  }
}