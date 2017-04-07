package ch.indr.threethreefive.commands;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.example.android.uamp.playback.QueueManager;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.libs.PageCommand;

public class PlayMedia extends PageCommand {

  private final MediaItem mediaItem;

  public PlayMedia(final @NonNull String title, final @NonNull MediaItem mediaItem) {
    super(title);
    this.mediaItem = mediaItem;
  }

  @Override public void execute(@NonNull Environment environment) {
    playMedia(environment);
  }

  protected @Nullable MediaSessionCompat.QueueItem playMedia(@NonNull Environment environment) {
    final QueueManager queueManager = environment.queueManager();
    final MediaControllerCompat.TransportControls controls = environment.playbackControls();
    if (controls == null) return null;

    final MediaSessionCompat.QueueItem queueItem = queueManager.createQueue("Track", mediaItem);
    controls.play();
    return queueItem;
  }
}
