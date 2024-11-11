package org.liliya.hotelapp.model;

import java.util.Objects;

/**
 *  Further could be added more fields such as passport, surname, contacts etc.
 */
public class Client {
    private String name;

    public Client(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client client)) return false;
        return Objects.equals(getName(), client.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
