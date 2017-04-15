/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.BaseListActivity;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageLink;
import ch.indr.threethreefive.libs.Preferences;
import ch.indr.threethreefive.libs.preferences.FloatPreferenceType;
import ch.indr.threethreefive.libs.qualifiers.RequiresActivityViewModel;
import ch.indr.threethreefive.services.UiModeManager;
import ch.indr.threethreefive.ui.IntentKey;
import ch.indr.threethreefive.ui.adapters.PageItemsAdapter;
import ch.indr.threethreefive.viewmodels.ListGuideViewModel;
import rx.Observable;
import timber.log.Timber;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.observeForUI;

@RequiresActivityViewModel(ListGuideViewModel.class)
public class ListGuideActivity extends BaseListActivity<ListGuideViewModel> implements AbsListView.OnScrollListener {

  protected @Inject Preferences preferences;
  protected @Inject UiModeManager uiModeManager;

  protected @Bind(R.id.layoutChangeFontSize) LinearLayout layoutChangeFontSize;
  protected @Bind(R.id.list) ListView listView;
  protected @Bind(R.id.progressBarHolder) FrameLayout progressBarHolder;
  protected @Bind(R.id.toolbarButtonUp) ImageButton toolbarButtonUp;
  protected @Bind(R.id.toolbarTitle) TextView toolbarTitle;
  protected @Bind(R.id.toolbarButtonFavorite) ImageButton toolbarButtonFavorite;

  private PageItemsAdapter pageItemsAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    component().inject(this);
    setContentView(R.layout.activity_list_view);
    ButterKnife.bind(this);

    setTextSize(preferences.textSize().get());

    listView.setOnScrollListener(this);

    viewModel.outputs.canGoUp()
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(this::updateUpButton);

    viewModel.outputs.isHomePage()
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(this::updateFontSizeButtons);

    viewModel.outputs.pageTitle()
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(this::setTitle);

    viewModel.outputs.pageItems()
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(this::updatePageItems);

    viewModel.outputs.showPage()
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(this::showPage);

    viewModel.goBack()
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(__ -> super.back());

    Observable.combineLatest(viewModel.isFavorable(), viewModel.isFavorite(), Pair::create)
        .compose(bindToLifecycle())
        .compose(observeForUI())
        .subscribe(this::updateFavorite);

    preferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    preferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    if (this.pageItemsAdapter != null) {
      environment().preferences().unregisterOnSharedPreferenceChangeListener(this.pageItemsAdapter);
    }
  }

  @Override public void setTitle(CharSequence title) {
    super.setTitle(title);
    if (toolbarTitle != null) {
      toolbarTitle.setText(title);
    }
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

  private void updateFavorite(Pair<Boolean, Boolean> isFavorableAndIsFavorite) {
    Timber.d("updateFavorite %s, %s", isFavorableAndIsFavorite.first, isFavorableAndIsFavorite.second, this.toString());

    if (toolbarButtonFavorite == null) return;

    toolbarButtonFavorite.setVisibility(isFavorableAndIsFavorite.first ? View.VISIBLE : View.GONE);

    if (isFavorableAndIsFavorite.second) {
      toolbarButtonFavorite.setImageResource(R.drawable.ic_favorite_white_48dp);
      toolbarButtonFavorite.setColorFilter(getResources().getColor(R.color.yellow_400));
    } else {
      toolbarButtonFavorite.setImageResource(R.drawable.ic_favorite_border_white_48dp);
      toolbarButtonFavorite.setColorFilter(null);
    }
  }

  /**
   * Shows or hides font size buttons.
   */
  private void updateFontSizeButtons(boolean visible) {
    Timber.d("updateFontSizeButtons %s, %s", String.valueOf(visible), this.toString());

    if (layoutChangeFontSize != null) {
      layoutChangeFontSize.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
  }

  /**
   * Updates action bars up button
   */
  private void updateUpButton(boolean canGoUp) {
    if (toolbarButtonUp != null) {
      toolbarButtonUp.setVisibility(canGoUp ? View.VISIBLE : View.GONE);
    }
  }

  /**
   * Update the visible page items and manages the state of the progress bar.
   */
  private void updatePageItems(@Nullable List<PageItem> pageItems) {
    Timber.d("Updating page items %s", pageItems == null ? "null" : pageItems.size());

    updateProgressBar(pageItems == null);
    updateListView(pageItems);
  }

  private void updateProgressBar(boolean isVisible) {
    if (isVisible) {
      showProgressBar();
    } else {
      hideProgressBar();
    }
  }

  private void showProgressBar() {
    if (progressBarHolder == null || progressBarHolder.getVisibility() != View.GONE) return;

    AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
    inAnimation.setDuration(200);
    progressBarHolder.setAnimation(inAnimation);
    progressBarHolder.setVisibility(View.VISIBLE);
  }

  private void hideProgressBar() {
    if (progressBarHolder == null || progressBarHolder.getVisibility() != View.VISIBLE) return;

    AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
    outAnimation.setDuration(200);
    progressBarHolder.setAnimation(outAnimation);
    progressBarHolder.setVisibility(View.GONE);
  }

  private void updateListView(@Nullable List<PageItem> pageItems) {
    listView.setVisibility(pageItems == null ? View.GONE : View.VISIBLE);

    if (pageItems == null) pageItems = new ArrayList<>();

    if (this.pageItemsAdapter != null) {
      environment().sharedPreferences().unregisterOnSharedPreferenceChangeListener(this.pageItemsAdapter);
    }
    this.pageItemsAdapter = new PageItemsAdapter(this, pageItems, environment().preferences());
    environment().preferences().registerOnSharedPreferenceChangeListener(this.pageItemsAdapter);
    setListAdapter(this.pageItemsAdapter);


    final Pair<Integer, Integer> firstVisibleItem = viewModel.getFirstVisibleItem();
    if (firstVisibleItem != null) {
      listView.setSelectionFromTop(firstVisibleItem.first, firstVisibleItem.second);
    }
  }

  @Override protected int getListViewId() {
    return R.id.list;
  }

  @Override protected void onListItemClick(ListView listView, View view, int position, long id) {
    Timber.i("Position click %d", position);
    PageItem pageItem = (PageItem) getListAdapter().getItem(position);
    Timber.i("PageItem clicked %s", pageItem.toString());

    viewModel.inputs.pageItemClick(pageItem);
  }

  @Override public void back() {
    viewModel.inputs.back();
  }

  @Override public boolean onSupportNavigateUp() {
    viewModel.inputs.back();
    return true;
  }

  @OnClick(R.id.buttonDecFontSize)
  public void setButtonDecFontSizeOnClick() {
    final FloatPreferenceType textSize = preferences.textSize();
    textSize.set(textSize.get() - 3);
  }

  @OnClick(R.id.buttonIncFontSize)
  public void setButtonIncFontSizeOnClick() {
    final FloatPreferenceType textSize = preferences.textSize();
    textSize.set(textSize.get() + 3);
  }

  @OnClick(R.id.toolbarButtonUp)
  public void toolbarButtonUpOnClick() {
    viewModel.inputs.up();
  }

  @OnClick(R.id.toolbarButtonFavorite)
  public void setToolbarButtonFavoriteOnClick() {
    viewModel.inputs.toggleFavorite();
  }

  private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
    @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      setTextSize(preferences.textSize().get());
    }
  };

  private void setTextSize(final float textSize) {
    if (toolbarTitle != null) {
      toolbarTitle.setTextSize(textSize * (22f / 18f));
    }

    // Doesn't work on low resolution screens...
//    if (toolbarButtonUp != null) {
//      // It's a kind of magic numbers!
//      final int left = Math.min(-8, (int) textSize - 44);
//      final int right = Math.min(8, (int) textSize - 24);
//      final LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) toolbarButtonUp.getLayoutParams();
//      layoutParams.setMargins(left, layoutParams.topMargin, right, layoutParams.bottomMargin);
//      toolbarButtonUp.setLayoutParams(layoutParams);
//    }
  }

  @Override public void onScrollStateChanged(AbsListView absListView, int scrollState) {
    // Timber.d("onScrollStateChanged scrollState %d, %s", scrollState, this.toString());
  }

  @Override public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    // Timber.d("onScroll firstVisibleItem %d, visibleItemCount %d, totalItemCount %d, %s", firstVisibleItem, visibleItemCount, totalItemCount, this.toString());

    final View itemView = absListView.getChildAt(0);
    int top = itemView == null ? 0 : itemView.getTop() - absListView.getPaddingTop();

    viewModel.setFirstVisibleItem(Pair.create(firstVisibleItem, top));
  }
}
