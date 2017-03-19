/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android.uamp.playback.QueueManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.BaseFragment;
import ch.indr.threethreefive.libs.PageLink;
import ch.indr.threethreefive.libs.Preferences;
import ch.indr.threethreefive.libs.qualifiers.RequiresFragmentViewModel;
import ch.indr.threethreefive.libs.utils.PlaybackStateUtils.PlayPauseAction;
import ch.indr.threethreefive.ui.IntentKey;
import ch.indr.threethreefive.ui.activities.ListGuideActivity;
import ch.indr.threethreefive.ui.utils.OnTouchClickListener;
import ch.indr.threethreefive.viewmodels.ListPlaybackFragmentViewModel;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.observeForUI;

@RequiresFragmentViewModel(ListPlaybackFragmentViewModel.class)
public class ListPlaybackFragment extends BaseFragment<ListPlaybackFragmentViewModel> {

  private static final long PROGRESS_UPDATE_INTERNAL = 1000;
  private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

  protected @Bind(R.id.buttonPlayPause) ImageButton buttonPlayPause;
  protected @Bind(R.id.layoutSeekControl) RelativeLayout layoutSeekControl;
  protected @Bind(R.id.playbackControls) LinearLayout playbackControls;
  protected @Bind(R.id.seekBar) SeekBar seekBar;
  protected @Bind(R.id.scrollViewTitle) HorizontalScrollView scrollViewTitle;
  protected @Bind(R.id.textViewTitle) TextView textViewTitle;
  protected @Bind(R.id.textViewPlaceholder) @Nullable TextView textViewDummy;
  protected @Bind(R.id.textViewSeekStart) TextView textViewSeekStart;
  protected @Bind(R.id.textViewSeekEnd) TextView textViewSeekEnd;

  private Preferences preferences;

  private final ScheduledExecutorService mExecutorService =
      Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture<?> mScheduleFuture;
  private final Handler mHandler = new Handler();
  private final Runnable mUpdateProgressTask = this::updateSeekBarProgress;

  private static final int SCROLL_AMOUNT = 3;

