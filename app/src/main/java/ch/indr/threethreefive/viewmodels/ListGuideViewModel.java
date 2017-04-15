/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.viewmodels;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import java.util.List;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageLink;
import ch.indr.threethreefive.libs.pages.Page;
import ch.indr.threethreefive.libs.utils.ObjectUtils;
import ch.indr.threethreefive.services.Speaker;
import ch.indr.threethreefive.ui.activities.ListGuideActivity;
import ch.indr.threethreefive.viewmodels.inputs.ListGuideViewModelInputs;
import ch.indr.threethreefive.viewmodels.outputs.ListGuideViewModelOutputs;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class ListGuideViewModel extends PageActivityViewModel<ListGuideActivity> implements ListGuideViewModelInputs, ListGuideViewModelOutputs {

  private final Speaker speaker;

  // INPUTS
  private final PublishSubject<PageItem> pageItemClick = PublishSubject.create();

  // OUTPUTS
  private final BehaviorSubject<Boolean> isHomePage = BehaviorSubject.create();
  private final BehaviorSubject<Boolean> isFavorable = BehaviorSubject.create();
  private final BehaviorSubject<Boolean> isFavorite = BehaviorSubject.create();

  public ListGuideViewModel(@NonNull Environment environment) {
    super(environment);

    this.speaker = environment.speaker();
  }

  @Override protected void onCreate(@NonNull Context context, @Nullable Bundle savedInstanceState) {
    super.onCreate(context, savedInstanceState);

    speaker.stop();

    pageItemClick.filter(pi -> pi != null)
        .compose(bindToLifecycle())
        .subscribe(this::executePageItem);

    page.switchMap(Page::parentPageLink)
        .map(ObjectUtils::isNull)
        .compose(bindToLifecycle())
        .subscribe(isHomePage);

    page.switchMap(Page::isFavorable)
        .compose(bindToLifecycle())
        .subscribe(isFavorable);

    page.switchMap(Page::isFavorite)
        .compose(bindToLifecycle())
        .subscribe(isFavorite);
  }

  public final ListGuideViewModelInputs inputs = this;

  public final ListGuideViewModelOutputs outputs = this;

  // INPUTS
  @Override public void pageItemClick(PageItem pageItem) {
    pageItemClick.onNext(pageItem);
  }

  @Override public void setFirstVisibleItem(Pair<Integer, Integer> value) {
    final Page page = this.page.getValue();
    if (page == null) {
      return;
    }
    page.setFirstVisibleItem(value);
  }

  // OUTPUTS
  @Override public Observable<Boolean> canGoUp() {
    return canGoUp;
  }

  @Override public Observable<Boolean> isHomePage() {
    return isHomePage;
  }

  @Override public Observable<String> pageTitle() {
    return pageTitle;
  }

  @Override public Observable<List<PageItem>> pageItems() {
    return pageItems;
  }

  @Override public Observable<PageLink> showPage() {
    return showPage;
  }

  @Override public Pair<Integer, Integer> getFirstVisibleItem() {
    final Page page = this.page.getValue();
    if (page == null) {
      return null;
    }
    return page.getFirstVisibleItem();
  }

  @Override public Observable<Boolean> isFavorable() {
    return isFavorable;
  }

  @Override public Observable<Boolean> isFavorite() {
    return isFavorite;
  }
}
