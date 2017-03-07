/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.navigation;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

public abstract class AbstractPageResolver {

  protected static final String AUTHORITY = "ch.indr.threethreefive";

  protected Uri setDefaultAuthority(Uri uri) {
    return uri.buildUpon().authority(AUTHORITY).build();
  }

  protected PageMeta makeMeta(Class pageClass, Uri uri) {
    return new PageMeta(pageClass, uri, new Bundle());
  }

  protected PageMeta makeMeta(Class pageClass, Uri uri, String id) {
    Bundle bundle = new Bundle();
    bundle.putString("id", id);
    return new PageMeta(pageClass, uri, bundle);
  }

  public abstract PageMeta resolve(Uri uri) throws PageNotFoundException;

  public class PageNotFoundException extends RuntimeException {

    public PageNotFoundException(@NonNull Uri uri) {
      super("Page not found: " + uri.toString());
    }
  }
}
