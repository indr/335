/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class PageCommand extends PageItem {
  protected PageCommand() {
    super("Unnamed PageCommand");
  }

  protected PageCommand(final @NonNull String title) {
    super(title);
  }

  protected PageCommand(final @NonNull String title, final @Nullable String subtitle, final @NonNull String description) {
    super(title, subtitle, description);
  }

  public abstract void execute(@NonNull Environment environment);
}
