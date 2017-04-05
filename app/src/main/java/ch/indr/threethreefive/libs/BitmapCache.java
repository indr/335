/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.uamp.AlbumArtCache;

import ch.indr.threethreefive.libs.utils.UriUtils;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public final class BitmapCache {

  private final AlbumArtCache albumArtCache;

  private static final BitmapCache instance = new BitmapCache();

  private BitmapCache() {
    this.albumArtCache = AlbumArtCache.getInstance();
  }

  public static BitmapCache getInstance() {
    return instance;
  }

  public @NonNull Observable<Bitmap> getIconImage(final @Nullable Uri iconUri) {
    BehaviorSubject<Bitmap> bitmap = BehaviorSubject.create();
    if (UriUtils.isEmpty(iconUri)) {
      bitmap.onNext(null);
      return bitmap;
    }

    final Bitmap iconImage = albumArtCache.getIconImage(UriUtils.getString(iconUri));
    if (iconImage != null) {
      Timber.d("Bitmap from cache: %s", iconUri);
      bitmap.onNext(iconImage);
    } else {
      Timber.d("Bitmap not in cache: %s", iconUri);
      albumArtCache.fetch(UriUtils.getString(iconUri), new AlbumArtCache.FetchListener() {
        @Override public void onError(String artUrl, Throwable e) {
          super.onError(artUrl, e);
          bitmap.onNext(null);
        }

        @Override public void onFetched(String artUrl, Bitmap bigImage, Bitmap iconImage) {
          bitmap.onNext(iconImage);
        }
      });
    }

    return bitmap;
  }
}
