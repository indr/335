/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import com.trello.rxlifecycle.ActivityEvent;

import rx.Observable;

/**
 * A type implements this interface when it can describe its lifecycle in terms of
 * creation, starting, stopping and destroying.
 */
public interface ActivityLifecycleType {

  /**
   * An observable that describes the lifecycle of the object, from CREATE to DESTROY.
   */
  Observable<ActivityEvent> lifecycle();
}
