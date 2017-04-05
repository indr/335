/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.graphics.Bitmap;

import com.example.android.uamp.AlbumArtCache;

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

  public Observable<Bitmap> getIconImage(String iconUri) {
    BehaviorSubject<Bitmap> bitmap = BehaviorSubject.create();

    final Bitmap iconImage = albumArtCache.getIconImage(iconUri);
    if (iconImage != null) {
      Timber.d("Bitmap from cache: %s", iconUri);
      bitmap.onNext(iconImage);
    } else {
      Timber.d("Bitmap not in cache: %s", iconUri);
      albumArtCache.fetch(iconUri, new AlbumArtCache.FetchListener() {
        @Override public void onError(String artUrl, Exception e) {
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
