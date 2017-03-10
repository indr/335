/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TextItem extends PageItem {

  public TextItem(final @NonNull String title) {
    super(title);
  }

  public TextItem(final @NonNull String title, final @Nullable String subtitle, final @NonNull String description) {
    super(title, subtitle, description);
  }
}
