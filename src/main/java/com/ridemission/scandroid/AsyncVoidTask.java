package com.ridemission.scandroid;

import android.os.AsyncTask;

/// A work around for scala bug overriding varargs Java code
/// https://lampsvn.epfl.ch/trac/scala/ticket/1459
public abstract class AsyncVoidTask extends AsyncTask<Void, Void, Void> {

    /**
      * Subclasses must provide an implementation
      */
    protected abstract void inBackground();

    @Override
    protected Void doInBackground(Void... params) {
        inBackground();
        return null;
    }
}
