package com.mita.fmipaschedule.model;

import java.util.List;

public class FakultasModel {
    private String id, name;
    private List<FakultasOpensModel> opens;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FakultasOpensModel> getOpens() {
        return opens;
    }

    public void setOpens(List<FakultasOpensModel> opens) {
        this.opens = opens;
    }
}
