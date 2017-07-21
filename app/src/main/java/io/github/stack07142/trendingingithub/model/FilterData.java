package io.github.stack07142.trendingingithub.model;

import java.util.ArrayList;
import java.util.List;

public class FilterData {

    List<String> languageList = new ArrayList<>();
    List<String> createdList = new ArrayList<>();

    public FilterData() {

        languageList.add("java");
        languageList.add("objective-c");
        languageList.add("swift");
        languageList.add("groovy");
        languageList.add("python");
        languageList.add("ruby");
        languageList.add("c");

        createdList.add("today");
        createdList.add("this week");
        createdList.add("this month");
        createdList.add("this year");
    }

    public List<String> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(List<String> languageList) {
        this.languageList = languageList;
    }

    public List<String> getCreatedList() {
        return createdList;
    }

    public void setCreatedList(List<String> createdList) {
        this.createdList = createdList;
    }
}
