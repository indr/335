/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.indr.threethreefive.ThreeThreeFiveApp;
import ch.indr.threethreefive.R;
import ch.indr.threethreefive.services.SpeakerType;
import ch.indr.threethreefive.services.UiModeManagerType;

public class UiSelectionActivity extends AppCompatActivity implements View.OnLongClickListener {

  protected @Inject SpeakerType speaker;
  protected @Inject UiModeManagerType uiModeManager;

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

  @Override protected void onStart() {
    super.onStart();

    speaker.sayUrgent("Press on the left half to launch the list interface. Press on the right half to launch the button interface.");
  }

  @OnClick(R.id.buttonButtonView)
  public void buttonButtonViewOnClick() {
    uiModeManager.launchButtonsUi(this);
    finish();
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