  private int nextScrollByAmount = SCROLL_AMOUNT;
  private BehaviorSubject<Boolean> autoScrollEnabled = BehaviorSubject.create(true);
  private PublishSubject<Boolean> delayResumeAutoScroll = PublishSubject.create();
  private PlaybackStateCompat lastPlaybackState;
  private int desiredVisibility;
  private QueueManager queueManager;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    this.preferences = environment().preferences();
    this.queueManager = environment().queueManager();
  }

  @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    View view = inflater.inflate(R.layout.fragment_list_playback, container, false);
    ButterKnife.bind(this, view);

    setTextSize(preferences.textSize().get());

    viewModel.visible()
        .compose(observeForUI())
        .subscribe(this::setVisibility);

    viewModel.playPauseAction()
        .compose(observeForUI())
        .subscribe(this::setPlayPauseButton);

    viewModel.statusText()
        .filter(__ -> textViewTitle != null)
        .compose(observeForUI())
        .subscribe(text -> {
          scrollViewTitle.scrollTo(0, 0);
          nextScrollByAmount = SCROLL_AMOUNT;
          textViewTitle.setText(text);
        });


    preferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    initSeekBar();
    initScrollView();

    return view;
  }

  private void initSeekBar() {

    viewModel.durationInMs()
        .map(Long::intValue)
        .compose(observeForUI())
        .subscribe((duration) -> {
          layoutSeekControl.setVisibility(duration > 0 ? View.VISIBLE : View.GONE);
          seekBar.setProgress(0);
          seekBar.setMax(duration);
          String formatted = DateUtils.formatElapsedTime(duration / 1000);
          textViewSeekStart.setText("00:00");
          textViewSeekEnd.setText(formatted);
        });

    viewModel.autoUpdateSeekBar()
        .distinctUntilChanged()
        .compose(observeForUI())
        .subscribe(autoUpdate -> {
          if (autoUpdate) {
            scheduleSeekBarUpdate();
          } else {
            stopSeekBarUpdate();
          }
        });

    viewModel.playbackStateCompat()
        .compose(observeForUI())
        .subscribe(stateCompat -> this.lastPlaybackState = stateCompat);

    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        textViewSeekStart.setText(DateUtils.formatElapsedTime(progress / 1000));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        stopSeekBarUpdate();
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        viewModel.seekTo(seekBar.getProgress());
        scheduleSeekBarUpdate();
      }
    });
  }

  private void scheduleSeekBarUpdate() {
    stopSeekBarUpdate();
    if (!mExecutorService.isShutdown()) {
      mScheduleFuture = mExecutorService.scheduleAtFixedRate(() ->
          mHandler.post(mUpdateProgressTask), PROGRESS_UPDATE_INITIAL_INTERVAL, PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
    }
  }

  private void stopSeekBarUpdate() {
    if (mScheduleFuture != null) {
      mScheduleFuture.cancel(false);
    }
  }

  private void updateSeekBarProgress() {
    Timber.d("updateSeekBarProgress %s", this.toString());

    if (lastPlaybackState == null) {
      return;
    }

    long currentPosition = lastPlaybackState.getPosition();
    if (lastPlaybackState.getState() != PlaybackStateCompat.STATE_PAUSED) {
      long timeDelta = SystemClock.elapsedRealtime() -
          lastPlaybackState.getLastPositionUpdateTime();
      currentPosition += (int) timeDelta * lastPlaybackState.getPlaybackSpeed();
    }
    seekBar.setProgress((int) currentPosition);
  }

  private void initScrollView() {
    delayResumeAutoScroll
        .debounce(1000, TimeUnit.MILLISECONDS)
        .compose(bindToLifecycle())
        .subscribe(autoScrollEnabled);

    textViewTitle.setOnTouchListener(new TouchListener());
    scrollViewTitle.setOnTouchListener(new TouchListener());
  }

  @Override public void onResume() {
    super.onResume();

    Observable.interval(200, TimeUnit.MILLISECONDS)
        .compose(bindToLifecycle())
        .withLatestFrom(autoScrollEnabled, (__, enabled) -> enabled)
        .filter(enabled -> enabled)
        .withLatestFrom(viewModel.playbackState(), (__, state) -> state)
        .filter(state -> state == PlaybackStateCompat.STATE_PLAYING)
        .subscribe(__ -> {
          if (scrollViewTitle == null) {
            return;
          }

          // Invert scroll direction at either ends
          if (!scrollViewTitle.canScrollHorizontally(nextScrollByAmount > 0 ? 1 : -1)) {
            nextScrollByAmount *= -1;
          }

          scrollViewTitle.scrollBy(nextScrollByAmount, 0);
        });
  }

  @Override public void onPause() {
    super.onPause();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();

    preferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
  }

  @OnClick(R.id.buttonPlayPause)
  public void buttonPlayPauseOnClick() {
    viewModel.playPause();
  }

  @OnClick(R.id.buttonSkipToPrevious)
  public void buttonSkipToPreviousOnClick() {
    viewModel.skipToPrevious();
  }

  @OnClick(R.id.buttonSkipToNext)
  public void buttonSkipToNextOnClick() {
    viewModel.skipToNext();
  }

  private void setVisibility(int visibility) {
    this.desiredVisibility = visibility;
    updateVisibility(visibility);
  }

  private void updateVisibility(int visibility) {
    if (textViewTitle != null) textViewTitle.setVisibility(visibility);
    if (playbackControls != null) playbackControls.setVisibility(visibility);
  }

  public void setVisible(boolean value) {
    updateVisibility(value ? this.desiredVisibility : View.GONE);
  }

  private void setPlayPauseButton(PlayPauseAction playPauseAction) {
    int imageResource;

    switch (playPauseAction) {
      case PAUSE:
        imageResource = R.drawable.ic_pause_black_48dp;
        break;
      case PLAY:
        imageResource = R.drawable.ic_play_arrow_black_48dp;
        break;
      case STOP:
        imageResource = R.drawable.ic_stop_black_48dp;
        break;
      default: {
        Timber.w("Unknown playPauseAction %s", playPauseAction);
        return;
      }
    }

    buttonPlayPause.setImageResource(imageResource);
  }

  private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
    @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      setTextSize(preferences.textSize().get());
    }
  };

  private void setTextSize(final float textSize) {
    if (textViewTitle != null) {
      textViewTitle.setTextSize(textSize);
    }
    if (textViewDummy != null) {
      textViewDummy.setTextSize(textSize);
    }
    if (textViewSeekStart != null) {
      textViewSeekStart.setTextSize(textSize * 0.5f);
    }
    if (textViewSeekEnd != null) {
      textViewSeekEnd.setTextSize(textSize * 0.5f);
    }
  }

  private void transitionToNowPlaying() {
    Timber.d("transitionToNowPlaying %s", this.toString());

    Intent intent = new Intent(this.getContext(), ListGuideActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

    final MediaSessionCompat.QueueItem queueItem = queueManager.getCurrentQueueItem();
    if (queueItem != null) {
      intent.putExtra(IntentKey.PAGE_TITLE, queueItem.getDescription().getTitle());
      intent.putExtra(IntentKey.PAGE_URI, "/playlist/" + queueItem.getQueueId());
    } else {
      PageLink pageLink = PageLink.NowPlaying;
      intent.putExtra(IntentKey.PAGE_TITLE, pageLink.getTitle());
      intent.putExtra(IntentKey.PAGE_URI, pageLink.getUri().toString());
    }

    startActivity(intent);
  }


  private class TouchListener extends OnTouchClickListener {

    @Override public boolean onClick(View view) {
      transitionToNowPlaying();

      return super.onClick(view);
    }

    @Override public boolean onTouch(View view, MotionEvent motionEvent) {
      autoScrollEnabled.onNext(false);
      delayResumeAutoScroll.onNext(true);

      return super.onTouch(view, motionEvent);
    }
  }
}
