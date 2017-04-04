/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.BaseActivity;
import ch.indr.threethreefive.libs.qualifiers.RequiresActivityViewModel;
import ch.indr.threethreefive.services.UiModeManager;
import ch.indr.threethreefive.viewmodels.AccessibilityNoticeViewModel;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.observeForUI;

@RequiresActivityViewModel(AccessibilityNoticeViewModel.class)
public class AccessibilityNoticeActivity extends BaseActivity<AccessibilityNoticeViewModel> {

  protected @Inject UiModeManager uiModeManager;

  protected @Bind(R.id.textView1) TextView textView;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    component().inject(this);
    setContentView(R.layout.activity_accessibility_notice);
    ButterKnife.bind(this);

    viewModel.resourceId()
        .defaultIfEmpty(R.string.start_accessibility_warning)
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(this::updateNotice);
  }

  @OnClick(android.R.id.content)
  public void contentOnClick() {
    uiModeManager.launchButtonsUi(this);
    finish();
  }

  private void updateNotice(final int resourceId) {
    textView.setText(resourceId);
  }
}
