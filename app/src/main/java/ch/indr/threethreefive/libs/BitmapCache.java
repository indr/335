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

import static com.example.android.uamp.AlbumArtCache.BIG_BITMAP_INDEX;
import static com.example.android.uamp.AlbumArtCache.ICON_BITMAP_INDEX;

public final class BitmapCache {

  private final AlbumArtCache albumArtCache;

  private static final BitmapCache instance = new BitmapCache();

  private BitmapCache() {
    this.albumArtCache = AlbumArtCache.getInstance();
  }

  public static BitmapCache getInstance() {
    return instance;
  }

  public @NonNull Observable<Bitmap> getIconImage(final @Nullable Uri imageUri) {
    return getImages(imageUri)
        .map(bitmaps -> bitmaps == null ? null : bitmaps[ICON_BITMAP_INDEX]);
  }

  public @NonNull Observable<Bitmap> getTitleImage(final @Nullable Uri imageUri) {
    return getImages(imageUri)
        .map(bitmaps -> bitmaps == null ? null : bitmaps[BIG_BITMAP_INDEX]);
  }

  private @NonNull Observable<Bitmap[]> getImages(final @Nullable Uri imageUri) {
    BehaviorSubject<Bitmap[]> observable = BehaviorSubject.create();
    if (UriUtils.isEmpty(imageUri)) {
      observable.onNext(null);
      return observable;
    }

    final Bitmap[] bitmaps = albumArtCache.getCachedImages(UriUtils.getString(imageUri));
    if (bitmaps != null) {
      Timber.d("Bitmap from cache: %s", imageUri);
      observable.onNext(bitmaps);
    } else {
      Timber.d("Bitmap not in cache: %s", imageUri);
      albumArtCache.fetch(UriUtils.getString(imageUri), new AlbumArtCache.FetchListener() {
        @Override public void onError(String artUrl, Throwable e) {
          super.onError(artUrl, e);
          observable.onNext(null);
        }

        @Override public void onFetched(String artUrl, Bitmap bigImage, Bitmap iconImage) {
          observable.onNext(new Bitmap[]{iconImage, bigImage});
        }
      });
    }
    return observable;
  }
}
