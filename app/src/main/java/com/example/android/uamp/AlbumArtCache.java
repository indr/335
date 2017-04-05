/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.uamp;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;

import com.androidnetworking.common.Priority;
import com.rxandroidnetworking.RxAndroidNetworking;

import ch.indr.threethreefive.libs.BitmapHelper;
import ch.indr.threethreefive.libs.utils.ObjectUtils;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Implements a basic cache of album arts, with async loading support.
 */
public final class AlbumArtCache {

  private static final int MAX_ALBUM_ART_CACHE_SIZE = 12 * 1024 * 1024;  // 12 MB
  private static final int MAX_ART_WIDTH = 800;  // pixels
  private static final int MAX_ART_HEIGHT = 480;  // pixels

  // Resolution reasonable for carrying around as an icon (generally in
  // MediaDescription.getIconBitmap). This should not be bigger than necessary, because
  // the MediaDescription object should be lightweight. If you set it too high and try to
  // serialize the MediaDescription, you may get FAILED BINDER TRANSACTION errors.
  private static final int MAX_ART_WIDTH_ICON = 128;  // pixels
  private static final int MAX_ART_HEIGHT_ICON = 128;  // pixels

  public static final int BIG_BITMAP_INDEX = 0;
  public static final int ICON_BITMAP_INDEX = 1;

  private final LruCache<String, Bitmap[]> mCache;

  private static final AlbumArtCache sInstance = new AlbumArtCache();

  public static AlbumArtCache getInstance() {
    return sInstance;
  }

  private AlbumArtCache() {
    // Holds no more than MAX_ALBUM_ART_CACHE_SIZE bytes, bounded by maxmemory/4 and
    // Integer.MAX_VALUE:
    int maxSize = Math.min(MAX_ALBUM_ART_CACHE_SIZE,
        (int) (Math.min(Integer.MAX_VALUE, Runtime.getRuntime().maxMemory() / 4)));
    Timber.d("Creating LruCache with max size %d", maxSize);
    mCache = new LruCache<String, Bitmap[]>(maxSize) {
      @Override
      protected int sizeOf(String key, Bitmap[] value) {
        return value[BIG_BITMAP_INDEX].getByteCount()
            + value[ICON_BITMAP_INDEX].getByteCount();
      }
    };
  }

  public Bitmap[] getCachedImages(String artUrl) {
    return mCache.get(artUrl);
  }

  public Bitmap getBigImage(String artUrl) {
    Bitmap[] result = mCache.get(artUrl);
    return result == null ? null : result[BIG_BITMAP_INDEX];
  }

  public Bitmap getIconImage(String artUrl) {
    Bitmap[] result = mCache.get(artUrl);
    return result == null ? null : result[ICON_BITMAP_INDEX];
  }

  public void fetch(final String artUrl, final FetchListener listener) {
    Bitmap[] bitmap = mCache.get(artUrl);
    if (bitmap != null) {
      listener.onFetched(artUrl, bitmap[BIG_BITMAP_INDEX], bitmap[ICON_BITMAP_INDEX]);
      return;
    }

    if (artUrl.startsWith("/")) {
      fetchFromFile(artUrl, listener);
      return;
    }

    RxAndroidNetworking.get(artUrl)
        .setTag("albumArtRequestTag")
        .setPriority(Priority.MEDIUM)
        .setBitmapMaxHeight(MAX_ART_HEIGHT)
        .setBitmapMaxWidth(MAX_ART_WIDTH)
        .setBitmapConfig(Bitmap.Config.ARGB_8888)
        .build()
        .getBitmapObservable()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.computation())
        .map(big -> {
          if (big == null) return null;
          Bitmap icon = BitmapHelper.scaleBitmap(big, MAX_ART_WIDTH_ICON, MAX_ART_HEIGHT_ICON);
          if (icon == null) return null;
          return new Bitmap[]{big, icon};
        })
        .map(bitmaps -> {
          if (bitmaps != null) {
            mCache.put(artUrl, bitmaps);
          }
          return bitmaps;
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Bitmap[]>() {
          @Override public void onCompleted() {
            Timber.d("onCompleted %s", this.toString());
          }

          @Override public void onError(Throwable e) {
            Timber.e(e, "Error fetching bitmap");
            listener.onError(artUrl, e);
          }

          @Override public void onNext(Bitmap[] bitmap) {
            Timber.d("Fetched bitmaps");
            if (bitmap != null) {
              listener.onFetched(artUrl, bitmap[BIG_BITMAP_INDEX], bitmap[ICON_BITMAP_INDEX]);
            } else {
              listener.onFetched(artUrl, null, null);
            }
          }
        });
  }

  public void fetchFromFile(final String artUrl, final FetchListener listener) {
    // WARNING: for the sake of simplicity, simultaneous multi-thread fetch requests
    // are not handled properly: they may cause redundant costly operations, like HTTP
    // requests and bitmap rescales. For production-level apps, we recommend you use
    // a proper image loading library, like Glide.
    Bitmap[] bitmap = mCache.get(artUrl);
    if (bitmap != null) {
      Timber.d("getOrFetch: album art is in cache, using it, %s", artUrl);
      listener.onFetched(artUrl, bitmap[BIG_BITMAP_INDEX], bitmap[ICON_BITMAP_INDEX]);
      return;
    }
    Timber.d("getOrFetch: starting asynctask to fetch %s", artUrl);

    new AsyncTask<Void, Void, Bitmap[]>() {
      @Override
      protected Bitmap[] doInBackground(Void[] objects) {
        Bitmap[] bitmaps;
        try {
          Timber.d("doInBackground: fetchAndRescaleBitmap %s", artUrl);
          Bitmap bitmap = BitmapHelper.fetchAndRescaleBitmap(artUrl,
              MAX_ART_WIDTH, MAX_ART_HEIGHT);
          Timber.d("doInBackground: fetched bitmap %s", ObjectUtils.toString(bitmap));
          if (bitmap == null) return null;
          Timber.d("doInBackground: scaleBitmap %s", artUrl);
          Bitmap icon = BitmapHelper.scaleBitmap(bitmap,
              MAX_ART_WIDTH_ICON, MAX_ART_HEIGHT_ICON);
          if (icon == null) return null;
          bitmaps = new Bitmap[]{bitmap, icon};
          mCache.put(artUrl, bitmaps);
          int maxSize = mCache.maxSize();
          Timber.d("doInBackground: put bitmap in cache, new cache size=%d (%.2f)",
              mCache.size(), 100 * (float) mCache.size() / maxSize);
        } catch (Exception e) {
          Timber.e(e, "Error fetching bitmap in background");
          return null;
        }
        return bitmaps;
      }

      @Override
      protected void onPostExecute(Bitmap[] bitmaps) {
        if (bitmaps == null) {
          listener.onError(artUrl, new IllegalArgumentException("got null bitmaps"));
        } else {
          listener.onFetched(artUrl,
              bitmaps[BIG_BITMAP_INDEX], bitmaps[ICON_BITMAP_INDEX]);
        }
      }
    }.execute();
  }

  public static abstract class FetchListener {
    public abstract void onFetched(String artUrl, Bitmap bigImage, Bitmap iconImage);

    public void onError(String artUrl, Throwable e) {
      Timber.e(e, "AlbumArtFetchListener: error while downloading " + artUrl);
    }
  }
}
