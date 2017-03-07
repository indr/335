/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.BaseFragment;
import ch.indr.threethreefive.libs.qualifiers.RequiresFragmentViewModel;
import ch.indr.threethreefive.libs.utils.PlaybackStateUtils;
import ch.indr.threethreefive.services.PlaybackAnnouncerType;
import ch.indr.threethreefive.services.SpeakerType;
import ch.indr.threethreefive.viewmodels.ButtonPlaybackFragmentViewModel;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.observeForUI;

@RequiresFragmentViewModel(ButtonPlaybackFragmentViewModel.class)
public class ButtonPlaybackFragment extends BaseFragment<ButtonPlaybackFragmentViewModel> {

  protected @Inject PlaybackAnnouncerType playbackAnnouncer;
  protected @Inject SpeakerType speaker;

  protected @Bind(R.id.buttonPlayPause) ImageButton buttonPlayPause;

  @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    component().inject(this);
    View view = inflater.inflate(R.layout.fragment_button_playback, container, false);
    ButterKnife.bind(this, view);

    viewModel.playbackState()
        .compose(observeForUI())
        .subscribe(this::setPlayPauseButton);

    return view;
  }

  @OnClick(R.id.buttonPlayPause)
  public void buttonPlayPauseOnClick() {
    viewModel.playPause();
  }

  @OnClick(R.id.buttonRewind)
  public void buttonSeekLeftOnClick() {
    viewModel.rewind();
  }

  @OnClick(R.id.buttonFastForward)
  public void buttonSeekRightOnClick() {
    viewModel.fastForward();
  }

  @OnClick(R.id.buttonSkipToPrevious)
  public void buttonSkipBackOnClick() {
    viewModel.skipToPrevious();
  }

  @OnClick(R.id.buttonSkipToNext)
  public void buttonSkipNextOnClick() {
    viewModel.skipToNext();
  }

  private void setPlayPauseButton(int playbackState) {
    int imageResource = 0;

    switch (PlaybackStateUtils.playPauseAction(playbackState)) {
      case PAUSE:
        imageResource = R.drawable.ic_pause_white_48dp;
        break;
      case PLAY:
        imageResource = R.drawable.ic_play_arrow_white_48dp;
        break;
      case STOP:
        imageResource = R.drawable.ic_stop_white_48dp;
        break;
    }

    buttonPlayPause.setImageResource(imageResource);
  }
}
