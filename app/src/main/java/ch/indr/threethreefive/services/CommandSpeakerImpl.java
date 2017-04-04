/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

import android.support.annotation.NonNull;

public class CommandSpeakerImpl implements CommandSpeaker {

  private final Speaker speaker;

  public CommandSpeakerImpl(@NonNull Speaker speaker) {
    this.speaker = speaker;
  }

  private void sayUrgent(String text) {
    speaker.sayUrgent(text);
  }

  @Override public void closeApp() {
    speaker.sayUrgent("Closing application");
  }

  @Override public void favoriteAdded() {
    sayUrgent("Added to favorites");
  }

  @Override public void favoriteRemoved() {
    sayUrgent("Removed from favorites");
  }

  @Override public void playlistAdded() {
    sayUrgent("Added to playlist");
  }

  @Override public void playlistCleared() {
    sayUrgent("Playlist cleared");
  }

  @Override public void playlistItemRemoved() {
    sayUrgent("Playlist item removed");
  }

  @Override public void preferenceAppLaunchCounterReset() {
    sayUrgent("App launch counter reset");
  }

  @Override public void preferenceButtonUiLaunchCounterReset() {
    sayUrgent("Button UI launch counter reset");
  }
}
