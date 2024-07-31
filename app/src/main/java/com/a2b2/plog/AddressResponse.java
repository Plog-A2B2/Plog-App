package com.a2b2.plog;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddressResponse {
    @SerializedName("documents")
    public List<Document> documents;

    public static class Document {
        @SerializedName("address")
        public Address address;

        public static class Address {
            @SerializedName("x")
            public String x;

            @SerializedName("y")
            public String y;
        }
    }
}
