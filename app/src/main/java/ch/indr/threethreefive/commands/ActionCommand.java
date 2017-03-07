/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.commands;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageCommand;
import rx.functions.Action1;

public class ActionCommand extends PageCommand {
  private final String title;
  private final Action1<Environment> action1;

  public ActionCommand(String title, Action1<Environment> action1) {
    this.title = title;
    this.action1 = action1;
  }

  @Override public void execute(@NonNull Environment environment) {
    action1.call(environment);
  }

  @NonNull @Override public String getName() {
    return title;
  }

  @Nullable @Override public String getDescription() {
    return null;
  }
}
