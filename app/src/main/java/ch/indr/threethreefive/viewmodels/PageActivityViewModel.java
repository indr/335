/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.viewmodels;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Stack;

import ch.indr.threethreefive.libs.ActivityLifecycleType;
import ch.indr.threethreefive.libs.ActivityViewModel;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageCommand;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageLink;
import ch.indr.threethreefive.libs.PageManager;
import ch.indr.threethreefive.libs.utils.ObjectUtils;
import ch.indr.threethreefive.libs.utils.StringUtils;
import ch.indr.threethreefive.navigation.Page;
import ch.indr.threethreefive.ui.IntentKey;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.coalesce;

public abstract class PageActivityViewModel<ViewType extends ActivityLifecycleType> extends ActivityViewModel<ViewType> {

  protected final Stack<Page> pageStack = new Stack<>();

  protected final Observable<String> pageTitleFromIntent;
  protected final Observable<Uri> pageUriFromIntent;

  // OUTPUTS
  protected final PublishSubject<Object> goBack = PublishSubject.create();
  protected final BehaviorSubject<Boolean> canGoUp = BehaviorSubject.create(false);
  protected final BehaviorSubject<String> pageTitle = BehaviorSubject.create();
  protected final BehaviorSubject<List<PageItem>> pageItems = BehaviorSubject.create((List<PageItem>) null);
  protected final PublishSubject<PageLink> showPage = PublishSubject.create();

  protected final BehaviorSubject<Page> page = BehaviorSubject.create();

  public PageActivityViewModel(@NonNull Environment environment) {
    super(environment);

    pageUriFromIntent = intent()
        .filter(i -> i.hasExtra(IntentKey.PAGE_URI) || pageStack.size() == 0)
        .map(i -> i.getStringExtra(IntentKey.PAGE_URI))
        .map(uriString -> {
          Timber.d("pageUriFromIntent uriString %s, %s", uriString, this.toString());
          return uriString;
        })
        .compose(coalesce(""))
        .map(Uri::parse)
        .startWith(PageLink.HomePage.getUri())
        .onErrorReturn(__ -> Uri.EMPTY);

    pageTitleFromIntent = intent()
        .filter(i -> i.hasExtra(IntentKey.PAGE_TITLE))
        .map(i -> i.getStringExtra(IntentKey.PAGE_TITLE))
        .compose(coalesce(""));
  }

  @Override protected void onCreate(@NonNull Context context, @Nullable Bundle savedInstanceState) {
    super.onCreate(context, savedInstanceState);

    pageUriFromIntent
        .compose(bindToLifecycle())
        .map(uri -> PageManager.fetch(context, uri))
        .subscribe(this::transitionTo);

    page.switchMap(Page::pageItems)
        .compose(bindToLifecycle())
        .subscribe(pageItems);

    pageTitleFromIntent
        .filter(StringUtils::isNotEmpty)
        .mergeWith(page.switchMap(Page::pageTitle))
        .compose(bindToLifecycle())
        .subscribe(pageTitle);

    page.switchMap(Page::parentPageLink)
        .map(ObjectUtils::isNotNull)
        .compose(bindToLifecycle())
        .subscribe(canGoUp);
  }

  @Override protected void onPause() {
    super.onPause();

    Page page = this.page.getValue();
    if (page != null) {
      PageManager.pause(page);
    }
  }

  @Override protected void onResume(@NonNull ViewType view) {
    super.onResume(view);

    Page page = this.page.getValue();
    if (page != null) {
      PageManager.resume(page);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    PageManager.destroy(pageStack);
  }

  private void transitionTo(@NonNull Page newPage) {
    Timber.d("transitionTo new %s, %s", newPage.toString(), this.toString());
    Page oldPage = this.page.getValue();
    transitionTo(oldPage, newPage);

    if (newPage.getIsRootPage()) {
      Timber.d("New page is a root page, clearing stack");
      PageManager.destroy(pageStack);
    }

    Timber.d("Adding page to stack %s, %s", newPage.toString(), this.toString());
    pageStack.add(newPage);
  }

  private void transitionTo(@Nullable Page oldPage, @NonNull Page newPage) {
    Timber.d("transitionTo old %s, new %s, %s", oldPage == null ? "null" : oldPage.toString(), newPage.toString(), this.toString());

    PageManager.resume(newPage);
    this.page.onNext(newPage);
    if (oldPage != null) {
      PageManager.pause(oldPage);
    }
  }

  protected void executePageItem(@NonNull PageItem pageItem) {
    if (PageLink.class.isAssignableFrom(pageItem.getClass())) {
      showPage.onNext((PageLink) pageItem);

    } else if (PageCommand.class.isAssignableFrom(pageItem.getClass())) {
      ((PageCommand) pageItem).execute(environment());
    }
  }

  // INPUTS
  public void back() {
    if (pageStack.size() < 2) {
      // Issue default back behavior
      goBack.onNext(null);
      return;
    }

    // Pop current page, old page
    Page oldPage = pageStack.pop();
    Timber.d("Popped page from stack %s, %s", oldPage.toString(), this.toString());

    // Peek previous page, new page
    Page newPage = pageStack.peek();

    // Make transition, this issues onPause and onResume
    transitionTo(oldPage, newPage);

    // Destroy old page
    PageManager.destroy(oldPage);
  }

  public void up() {
    back();
  }

  // OUTPUTS
  public Observable<Object> goBack() {
    return goBack;
  }
}
