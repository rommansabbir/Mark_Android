package com.rommansabbir.androidtraining;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public abstract class AppViewModel extends ViewModel {
    // Can't use ConcurrentHashMap, because it can lose values on old apis (see b/37042460)
    @Nullable
    private final Map<String, Object> mBagOfTags = new HashMap<>();
    private volatile boolean mCleared = false;

    private static void closeWithRuntimeException(Object obj) {
        if (obj instanceof Closeable) {
            try {
                ((Closeable) obj).close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     * <p>
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel.
     */
    @SuppressWarnings("WeakerAccess")
    protected void onCleared() {
    }

    @MainThread
    final void clear() {
        mCleared = true;
        // Since clear() is final, this method is still called on mock objects
        // and in those cases, mBagOfTags is null. It'll always be empty though
        // because setTagIfAbsent and getTag are not final so we can skip
        // clearing it
        if (mBagOfTags != null) {
            synchronized (mBagOfTags) {
                for (Object value : mBagOfTags.values()) {
                    // see comment for the similar call in setTagIfAbsent
                    closeWithRuntimeException(value);
                }
            }
        }
        onCleared();
    }

    @SuppressWarnings("unchecked")
    <T> T setTagIfAbsent(String key, T newValue) {
        T previous;
        synchronized (mBagOfTags) {
            previous = (T) mBagOfTags.get(key);
            if (previous == null) {
                mBagOfTags.put(key, newValue);
            }
        }
        T result = previous == null ? newValue : previous;
        if (mCleared) {
            // It is possible that we'll call close() multiple times on the same object, but
            // Closeable interface requires close method to be idempotent:
            // "if the stream is already closed then invoking this method has no effect." (c)
            closeWithRuntimeException(result);
        }
        return result;
    }

    @SuppressWarnings({"TypeParameterUnusedInFormals", "unchecked"})
    <T> T getTag(String key) {
        if (mBagOfTags == null) {
            return null;
        }
        synchronized (mBagOfTags) {
            return (T) mBagOfTags.get(key);
        }
    }
}
