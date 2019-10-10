/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.BaseFragment;
import ch.indr.threethreefive.libs.Preferences;
import ch.indr.threethreefive.libs.qualifiers.RequiresFragmentViewModel;
import ch.indr.threethreefive.ui.utils.TouchGestureListener;
import ch.indr.threethreefive.viewmodels.ToastFragmentViewModel;
import timber.log.Timber;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.observeForUI;
import static ch.indr.threethreefive.services.ToastManager.Toast;

@RequiresFragmentViewModel(ToastFragmentViewModel.class)
public class ToastFragment extends BaseFragment<ToastFragmentViewModel> {

  protected @BindView(R.id.frameLayoutToast) FrameLayout frameLayout;
  protected @BindView(R.id.scrollViewTitle) HorizontalScrollView scrollViewTitle;
  protected @BindView(R.id.textViewTitle) TextView textView;

  private Preferences preferences;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    this.preferences = environment().preferences();
  }

  @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    View view = inflater.inflate(R.layout.fragment_toast, container, false);
    ButterKnife.bind(this, view);

    setTextSize(preferences.textSize().get());

    frameLayout.setVisibility(View.GONE);

    final TouchListener touchListener = new TouchListener(getActivity());
    scrollViewTitle.setOnTouchListener(touchListener);
    textView.setOnTouchListener(touchListener);

    viewModel.hideToast()
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(this::hideToast);

    preferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();

    preferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
  }

  @Override public void onResume() {
    super.onResume();

    viewModel.showToast()
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(this::showToast);
  }

  private void hideToast(Toast toast) {
    Timber.d("hideToast %s", this.toString());

    Animation outAnimation = new AlphaAnimation(1f, 0f);
    outAnimation.setDuration(500);
    frameLayout.setAnimation(outAnimation);
    frameLayout.setVisibility(View.GONE);
  }

  private void showToast(Toast toast) {
    Timber.d("showToast", this.toString());

    scrollViewTitle.setScrollX(0);
    textView.setText(toast.getText());

    Animation inAnimation = new AlphaAnimation(0f, 1f);
    inAnimation.setDuration(500);
    frameLayout.setAnimation(inAnimation);
    frameLayout.setVisibility(View.VISIBLE);
  }

  private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
    @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      setTextSize(preferences.textSize().get());
    }
  };

  private void setTextSize(final float textSize) {
    if (textView != null) {
      textView.setTextSize(textSize * (14f / 18f));
    }
  }

  private class TouchListener extends TouchGestureListener {
    TouchListener(Context context) {
      super(context);
    }

    @Override public boolean onSingleTapUp(MotionEvent e) {
      viewModel.toastClicked();
      return super.onSingleTapUp(e);
    }

    @Override public boolean onTouch(View view, MotionEvent motionEvent) {
      viewModel.toastTouched();
      return super.onTouch(view, motionEvent);
    }
  }
}
