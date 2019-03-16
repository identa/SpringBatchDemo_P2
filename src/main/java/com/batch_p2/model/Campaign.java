package com.batch_p2.model;

import java.util.Date;

public class Campaign {
    private int id;

    private String name;

    private int statusID;

    private Date startDate;

    private Date endDate;

    private double budget;

    private double bid;

    public Campaign(int id, String name, int statusID, Date startDate, Date endDate, double budget, double bid) {
        this.id = id;
        this.name = name;
        this.statusID = statusID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.bid = bid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }
}
