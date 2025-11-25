package com.example;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class MessageDocument {

    @BsonId
    private ObjectId id;

    @BsonProperty("message")
    private String message;

    @BsonProperty("timestamp")
    private long timestamp;

    public MessageDocument() {}

    public MessageDocument(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    // getters e setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

