/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

import org.junit.Before;
import org.junit.Test;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.fakes.FakePlaybackClient;

import static android.support.v4.media.session.PlaybackStateCompat.STATE_CONNECTING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_STOPPED;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PlaybackAnnouncerTests extends TtfRobolectricTestCase {

  private FakePlaybackClient playbackClient;
  private Speaker speaker;
  private PlaybackAnnouncerImpl sut;

  @Before
  @Override public void setUp() throws Exception {
    super.setUp();

    this.playbackClient = new FakePlaybackClient();
    this.speaker = mock(Speaker.class);
    this.sut = new PlaybackAnnouncerImpl(playbackClient, speaker);
  }

  private void onNextPlaybackState(int stateConnecting) {
    playbackClient.playbackState.onNext(stateConnecting);
  }

  private void waitForDebounceTimeout() {
    try {
      Thread.sleep(PlaybackAnnouncerImpl.DEBOUNCE_TIMEOUT + 100);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void playback_state_connecting_should_speak_state_connecting() {
    // Arrange
    sut.start();

    // Act
    onNextPlaybackState(STATE_CONNECTING);

    // Assert
    waitForDebounceTimeout();
    verify(speaker).sayQueued(R.string.speech_playback_state_connecting);
  }

  @Test
  public void playback_state_playing_to_stopped_should_speak_state_stopped() {
    // Arrange
    sut.start();

    // Act
    onNextPlaybackState(STATE_PLAYING);
    onNextPlaybackState(STATE_STOPPED);

    // Assert
    waitForDebounceTimeout();
    verify(speaker).sayQueued(R.string.speech_playback_state_stopped);
  }
}
