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

  private final String title;
  private final String description;

  public TextItem(@NonNull String title, @Nullable String description) {
    this.title = title;
    this.description = description;
  }

  @NonNull @Override public String getName() {
    return title;
  }

  @Nullable @Override public String getDescription() {
    return description;
  }
}
