/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.playlist;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;

import com.example.android.uamp.playback.Queue;
import com.example.android.uamp.playback.QueueManager;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageCommand;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.pages.Page;
import ch.indr.threethreefive.libs.utils.StringUtils;

public class QueuePage extends Page {

  private final QueueManager queueManager;

  public QueuePage(Environment environment) {
    super(environment);

    this.queueManager = environment.queueManager();
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle(getString(R.string.title_playlist));
  }

  @Override public void onResume() {
    super.onResume();

    reloadItems();
  }

  private void reloadItems() {
    setPageItems(buildItems());
  }

  private PageItemsBuilder buildItems() {
    final Queue queue = queueManager.getCurrentQueue();
    final PageItemsBuilder builder = pageItemsBuilder();

    if (queue == null || queue.size() == 0) {
      builder.addText(getString(R.string.empty_playlist));
      return builder;
    }

    builder.add(new PlayQueue(getString(R.string.play_playlist), queue.getQueueItem(0).getQueueId()));
    builder.add(new ClearQueue(getResources()));

    for (QueueItem queueItem : queue.queueItems()) {
      MediaDescriptionCompat description = queueItem.getDescription();
      final String title = StringUtils.getString(description.getTitle(), getString(R.string.not_available));
      final String subtitle = StringUtils.getString(description.getSubtitle());

      builder.addLink(PageUris.playlistItem(queueItem.getQueueId()),
          title, subtitle,
          StringUtils.join(new String[]{title, subtitle}, ", "),
          description.getIconUri(),
          R.drawable.ic_default_art);
    }

    return builder;
  }

  private class PlayQueue extends PageCommand {
    private final long firstQueueItemId;

    PlayQueue(final @NonNull String title, long firstQueueItemId) {
      super(title);
      this.firstQueueItemId = firstQueueItemId;
    }

    @Override public void execute(@NonNull Environment environment) {
      MediaControllerCompat.TransportControls controls = environment.playbackControls();
      if (controls == null) return;

      controls.skipToQueueItem(firstQueueItemId);
      controls.play();
    }
  }

  private class ClearQueue extends PageCommand {
    private final Resources resources;

    ClearQueue(final @NonNull Resources resources) {
      super(resources.getString(R.string.clear_playlist));

      this.resources = resources;
    }

    @Override public void execute(@NonNull Environment environment) {
      environment.queueManager().clearQueue();
      environment.toastManager().toast(resources.getString(R.string.toast_playlist_cleared));
      environment.speaker().command().playlistCleared();
      QueuePage.this.reloadItems();
    }
  }
}
