/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PreferencesType;
import ch.indr.threethreefive.ui.utils.OnTouchClickListener;

/**
 * https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView#defining-the-adapter
 */
public class PageItemsAdapter extends ArrayAdapter<PageItem> implements SharedPreferences.OnSharedPreferenceChangeListener {

  private final PreferencesType preferences;
  private float textSize;
  private HashSet<TextView> textViews = new HashSet<>();

  public PageItemsAdapter(Context context, List<PageItem> pageItems, @NonNull PreferencesType preferences) {
    super(context, 0, pageItems);

    this.preferences = preferences;
    this.textSize = preferences.textSize().get();
  }

  @Override @NonNull public View getView(int position, View convertView, @NonNull ViewGroup parent) {
    // Get the data item for this position
    PageItem pageItem = getItem(position);

    // Check if an existing view is being reused, otherwise inflate the view
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item, parent, false);
    }

    // Lookup views for data population and touch listening
    TextView textView = (TextView) convertView.findViewById(R.id.text1);
    HorizontalScrollView scrollView = (HorizontalScrollView) convertView.findViewById(R.id.scrollView1);
    scrollView.scrollTo(0, 0);

    // Put TextView in HashSet so we can update the appearance later
    textViews.add(textView);

    // Set initial appearance
    textView.setTextSize(textSize);

    if (pageItem != null) {
      // This may be dangerous:
      // This subscription will hold a reference to the TextView and the PageItem.
      // PageItems are released when a Page is destroyed, so we should be fine.
      pageItem.description().subscribe(textView::setContentDescription);
      pageItem.name().subscribe(textView::setText);
    }

    // Attach touch listeners to perform a list item click
    // If you want to puke, see: https://code.google.com/p/android/issues/detail?id=3414
    textView.setOnTouchListener(new TouchListener(parent, position));
    scrollView.setOnTouchListener(new TouchListener(parent, position));

    // Return the completed view to render on screen
    return convertView;
  }

  @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    this.textSize = preferences.textSize().get();
    for (TextView textView : textViews) {
      textView.setTextSize(textSize);
    }
  }

  private class TouchListener extends OnTouchClickListener {

    private final ViewGroup parent;
    private final int position;

    TouchListener(ViewGroup parent, int position) {
      this.parent = parent;
      this.position = position;
    }

    @Override public boolean onClick(View view) {
      if (!parent.getClass().equals(ListView.class)) {
        return false;
      }
      ListView listView = (ListView) parent;
      listView.performItemClick(view, position, view.getId());
      return true;
    }
  }
}
