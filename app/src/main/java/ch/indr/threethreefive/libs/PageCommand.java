/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.support.annotation.NonNull;

public abstract class PageCommand extends PageItem {
  protected PageCommand() {
  }

  protected PageCommand(final @NonNull String title) {
    super(new Description(title));
  }

  public abstract void execute(@NonNull Environment environment);
}
