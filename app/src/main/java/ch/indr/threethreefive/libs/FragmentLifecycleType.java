/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;


import com.trello.rxlifecycle.FragmentEvent;

import rx.Observable;

/**
 * A type implements this interface when it can describe its lifecycle in terms of attaching, view creation, starting,
 * stopping, destroying, and detaching.
 */
public interface FragmentLifecycleType {

  /**
   * An observable that describes the lifecycle of the object, from ATTACH to DETACH.
   */
  Observable<FragmentEvent> lifecycle();
}
