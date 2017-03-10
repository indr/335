/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.android.uamp.playback.QueueManagerType;

import javax.inject.Inject;

import ch.indr.threethreefive.ThreeThreeFiveApp;
import ch.indr.threethreefive.libs.PageLink;
import ch.indr.threethreefive.services.UiModeManagerType;
import ch.indr.threethreefive.ui.IntentKey;

public class NowPlayingProxyActivity extends AppCompatActivity {

  protected @Inject QueueManagerType queueManager;
  protected @Inject UiModeManagerType uiModeManager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((ThreeThreeFiveApp) getApplication()).component().inject(this);

    Intent newIntent;
    if (uiModeManager.getCurrentUiMode() == UiModeManagerType.UI_MODE_BUTTONS) {
      newIntent = new Intent(this, ButtonGuideActivity.class);
    } else {
      newIntent = new Intent(this, ListGuideActivity.class);
    }

    final MediaSessionCompat.QueueItem queueItem = queueManager.getCurrentQueueItem();

    if (queueItem != null) {
      newIntent.putExtra(IntentKey.PAGE_TITLE, queueItem.getDescription().getTitle());
      newIntent.putExtra(IntentKey.PAGE_URI, "/playlist/" + queueItem.getQueueId());
    } else {
      PageLink pageLink = PageLink.NowPlaying;
      newIntent.putExtra(IntentKey.PAGE_TITLE, pageLink.getTitle());
      newIntent.putExtra(IntentKey.PAGE_URI, pageLink.getUri().toString());
    }

    startActivity(newIntent);
    finish();
  }
}
