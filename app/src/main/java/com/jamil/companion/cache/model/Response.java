package com.jamil.companion.cache.model;

/**
 * Response model class for the SQLite response caching DB.
 * (An object model of a row from the Response table)
 */

public class Response {
    private int id;
    private String requestIdentifier; // Hash in database
    private String responseBody;
    private String modifiedDate;

    public Response (int _id, String _requestIdentifier, String _responseBody, String _modifiedDate)
    {
        id = _id;
        setRequestIdentifier(_requestIdentifier);
        setResponseBody(_responseBody);
        setModifiedDate(_modifiedDate);
    }

    public Response (String _requestIdentifier, String _responseBody, String _modifiedDate)
    {
        this(0, _requestIdentifier, _responseBody, _modifiedDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestIdentifier() {
        return requestIdentifier;
    }

    public void setRequestIdentifier(String requestIdentifier) {
        this.requestIdentifier = requestIdentifier;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
