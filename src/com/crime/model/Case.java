package com.crime.model;

import java.sql.Date;

public class Case {
    private int caseId;
    private String caseNumber, title, description, status, location, officerName;
    private Date dateReported;

    public Case() {}

    public Case(int caseId, String caseNumber, String title, String description,
                String status, String location, Date dateReported, String officerName) {
        this.caseId = caseId; this.caseNumber = caseNumber; this.title = title;
        this.description = description; this.status = status; this.location = location;
        this.dateReported = dateReported; this.officerName = officerName;
    }

    public int getCaseId()         { return caseId; }
    public String getCaseNumber()  { return caseNumber; }
    public String getTitle()       { return title; }
    public String getDescription() { return description; }
    public String getStatus()      { return status; }
    public String getLocation()    { return location; }
    public Date getDateReported()  { return dateReported; }
    public String getOfficerName() { return officerName; }

    public void setCaseId(int v)         { caseId = v; }
    public void setCaseNumber(String v)  { caseNumber = v; }
    public void setTitle(String v)       { title = v; }
    public void setDescription(String v) { description = v; }
    public void setStatus(String v)      { status = v; }
    public void setLocation(String v)    { location = v; }
    public void setDateReported(Date v)  { dateReported = v; }
    public void setOfficerName(String v) { officerName = v; }

    public String toString() { return caseNumber + " - " + title; }
}