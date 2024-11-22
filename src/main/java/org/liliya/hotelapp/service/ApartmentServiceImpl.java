package org.liliya.hotelapp.service;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.liliya.hotelapp.configuration.Configuration;
import org.liliya.hotelapp.model.Apartment;
import org.liliya.hotelapp.model.Client;
import org.liliya.hotelapp.model.ReservationStatus;

import java.util.ArrayList;
import java.util.List;

public class ApartmentServiceImpl implements ApartmentService {
    private final SessionFactory sessionFactory;
    private final Configuration configuration;

    public ApartmentServiceImpl(SessionFactory sessionFactory) {
        this.configuration = Configuration.getInstance();
        this.sessionFactory = sessionFactory;
    }

    @Override
    public int register(double price) {
        int generatedId = -1;
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Apartment apartment = new Apartment();
            apartment.setPrice(price);
            apartment.setReservationStatus(ReservationStatus.AVAILABLE);
            apartment.setClient(null);

            generatedId = (int) session.save(apartment);

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return generatedId;
    }

    @Override
    public boolean reserve(Client client) {
        if (!configuration.statusChangeAvailability()) {
            return false;
        }

        Transaction transaction;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            int clientId = insertClient(session, client);
            if (clientId == -1) {
                transaction.rollback();
                return false;
            }
            int apartmentId = findAvailableApartment(session);
            if (apartmentId == -1) {
                transaction.rollback();
                return false;
            }
            if (updateApartmentAfterReserve(session, apartmentId, clientId)) {
                transaction.commit();
                return true;
            } else {
                transaction.rollback();
            }
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean release(int id) {
        if (!configuration.statusChangeAvailability()) {
            return false;
        }
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Integer clientId = findClientByApartmentId(session, id);
            if (clientId != null) {
                deleteClient(session, clientId);
            }

            if (updateApartmentAfterRelease(id, session)) {
                transaction.commit();
                return true;
            } else {
                transaction.rollback();
            }
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return false;
    }

    @Override
    public List<Apartment> getPaginatedAndSortedApartments(int page, int size, String sortBy) {
        List<Apartment> apartments = new ArrayList<>();

        try (Session session = sessionFactory.openSession()) {
            int offset = (page - 1) * size;

            String hql = "FROM Apartment a LEFT JOIN FETCH a.client ORDER BY a." + sortBy;
            apartments = session.createQuery(hql, Apartment.class)
                    .setFirstResult(offset)
                    .setMaxResults(size)
                    .getResultList();
        } catch (HibernateException e) {
            e.printStackTrace();
        }

        return apartments;
    }

    private int insertClient(Session session, Client client) {
        try {
            return (int) session.save(client);
        } catch (HibernateException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int findAvailableApartment(Session session) {
        List<Apartment> availableApartments = session.createQuery("FROM Apartment where reservationStatus =:status", Apartment.class)
                .setParameter("status", ReservationStatus.AVAILABLE)
                .setMaxResults(1)
                .getResultList();

        if (!availableApartments.isEmpty()) {
            return availableApartments.get(0).getId();
        }
        return -1;
    }

    private boolean updateApartmentAfterReserve(Session session, int apartmentId, int clientId) {
        try {
            Apartment apartment = session.get(Apartment.class, apartmentId);
            if (apartment != null) {
                if (apartment.getClient() != null) {
                    return false;
                }

                apartment.setReservationStatus(ReservationStatus.RESERVED);
                Client client = session.get(Client.class, clientId);
                apartment.setClient(client);
                session.merge(apartment);
                return true;
            }
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Integer findClientByApartmentId(Session session, int apartmentId) {
        try {
            Apartment apartment = session.get(Apartment.class, apartmentId);
            if (apartment != null && apartment.getClient() != null) {
                return apartment.getClient().getId();
            }
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void deleteClient(Session session, int clientId) {
        Client client = session.get(Client.class, clientId);
        if (client != null) {
            session.remove(client);
        }
    }

    private boolean updateApartmentAfterRelease(int apartmentId, Session session) {
        Apartment apartment = session.get(Apartment.class, apartmentId);
        if (apartment != null) {
            apartment.setReservationStatus(ReservationStatus.AVAILABLE);
            apartment.setClient(null);
            session.merge(apartment);
            return true;
        }
        return false;
    }
}