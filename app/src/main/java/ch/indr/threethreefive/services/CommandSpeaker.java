/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

public interface CommandSpeaker {
  void applicationClosed();

  void favoriteAdded();

  void favoriteRemoved();

  void playlistAdded();

  void playlistCleared();

  void playlistItemRemoved();

  void preferenceAppLaunchCounterReset();

  void preferenceButtonUiLaunchCounterReset();
}
