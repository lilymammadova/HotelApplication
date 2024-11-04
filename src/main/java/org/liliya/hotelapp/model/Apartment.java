package org.liliya.hotelapp.model;

public class Apartment {
    private int id;
    private double price;
    private ReservationStatus reservationStatus;
    private Client client;

    public Apartment(int id, double price, ReservationStatus reservationStatus, Client client) {
        this.id = id;
        this.price = price;
        this.reservationStatus = reservationStatus;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Apartment{" +
                "id=" + id +
                ", price=" + price +
                ", reservationStatus='" + reservationStatus + '\'' +
                ", clientName='" + client + '\'' +
                '}';
    }
}
