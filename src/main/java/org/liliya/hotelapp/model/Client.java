package org.liliya.hotelapp.model;

import java.io.Serializable;
import java.util.Objects;

/**
 *  Further could be added more fields such as passport, surname, contacts etc.
 */
public class Client implements Serializable {
    private int id;
    private String name;

    public Client(String name) {
        this.name = name;
    }

    public Client(int id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client client)) return false;
        return getId() == client.getId() && Objects.equals(getName(), client.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
