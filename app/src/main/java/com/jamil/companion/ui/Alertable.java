package com.jamil.companion.ui;

import com.jamil.companion.model.Entity;

import java.util.ArrayList;

public interface Alertable {

    /**
     * Function to be called when the API receives data that is ready to be used by the app.
     *
     * @param entities list of entities parsed out of the API response and into models
     * @param offset the offset of the results from the beginning, used for pagination
     * @param limit the maximum number of results in this result set
     * @param total the total number of results that match this query overall
     * @param count the actual number of results in this result set
     */
    void receiveData(ArrayList<Entity> entities, int offset, int limit, int total, int count);
}
