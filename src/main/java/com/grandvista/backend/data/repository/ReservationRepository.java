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
}
