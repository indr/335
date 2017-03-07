/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.viewmodels.inputs;

import ch.indr.threethreefive.libs.PageItem;

public interface ListGuideViewModelInputs {
  void back();

  void pageItemClick(PageItem pageItem);

  void up();
}
