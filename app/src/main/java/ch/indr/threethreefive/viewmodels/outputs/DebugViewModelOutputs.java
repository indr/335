/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.viewmodels.outputs;

import rx.Observable;

public interface DebugViewModelOutputs {
  Observable<String> testText();

  Observable<String> showDebug();
}
