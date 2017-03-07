/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.playlist.pages;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;

import com.example.android.uamp.playback.Queue;
import com.example.android.uamp.playback.QueueManagerType;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageCommand;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.navigation.Page;

public class QueuePage extends Page {

  private final QueueManagerType queueManager;

  public QueuePage(Environment environment) {
    super(environment);

    this.queueManager = environment.queueManager();
  }

  @Override public void onCreate(@NonNull Context context, Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    setTitle("Playlist");
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
      builder.addText("Empty Playlist");
      return builder;
    }

    builder.addItem(new PlayQueue(queue.getQueueItem(0).getQueueId()));
    builder.addItem(new ClearQueue());

    for (QueueItem each : queue.queueItems()) {
      MediaDescriptionCompat description = each.getDescription();
      CharSequence title = description.getTitle();
      if (title == null) {
        title = "Unnamed";
      }
      long queueItemId = each.getQueueId();
      builder.addLink("/playlist/" + queueItemId, title.toString());
    }

    return builder;
  }

  private class PlayQueue extends PageCommand {

    private final long firstQueueItemId;

    PlayQueue(long firstQueueItemId) {
      this.firstQueueItemId = firstQueueItemId;
    }

    @Override public void execute(@NonNull Environment environment) {
      MediaControllerCompat.TransportControls controls = environment.playbackControls();
      if (controls == null) return;

      controls.skipToQueueItem(firstQueueItemId);
      controls.play();
    }

    @NonNull @Override public String getName() {
      return "Play Playlist";
    }

    @Nullable @Override public String getDescription() {
      return null;
    }
  }

  private class ClearQueue extends PageCommand {

    @Override public void execute(@NonNull Environment environment) {
      environment.queueManager().clearQueue();
      environment.toastManager().toast("Playlist cleared");
      environment.speaker().command().playlistCleared();
      QueuePage.this.reloadItems();
    }

    @NonNull @Override public String getName() {
      return "Clear Playlist";
    }

    @Nullable @Override public String getDescription() {
      return null;
    }
  }
}
