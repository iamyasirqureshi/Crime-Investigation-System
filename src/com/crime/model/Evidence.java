package com.crime.model;

import java.sql.Date;

public class Evidence {
    private int evidenceId, caseId;
    private String title, type, description, foundLocation, caseNumber;
    private Date foundDate;

    public Evidence() {}

    public int getEvidenceId()         { return evidenceId; }
    public String getTitle()           { return title; }
    public String getType()            { return type; }
    public String getDescription()     { return description; }
    public String getFoundLocation()   { return foundLocation; }
    public Date getFoundDate()         { return foundDate; }
    public int getCaseId()             { return caseId; }
    public String getCaseNumber()      { return caseNumber; }

    public void setEvidenceId(int v)         { evidenceId = v; }
    public void setTitle(String v)           { title = v; }
    public void setType(String v)            { type = v; }
    public void setDescription(String v)     { description = v; }
    public void setFoundLocation(String v)   { foundLocation = v; }
    public void setFoundDate(Date v)         { foundDate = v; }
    public void setCaseId(int v)             { caseId = v; }
    public void setCaseNumber(String v)      { caseNumber = v; }
}