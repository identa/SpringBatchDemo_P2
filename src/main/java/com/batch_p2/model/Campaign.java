package com.batch_p2.model;

import java.util.Date;

public class Campaign {
    private int id;

    private String name;

    private int statusID;

    private Date start_date;

    private Date end_date;

    private double budget;

    private double bid;

    public Campaign(int id, String name, int statusID, Date start_date, Date end_date, double budget, double bid) {
        this.id = id;
        this.name = name;
        this.statusID = statusID;
        this.start_date = start_date;
        this.end_date = end_date;
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

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
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
