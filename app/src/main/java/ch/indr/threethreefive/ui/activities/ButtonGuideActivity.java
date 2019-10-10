/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.trello.rxlifecycle.ActivityEvent;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.BaseActivity;
import ch.indr.threethreefive.libs.PageLink;
import ch.indr.threethreefive.libs.qualifiers.RequiresActivityViewModel;
import ch.indr.threethreefive.services.PlaybackAnnouncer;
import ch.indr.threethreefive.services.PlaybackClient;
import ch.indr.threethreefive.services.Speaker;
import ch.indr.threethreefive.services.ToastManager;
import ch.indr.threethreefive.ui.IntentKey;
import ch.indr.threethreefive.viewmodels.ButtonGuideViewModel;
import timber.log.Timber;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.observeForUI;
import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.takeWhen;

@RequiresActivityViewModel(ButtonGuideViewModel.class)
public class ButtonGuideActivity extends BaseActivity<ButtonGuideViewModel> {

  protected @Inject PlaybackAnnouncer playbackAnnouncer;
  protected @Inject Speaker speaker;
  protected @Inject ToastManager toastManager;
  protected @Inject PlaybackClient playbackClient;

  protected @BindView(R.id.buttonEnter) Button buttonEnter;
  protected @BindView(R.id.progressBarHolder) FrameLayout progressBarHolder;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    component().inject(this);
    setContentView(R.layout.activity_button_view);
    ButterKnife.bind(this);

    initActionBar();
    initButtons();

    // If accessibility service spoken feedback is enabled, the title will
    // be spoken. This utterance interferes with the welcome/intro speech
    // and navigation utterances.
    // Therefore: We set activity title to null and don't observe the
    // view models activity title observable.
    setTitle(null);
    // viewModel.activityTitle()
    //     .compose(bindToLifecycle())
    //     .compose(observeForUI())
    //     .subscribe(this::setTitle);

    viewModel.activityTitle()
        .compose(bindToLifecycle())
        .compose(takeWhen(lifecycle().filter(event -> event == ActivityEvent.RESUME)))
        .skip(1) // Skip first resume, that is, after create
        .compose(observeForUI())
        .subscribe(title -> {
          Timber.d("onResume, speaking activity title");
          speaker.sayIdle(title);
        });

    viewModel.canGoUp()
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(this::updateUpButton);

    viewModel.showPage()
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(this::showPage);

    viewModel.goBack()
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(__ -> super.back());
  }

  private void initActionBar() {
    updateUpButton(false);
  }

  private void initButtons() {
    int[] buttonIds = new int[]{
        R.id.buttonHome, R.id.buttonUp, R.id.buttonLocation,
        R.id.buttonStepLeft, R.id.buttonEnter, R.id.buttonStepRight,

        // In fragment, but it seems to work?
        R.id.buttonSkipToPrevious, R.id.buttonRewind, R.id.buttonPlayPause, R.id.buttonFastForward, R.id.buttonSkipToNext
    };

    for (int buttonId : buttonIds) {
      View button = findViewById(buttonId);
      if (button != null) {
        button.setOnLongClickListener(onLongClickButtons);
      }
    }
  }

  @Override protected void onResume() {
    super.onResume();

    playbackAnnouncer.start();
  }

  @Override protected void onPause() {
    super.onPause();

    playbackAnnouncer.stop();
  }

  /**
   * Starts activity with the provided page link.
   */
  private void showPage(@NonNull PageLink pageLink) {
    Intent intent = new Intent(this, this.getClass());
    intent.putExtra(IntentKey.PAGE_TITLE, pageLink.getTitle());
    intent.putExtra(IntentKey.PAGE_URI, pageLink.getUri().toString());
    intent.putExtra(IntentKey.PAGE_REPLACE, pageLink.getReplace());

    viewModel.intent(intent);
  }

  private void updateUpButton(boolean canGoUp) {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar == null) return;

    actionBar.setDisplayHomeAsUpEnabled(canGoUp);
    actionBar.setHomeButtonEnabled(canGoUp);
  }

  @OnClick(R.id.buttonHome)
  protected void buttonHomeOnClick() {
    viewModel.inputs.home();
  }

  @OnClick(R.id.buttonUp)
  public void buttonUpOnClick() {
    viewModel.inputs.up();
  }

  @OnClick(R.id.buttonLocation)
  public void buttonLocationOnClick() {
    viewModel.inputs.location();
  }

  @OnClick(R.id.buttonEnter)
  public void buttonEnterOnClick() {
    viewModel.inputs.enter();
  }

  @OnClick(R.id.buttonStepLeft)
  public void buttonStepLeftOnClick() {
    viewModel.inputs.stepLeft();
  }

  @OnClick(R.id.buttonStepRight)
  public void buttonStepRightOnClick() {
    viewModel.inputs.stepRight();
  }

  private View.OnLongClickListener onLongClickButtons = new View.OnLongClickListener() {
    @Override public boolean onLongClick(View view) {
      CharSequence contentDescription = view.getContentDescription();
      if (contentDescription != null) {
        Timber.i("Say " + contentDescription);
        speaker.sayUrgent(contentDescription);
      }
      return contentDescription != null; // true if the callback consumed the long click, false otherwise.
    }
  };

  @Override public void back() {
    viewModel.inputs.back();
  }

  @Override public boolean onSupportNavigateUp() {
    viewModel.inputs.up();
    return true;
  }
}
