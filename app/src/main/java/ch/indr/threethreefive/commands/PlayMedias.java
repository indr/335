package ch.indr.threethreefive.commands;

import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaControllerCompat;

import com.example.android.uamp.playback.QueueManager;

import java.util.List;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.libs.PageCommand;

public class PlayMedias extends PageCommand {

  private List<MediaItem> mediaItems;

  public PlayMedias(final @NonNull String title, final @NonNull List<MediaItem> mediaItems) {
    super(title);
    this.mediaItems = mediaItems;
  }

  @Override public void execute(@NonNull Environment environment) {
    final QueueManager queueManager = environment.queueManager();
    final MediaControllerCompat.TransportControls controls = environment.playbackControls();
    if (controls == null) return;

    queueManager.createQueue("Tracks", mediaItems);
    controls.play();
  }
}
