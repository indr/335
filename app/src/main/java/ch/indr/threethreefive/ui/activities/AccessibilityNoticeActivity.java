/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
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

  protected @BindView(R.id.textView1) TextView textView;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    component().inject(this);
    setContentView(R.layout.activity_accessibility_notice);
    ButterKnife.bind(this);

    viewModel.resourceId()
        .defaultIfEmpty(R.string.start_accessibility_warning)
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(resourceId -> textView.setText(resourceId));

    viewModel.launchButtonsUi()
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(__ -> {
          this.uiModeManager.launchButtonsUi(this);
          this.finish();
        });

    viewModel.launchListUi()
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(__ -> {
          this.uiModeManager.launchListUi(this);
          this.finish();
        });

    viewModel.launchUiSelection()
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(__ -> {
          Intent intent = new Intent(this, UiSelectionActivity.class);
          startActivity(intent);
          this.finish();
        });
  }

  @OnClick(R.id.button1)
  public void buttonOnClick() {
    viewModel.continueClicked();
  }
}
