package com.codepath.nytimessearch.listeners;

import com.codepath.nytimessearch.models.Query;

/**
 * Created by Owner on 22-Oct-16.
 */

public interface FilterSearchDialogListener {

    void onFinishChoosingOptions(Query q);
}
