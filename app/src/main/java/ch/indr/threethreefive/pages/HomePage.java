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

import com.example.android.uamp.playback.QueueManagerType;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageLink;
import ch.indr.threethreefive.libs.utils.MediaDescriptionUtils;
import ch.indr.threethreefive.libs.utils.PlaybackStateUtils;
import ch.indr.threethreefive.navigation.Page;
import ch.indr.threethreefive.services.PlaybackClientType;
import ch.indr.threethreefive.services.UiModeManagerType;

import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;
import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.observeForUI;

public class HomePage extends Page {

  private final PlaybackClientType playbackClient;
  private final QueueManagerType queueManager;

  public HomePage(Environment environment) {
    super(environment);

    this.playbackClient = environment.playbackClient();
    this.queueManager = environment.queueManager();
  }

  @Override public void onCreate(@NonNull Context context, Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

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
      builder.addItem(new NowPlayingItem(playbackClient));
    }

    // Static main menu items
    builder
        .addLink("/music", getResStr(R.string.mainmenu_music_title), getResStr(R.string.mainmenu_music_subtitle), getResStr(R.string.mainmenu_music_description))
        .addLink("/radio", getResStr(R.string.mainmenu_radio_title), getResStr(R.string.mainmenu_radio_subtitle), getResStr(R.string.mainmenu_radio_description))
        .addLink("/playlist", getResStr(R.string.mainmenu_playlist_title), getResStr(R.string.mainmenu_playlist_subtitle), getResStr(R.string.mainmenu_playlist_description))
        .addLink("/favorites", getResStr(R.string.mainmenu_favorites_title), getResStr(R.string.mainmenu_favorites_subtitle), getResStr(R.string.mainmenu_favorites_description))
        .addLink("/preferences", getResStr(R.string.mainmenu_preferences_title), getResStr(R.string.mainmenu_preferences_subtitle), getResStr(R.string.mainmenu_preferences_description));

    setPageItems(builder);
  }

  private String getResStr(int id) {
    return getResources().getString(id);
  }

  private boolean isButtonView() {
    return environment().preferences().uiMode().get() == UiModeManagerType.UI_MODE_BUTTONS;
  }

  private boolean isPausedOrPlaying() {
    if (!playbackClient.playbackState().hasValue()) {
      return false;
    }

    int playbackState = playbackClient.playbackState().getValue();

    return playbackState == PlaybackStateCompat.STATE_PAUSED || playbackState == STATE_PLAYING;
  }

  public class NowPlayingItem extends PageLink {

    public NowPlayingItem(@NonNull PlaybackClientType playbackClient) {
      super(Uri.parse("/now-playing"), "Now Playing");

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
      String prefix = PlaybackStateUtils.toString(stateAndQueueItem.first);
      setTitle(prefix + ": " + MediaDescriptionUtils.fullTitle(queueItem.getDescription()));
      uri.onNext(Uri.parse("/playlist/" + queueItem.getQueueId()));
    }
  }
}
