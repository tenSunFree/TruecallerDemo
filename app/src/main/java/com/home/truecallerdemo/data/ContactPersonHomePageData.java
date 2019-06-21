package com.home.truecallerdemo.data;

import java.util.List;

public class ContactPersonHomePageData {

    private boolean isShowLabel; // 是否顯示標籤, 比如說A B C D..等

    private String name;

    private List<String> number;

    public List<String> getNumber() {
        return number;
    }

    public void setNumber(List<String> number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowLabel() {
        return isShowLabel;
    }

    public void setShowLabel(boolean showLabel) {
        isShowLabel = showLabel;
    }
}
