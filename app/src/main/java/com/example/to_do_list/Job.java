package com.example.to_do_list;

public class Job {
    private Integer id;
    private String nameJob;

    public Job(Integer id, String nameJob) {
        this.id = id;
        this.nameJob = nameJob;
    }

    public Job() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameJob() {
        return nameJob;
    }

    public void setNameJob(String nameJob) {
        this.nameJob = nameJob;
    }
}
