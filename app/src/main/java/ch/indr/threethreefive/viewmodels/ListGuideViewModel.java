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

import java.util.List;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageLink;
import ch.indr.threethreefive.libs.utils.ObjectUtils;
import ch.indr.threethreefive.navigation.Page;
import ch.indr.threethreefive.services.SpeakerType;
import ch.indr.threethreefive.ui.activities.ListGuideActivity;
import ch.indr.threethreefive.viewmodels.inputs.ListGuideViewModelInputs;
import ch.indr.threethreefive.viewmodels.outputs.ListGuideViewModelOutputs;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class ListGuideViewModel extends PageActivityViewModel<ListGuideActivity> implements ListGuideViewModelInputs, ListGuideViewModelOutputs {

  private final SpeakerType speaker;

  // INPUTS
  private final PublishSubject<PageItem> pageItemClick = PublishSubject.create();

  // OUTPUTS
  private final BehaviorSubject<Boolean> isHomePage = BehaviorSubject.create();

  public ListGuideViewModel(@NonNull Environment environment) {
    super(environment);

    this.speaker = environment.speaker();
  }

  @Override protected void onCreate(@NonNull Context context, @Nullable Bundle savedInstanceState) {
    super.onCreate(context, savedInstanceState);

    speaker.stop();

    pageItemClick
        .filter(pi -> pi != null)
        .compose(bindToLifecycle())
        .subscribe(this::executePageItem);

    page.switchMap(Page::parentPageLink)
        .map(ObjectUtils::isNull)
        .compose(bindToLifecycle())
        .subscribe(isHomePage);
  }

  public final ListGuideViewModelInputs inputs = this;

  public final ListGuideViewModelOutputs outputs = this;

  // INPUTS
  @Override public void pageItemClick(PageItem pageItem) {
    pageItemClick.onNext(pageItem);
  }

  // OUTPUTS
  @Override public Observable<Boolean> canGoUp() {
    // TODO: Refactor to isHomePage?
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
}
