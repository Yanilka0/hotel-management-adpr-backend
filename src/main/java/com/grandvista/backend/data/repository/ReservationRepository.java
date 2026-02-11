package com.grandvista.backend.data.repository;

import com.grandvista.backend.data.database.MongoDBConnection;
import com.grandvista.backend.data.model.Reservation;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class ReservationRepository {
    private final MongoCollection<Document> collection;

    public ReservationRepository() {
        this.collection = MongoDBConnection.getInstance()
                .getDatabase()
                .getCollection("reservations");
    }

    public Reservation save(Reservation reservation) {
        Document doc = new Document();
        doc.append("guestId", reservation.getGuestId())
                .append("roomType", reservation.getRoomType())
                .append("numberOfPeople", reservation.getNumberOfPeople())
                .append("checkInDate", reservation.getCheckInDate().toString())
                .append("checkOutDate", reservation.getCheckOutDate().toString())
                .append("bedType", reservation.getBedType())
                .append("breakfastIncluded", reservation.isBreakfastIncluded())
                .append("status", reservation.getStatus())
                .append("createdAt", reservation.getCreatedAt().toString());

        collection.insertOne(doc);
        reservation.setId(doc.getObjectId("_id").toString());

        return reservation;
    }

    public java.util.List<Reservation> getAll() {
        java.util.List<Reservation> reservations = new java.util.ArrayList<>();
        for (Document doc : collection.find()) {
            reservations.add(documentToReservation(doc));
        }
        return reservations;
    }

    private Reservation documentToReservation(Document doc) {
        Reservation reservation = new Reservation();
        reservation.setId(doc.getObjectId("_id").toString());
        reservation.setGuestId(doc.getString("guestId"));
        reservation.setRoomType(doc.getString("roomType"));
        reservation.setNumberOfPeople(doc.getInteger("numberOfPeople"));

        String checkInStr = doc.getString("checkInDate");
        if (checkInStr != null) {
            reservation.setCheckInDate(java.time.LocalDate.parse(checkInStr));
        }

        String checkOutStr = doc.getString("checkOutDate");
        if (checkOutStr != null) {
            reservation.setCheckOutDate(java.time.LocalDate.parse(checkOutStr));
        }

        reservation.setBedType(doc.getString("bedType"));
        reservation.setBreakfastIncluded(doc.getBoolean("breakfastIncluded"));
        reservation.setStatus(doc.getString("status"));

        String createdAtStr = doc.getString("createdAt");
        if (createdAtStr != null) {
            reservation.setCreatedAt(java.time.LocalDateTime.parse(createdAtStr));
        }

        return reservation;
    }

    public boolean delete(String id) {
        try {
            com.mongodb.client.result.DeleteResult result = collection
                    .deleteOne(com.mongodb.client.model.Filters.eq("_id", new org.bson.types.ObjectId(id)));
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
