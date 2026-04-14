package com.crime.model;

public class Suspect {
    private int suspectId, age, caseId;
    private String name, gender, address, background, dangerLevel, caseNumber;

    public Suspect() {}

    public Suspect(int suspectId, String name, int age, String gender,
                   String address, String background, String dangerLevel, int caseId) {
        this.suspectId = suspectId; this.name = name; this.age = age;
        this.gender = gender; this.address = address; this.background = background;
        this.dangerLevel = dangerLevel; this.caseId = caseId;
    }

    public int getSuspectId()        { return suspectId; }
    public String getName()          { return name; }
    public int getAge()              { return age; }
    public String getGender()        { return gender; }
    public String getAddress()       { return address; }
    public String getBackground()    { return background; }
    public String getDangerLevel()   { return dangerLevel; }
    public int getCaseId()           { return caseId; }
    public String getCaseNumber()    { return caseNumber; }

    public void setSuspectId(int v)        { suspectId = v; }
    public void setName(String v)          { name = v; }
    public void setAge(int v)              { age = v; }
    public void setGender(String v)        { gender = v; }
    public void setAddress(String v)       { address = v; }
    public void setBackground(String v)    { background = v; }
    public void setDangerLevel(String v)   { dangerLevel = v; }
    public void setCaseId(int v)           { caseId = v; }
    public void setCaseNumber(String v)    { caseNumber = v; }
}
