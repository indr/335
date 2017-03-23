/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.pages;

import android.net.Uri;
import android.os.Bundle;

public class PageMeta {

  private final Uri uri;
  private final Class clazz;
  private final Bundle bundle;

  public PageMeta(Class clazz, Uri uri, Bundle bundle) {
    this.clazz = clazz;
    this.uri = uri;
    this.bundle = bundle;
  }

  public Uri getUri() {
    return uri;
  }

  public Class getClazz() {
    return clazz;
  }

  public Bundle getBundle() {
    return bundle;
  }
}
