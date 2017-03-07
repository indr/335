/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.viewmodels.outputs;

import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageLink;
import rx.Observable;

public interface ButtonGuideViewModelOutputs {

  Observable<String> activityTitle();

  Observable<Boolean> canGoUp();

  Observable<PageItem> pageItem();

  Observable<PageLink> showPage();
}
