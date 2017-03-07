package ch.indr.threethreefive.commands;

import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;

import java.util.ArrayList;
import java.util.List;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.libs.PageCommand;

public class AddToPlaylist extends PageCommand {

  private List<MediaItem> mediaItems;

  public AddToPlaylist(MediaItem mediaItem) {
    this.name.onNext("Add to Playlist");
    this.mediaItems = new ArrayList<>();
    this.mediaItems.add(mediaItem);
  }

  public AddToPlaylist(@NonNull String name, @NonNull List<MediaItem> mediaItems) {
    this.name.onNext("Add to Playlist");
    this.mediaItems = new ArrayList<>(mediaItems);
  }

  @Override public void execute(@NonNull Environment environment) {
    addToQueue(environment);
  }

  protected List<MediaSessionCompat.QueueItem> addToQueue(@NonNull Environment environment) {
    final List<MediaSessionCompat.QueueItem> queueItems = environment.queueManager().addToQueue(mediaItems);

    String toast = mediaItems.size() == 1 ? "Added to Playlist " + mediaItems.get(0).getName() : "Added to Playlist: " + mediaItems.size() + " Songs";
    environment.toastManager().toast(toast);
    environment.speaker().command().playlistAdded();

    return queueItems;
  }
}
