/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.indr.threethreefive.R;
import ch.indr.threethreefive.ThreeThreeFiveApp;
import ch.indr.threethreefive.libs.BaseActivity;
import ch.indr.threethreefive.libs.qualifiers.RequiresActivityViewModel;
import ch.indr.threethreefive.services.AccessibilityServices;
import ch.indr.threethreefive.services.Speaker;
import ch.indr.threethreefive.services.UiModeManager;
import ch.indr.threethreefive.ui.IntentKey;
import ch.indr.threethreefive.viewmodels.UiSelectionViewModel;

@RequiresActivityViewModel(UiSelectionViewModel.class)
public class UiSelectionActivity extends BaseActivity<UiSelectionViewModel> implements View.OnLongClickListener {

  protected @Inject Speaker speaker;
  protected @Inject UiModeManager uiModeManager;

  protected @Bind(R.id.buttonButtonView) Button buttonButtonView;
  protected @Bind(R.id.buttonListView) Button buttonListView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((ThreeThreeFiveApp) getApplication()).component().inject(this);
    setContentView(R.layout.activity_ui_selection);
    ButterKnife.bind(this);

    setTitle("Select Interface");

    buttonButtonView.setOnLongClickListener(this);
    buttonListView.setOnLongClickListener(this);
  }

  @OnClick(R.id.buttonButtonView)
  public void buttonButtonViewOnClick() {
    if (isAccessibilityServicesEnabled()) {
      Intent intent = new Intent(this, AccessibilityNoticeActivity.class);
      intent.putExtra(IntentKey.ACCESSIBILITY_NOTICE_REASON, "accessibility service");
      intent.putExtra(IntentKey.ACCESSIBILITY_NOTICE_RESOURCE_ID, R.string.start_accessibility_warning);
      intent.putExtra(IntentKey.NEXT_ACTIVITY, ButtonGuideActivity.class.getSimpleName());
      this.startActivity(intent);
    } else {
      uiModeManager.launchButtonsUi(this);
      finish();
    }
  }

  private boolean isAccessibilityServicesEnabled() {
    final AccessibilityServices accessibilityServices = AccessibilityServices.newInstance(this);
    return accessibilityServices.isSpokenFeedbackEnabled() || accessibilityServices.isTouchExplorationEnabled();
  }

  @OnClick(R.id.buttonListView)
  public void buttonListViewOnClick() {
    uiModeManager.launchListUi(this);
    finish();
  }

  @Override public boolean onLongClick(View view) {
    speaker.sayUrgent(view.getContentDescription());
    return true;
  }
}
