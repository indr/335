/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.viewmodels.outputs;

import android.support.annotation.Nullable;
import android.util.Pair;

import java.util.List;

import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageLink;
import rx.Observable;

public interface ListGuideViewModelOutputs {

  Observable<Boolean> canGoUp();

  Observable<Boolean> isHomePage();

  Observable<String> pageTitle();

  Observable<List<PageItem>> pageItems();

  Observable<PageLink> showPage();

  /**
   * Pair of position and Y.
   */
  @Nullable Pair<Integer, Integer> getFirstVisibleItem();

  Observable<Boolean> isFavorable();

  Observable<Boolean> isFavorite();
}

