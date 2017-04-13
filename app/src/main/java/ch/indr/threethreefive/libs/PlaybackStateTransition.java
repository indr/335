/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.support.v4.media.session.PlaybackStateCompat;

public class PlaybackStateTransition extends Transition<PlaybackStateCompat> {

  public PlaybackStateTransition() {
    super(null, null);
  }

  protected PlaybackStateTransition(PlaybackStateCompat from, PlaybackStateCompat to) {
    super(from, to);
  }

  public boolean is(int from, int to) {
    return isFrom(from) && isTo(to);
  }

  public boolean isFrom(int state) {
    return isState(getFrom(), state);
  }

  public boolean isTo(int state) {
    return isState(getTo(), state);
  }

  public PlaybackStateTransition next(PlaybackStateCompat value) {
    return new PlaybackStateTransition(getTo(), value);
  }

  private boolean isState(PlaybackStateCompat playbackState, int state) {
    return playbackState != null && playbackState.getState() == state;
  }
}
