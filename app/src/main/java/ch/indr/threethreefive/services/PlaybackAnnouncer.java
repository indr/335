/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

public interface PlaybackAnnouncer {

  boolean isStarted();

  /**
   * Starts announcing playback status changes.
   */
  void start();

  /**
   * Stops announcing playback status changes.
   */
  void stop();
}
