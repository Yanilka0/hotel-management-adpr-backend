package com.grandvista.backend.repository;

import com.grandvista.backend.database.MongoDBConnection;
import com.grandvista.backend.model.StaffUser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.Optional;

public class StaffUserRepository {
    private final MongoCollection<Document> collection;

    public StaffUserRepository() {
        this.collection = MongoDBConnection.getInstance()
                .getDatabase()
                .getCollection("staff_users");
    }

    public StaffUser save(StaffUser user) {
        Document doc = new Document();

        if (user.getStaffId() != null && !user.getStaffId().isEmpty()) {
            // Update existing user
            doc.append("email", user.getEmail())
                    .append("fullName", user.getFullName())
                    .append("passwordHash", user.getPasswordHash())
                    .append("profileImageUrl", user.getProfileImageUrl())
                    .append("createdAt", user.getCreatedAt().toString())
                    .append("role", user.getRole());

            collection.replaceOne(Filters.eq("_id", new ObjectId(user.getStaffId())), doc);
        } else {
            // Insert new user
            doc.append("email", user.getEmail())
                    .append("fullName", user.getFullName())
                    .append("passwordHash", user.getPasswordHash())
                    .append("profileImageUrl", user.getProfileImageUrl())
                    .append("createdAt", user.getCreatedAt().toString())
                    .append("role", user.getRole());

            collection.insertOne(doc);
            user.setStaffId(doc.getObjectId("_id").toString());
        }

        return user;
    }

    public Optional<StaffUser> findByEmail(String email) {
        Document doc = collection.find(Filters.eq("email", email)).first();
        if (doc == null) {
            return Optional.empty();
        }
        return Optional.of(documentToStaffUser(doc));
    }

    public Optional<StaffUser> findById(String id) {
        Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        if (doc == null) {
            return Optional.empty();
        }
        return Optional.of(documentToStaffUser(doc));
    }

    private StaffUser documentToStaffUser(Document doc) {
        StaffUser user = new StaffUser();
        user.setStaffId(doc.getObjectId("_id").toString());
        user.setEmail(doc.getString("email"));
        user.setFullName(doc.getString("fullName"));
        user.setPasswordHash(doc.getString("passwordHash"));
        user.setProfileImageUrl(doc.getString("profileImageUrl"));

        String createdAtStr = doc.getString("createdAt");
        if (createdAtStr != null) {
            user.setCreatedAt(LocalDateTime.parse(createdAtStr));
        }

        user.setRole(doc.getString("role"));
        return user;
    }
}
