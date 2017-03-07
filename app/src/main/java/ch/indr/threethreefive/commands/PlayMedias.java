package ch.indr.threethreefive.commands;

import android.support.annotation.NonNull;
import android.support.v4.media.session.MediaControllerCompat;

import com.example.android.uamp.playback.QueueManagerType;

import java.util.List;

import ch.indr.threethreefive.libs.MediaItem;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageCommand;

public class PlayMedias extends PageCommand {
  private String name;
  private List<MediaItem> mediaItems;

  public PlayMedias(String name, List<MediaItem> mediaItems) {
    this.name = name;
    this.mediaItems = mediaItems;
  }

  @NonNull @Override public String getName() {
    return name;
  }

  @Override public String getDescription() {
    return name;
  }

  @Override public void execute(@NonNull Environment environment) {
    final QueueManagerType queueManager = environment.queueManager();
    final MediaControllerCompat.TransportControls controls = environment.playbackControls();
    if (controls == null) return;

    queueManager.createQueue("Tracks", mediaItems);
    controls.play();
  }
}
