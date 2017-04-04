/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.BaseActivity;
import ch.indr.threethreefive.libs.qualifiers.RequiresActivityViewModel;
import ch.indr.threethreefive.services.UiModeManager;
import ch.indr.threethreefive.viewmodels.AccessibilityNoticeViewModel;

@RequiresActivityViewModel(AccessibilityNoticeViewModel.class)
public class AccessibilityNoticeActivity extends BaseActivity<AccessibilityNoticeViewModel> {

  protected @Inject UiModeManager uiModeManager;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    component().inject(this);
    setContentView(R.layout.activity_accessibility_notice);
    ButterKnife.bind(this);
  }

  @OnClick(android.R.id.content)
  public void contentOnClick() {
    uiModeManager.launchButtonsUi(this);
    finish();
  }
}
