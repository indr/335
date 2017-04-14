/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.pages;

import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.List;

import ch.indr.threethreefive.libs.Description;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageLink;
import rx.Observable;

public interface PageType {
  boolean getIsRootPage();

  @Nullable Uri getPageUri();

  @Nullable Uri getParentPageUri();

  Observable<Description> description();

  Observable<List<PageItem>> pageItems();

  Observable<PageItem> pageItem();

  Observable<PageLink> parentPageLink();

  Observable<PageTransition> transitionTo();
}
