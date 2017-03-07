/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.qualifiers;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ch.indr.threethreefive.libs.ActivityViewModel;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresActivityViewModel {
  Class<? extends ActivityViewModel> value();
}
