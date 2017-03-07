/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

import android.support.v4.media.session.PlaybackStateCompat;

import org.junit.Before;
import org.junit.Test;

import ch.indr.threethreefive.ThreeThreeFiveRobolectricTestCase;
import ch.indr.threethreefive.mocks.MockPlaybackClient;
import ch.indr.threethreefive.mocks.MockSpeaker;

public class PlaybackAnnouncerTests extends ThreeThreeFiveRobolectricTestCase {

  private MockPlaybackClient playbackClient;
  private MockSpeaker speaker;

  @Before
  @Override public void setUp() throws Exception {
    super.setUp();

    this.playbackClient = new MockPlaybackClient();
    this.speaker = new MockSpeaker();
  }

  @Test
  public void testSpeakLoadingStream() {
    PlaybackAnnouncerType playbackAnnouncer = new PlaybackAnnouncer(playbackClient, speaker);

    playbackAnnouncer.stop();
    playbackClient.playbackState.onNext(PlaybackStateCompat.STATE_CONNECTING);
    assertEquals(speaker.lastSpeech, null);

    playbackAnnouncer.start();
    playbackClient.playbackState.onNext(PlaybackStateCompat.STATE_CONNECTING);
    assertEquals(speaker.lastSpeech, "Loeading...");
  }
}
