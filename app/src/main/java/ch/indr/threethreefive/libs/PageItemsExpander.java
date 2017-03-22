/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.functions.Action2;

public class PageItemsExpander<T> {

  private List<ListMeta> lists = new ArrayList<>();
  private int currentListIndex = -1;

  public void add(List<T> listItems, String showTitle) {
    lists.add(new ListMeta(listItems, showTitle));
  }

  public void buildFirst(PageItemsBuilder builder, Action2<PageItemsBuilder, List<T>> addItemsFunc, Action1<Environment> expandFunc) {
    currentListIndex = 0;
    build(builder, addItemsFunc, expandFunc);
  }

  public void buildNext(PageItemsBuilder builder, Action2<PageItemsBuilder, List<T>> addItemsFunc, Action1<Environment> expandFunc) {
    currentListIndex++;
    build(builder, addItemsFunc, expandFunc);
  }

  private void build(PageItemsBuilder builder, Action2<PageItemsBuilder, List<T>> addItemsFunc, Action1<Environment> expandFunc) {
    if (currentListIndex < lists.size()) {
      final ListMeta currentList = lists.get(currentListIndex);
      addItemsFunc.call(builder, currentList.getItems());

      final ListMeta nextList = getNextListMeta(currentListIndex + 1, currentList);
      if (nextList != null) {
        builder.addItem(nextList.getTitle(), expandFunc);
      }
    } else {
      addItemsFunc.call(builder, new ArrayList<T>());
    }
  }

  private ListMeta getNextListMeta(int fromIndex, ListMeta currentList) {
    ListMeta nextList = fromIndex < lists.size() ? lists.get(fromIndex) : null;
    if (nextList == null) return null;

    // Iterate through the next lists to see if we can skip similar lists.
    // E.g.: Skip "Show more" in case "Show all" has equal number of items.
    for (int index = fromIndex + 1; index < lists.size(); index++) {
      ListMeta eachList = lists.get(index);

      if (eachList.size() <= nextList.size()) {
        nextList = eachList;
      } else {
        break;
      }
    }

    // Has the next list more items than the current list?
    if (nextList.size() > currentList.size()) {
      return nextList;
    }
    return null;
  }

  private class ListMeta {

    private final List<T> items;
    private final String title;

    public ListMeta(List<T> items, String showTitle) {
      this.items = items;
      this.title = showTitle;
    }

    public List<T> getItems() {
      return items;
    }

    public String getTitle() {
      return title;
    }

    public int size() {
      return items.size();
    }
  }
}
