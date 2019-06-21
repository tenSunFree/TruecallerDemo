package com.home.truecallerdemo.eventbus;

import java.util.List;

public class ContactPersonShortMessageServiceEvent {

    private String name;

    private List<String> number;

    public ContactPersonShortMessageServiceEvent(String name, List<String> number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public List<String> getNumber() {
        return number;
    }
}
