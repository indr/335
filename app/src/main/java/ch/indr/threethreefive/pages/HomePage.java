/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Pair;

import com.example.android.uamp.playback.QueueManager;

import javax.inject.Inject;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageLink;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.pages.Page;
import ch.indr.threethreefive.libs.utils.MediaDescriptionUtils;
import ch.indr.threethreefive.libs.utils.PlaybackStateUtils;
import ch.indr.threethreefive.services.PlaybackClient;
import ch.indr.threethreefive.services.UiModeManager;

import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;
import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.observeForUI;

public class HomePage extends Page {
  protected @Inject PlaybackClient playbackClient;
  protected @Inject QueueManager queueManager;
  protected @Inject UiModeManager uiModeManager;

  public HomePage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    component().inject(this);

    setTitle(getContext().getString(R.string.app_name));
    setParentPageLink(null);

    if (!isButtonView()) {
      addPageItems();
    }
  }

  @Override public void onResume() {
    super.onResume();

    if (isButtonView()) {
      addPageItems();
    }
  }

  private void addPageItems() {
    PageItemsBuilder builder = pageItemsBuilder();

    // Now playing only in button ui mode, and only if playback is paused or playing
    if (isButtonView() && isPausedOrPlaying()) {
      builder.add(new NowPlayingItem(playbackClient));
    }

    // Static main menu items
    builder
        .addLink(PageUris.music(), getString(R.string.mainmenu_music_title), getString(R.string.mainmenu_music_subtitle), getString(R.string.mainmenu_music_description))
        .addLink(PageUris.radio(), getString(R.string.mainmenu_radio_title), getString(R.string.mainmenu_radio_subtitle), getString(R.string.mainmenu_radio_description))
        .addLink(PageUris.playlist(), getString(R.string.mainmenu_playlist_title), getString(R.string.mainmenu_playlist_subtitle), getString(R.string.mainmenu_playlist_description))
        .addLink(PageUris.favorites(), getString(R.string.mainmenu_favorites_title), getString(R.string.mainmenu_favorites_subtitle), getString(R.string.mainmenu_favorites_description))
        .addLink(PageUris.preferences(), getString(R.string.mainmenu_preferences_title), getString(R.string.mainmenu_preferences_subtitle), getString(R.string.mainmenu_preferences_description));

    setPageItems(builder);
  }

  private boolean isButtonView() {
    return uiModeManager.getCurrentUiMode() == UiModeManager.UI_MODE_BUTTONS;
  }

  private boolean isPausedOrPlaying() {
    if (!playbackClient.playbackState().hasValue()) {
      return false;
    }

    int playbackState = playbackClient.playbackState().getValue();

    return playbackState == PlaybackStateCompat.STATE_PAUSED || playbackState == STATE_PLAYING;
  }

  public class NowPlayingItem extends PageLink {

    public NowPlayingItem(@NonNull PlaybackClient playbackClient) {
      super(PageUris.nowPlaying(), "Now Playing");

      playbackClient.playbackState()
          .distinctUntilChanged()
          .map(state -> Pair.create(state, queueManager.getCurrentQueueItem()))
          .compose(observeForUI())
          .subscribe(this::updateItem);
    }

    private void updateItem(final Pair<Integer, MediaSessionCompat.QueueItem> stateAndQueueItem) {
      if (stateAndQueueItem == null) {
        return;
      }

      final MediaSessionCompat.QueueItem queueItem = stateAndQueueItem.second;
      final String playbackState = PlaybackStateUtils.toString(stateAndQueueItem.first);

      String title = "Playback state: " + playbackState;
      Uri uri = PageUris.home();
      if (queueItem != null) {
        title = playbackState + ": " + MediaDescriptionUtils.fullTitle(queueItem.getDescription());
        uri = PageUris.playlistItem(queueItem.getQueueId());
      }

      setTitle(title);
      setDescription(title);
      setUri(uri);
    }
  }
}
