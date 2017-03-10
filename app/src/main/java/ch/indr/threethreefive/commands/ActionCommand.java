/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.commands;

import android.support.annotation.NonNull;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageCommand;
import rx.functions.Action1;

public class ActionCommand extends PageCommand {

  private final Action1<Environment> action;

  public ActionCommand(final @NonNull String title, final @NonNull Action1<Environment> action) {
    super(title);
    this.action = action;
  }

  @Override public void execute(@NonNull Environment environment) {
    action.call(environment);
  }
}
