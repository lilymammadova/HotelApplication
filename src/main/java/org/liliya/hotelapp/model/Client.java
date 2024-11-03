package org.liliya.hotelapp.model;

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
}
