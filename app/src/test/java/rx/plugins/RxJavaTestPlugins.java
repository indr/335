/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package rx.plugins;

public class RxJavaTestPlugins extends RxJavaPlugins {
  RxJavaTestPlugins() {
    super();
  }

  public static void resetPlugins() {
    getInstance().reset();
  }
}
