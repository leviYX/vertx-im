package org.im.group;


import java.util.Set;

public class GroupDomin {
    private String id;
    private String name;
    private Set<String> members;

    public GroupDomin(String id, String name, Set<String> members) {
        this.id = id;
        this.name = name;
        this.members = members;
    }

    public GroupDomin() {
    }

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

    public Set<String> getMembers() {
        return members;
    }

    public void setMembers(Set<String> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "GroupDomin{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", members=" + members +
                '}';
    }
}
