package com.grandvista.backend.data.repository;

import com.grandvista.backend.data.database.MongoDBConnection;
import com.grandvista.backend.data.model.Guest;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Optional;

public class GuestRepository {
    private final MongoCollection<Document> collection;

    public GuestRepository() {
        this.collection = MongoDBConnection.getInstance()
                .getDatabase()
                .getCollection("guests");
    }

    public Guest save(Guest guest) {
        Document doc = new Document();
        doc.append("fullName", guest.getFullName())
                .append("age", guest.getAge())
                .append("contactNumber", guest.getContactNumber())
                .append("address", guest.getAddress())
                .append("identificationId", guest.getIdentificationId());

        if (guest.getId() != null) {
            collection.replaceOne(Filters.eq("_id", new ObjectId(guest.getId())), doc);
        } else {
            collection.insertOne(doc);
            guest.setId(doc.getObjectId("_id").toString());
        }

        return guest;
    }

    public Optional<Guest> findByIdentificationId(String identificationId) {
        Document doc = collection.find(Filters.eq("identificationId", identificationId)).first();
        if (doc != null) {
            return Optional.of(documentToGuest(doc));
        }
        return Optional.empty();
    }

    private Guest documentToGuest(Document doc) {
        Guest guest = new Guest();
        guest.setId(doc.getObjectId("_id").toString());
        guest.setFullName(doc.getString("fullName"));
        guest.setAge(doc.getInteger("age"));
        guest.setContactNumber(doc.getString("contactNumber"));
        guest.setAddress(doc.getString("address"));
        guest.setIdentificationId(doc.getString("identificationId"));
        return guest;
    }
}
