/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.Preferences;
import ch.indr.threethreefive.ui.utils.OnTouchClickListener;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView#defining-the-adapter
 */
public class PageItemsAdapter extends ArrayAdapter<PageItem> implements SharedPreferences.OnSharedPreferenceChangeListener {

  // ripple_material_light
  private static final int highlightColor = Color.argb(0x1f, 0, 0, 0);

  private final Preferences preferences;
  private final HashMap<View, List<Subscription>> subscriptions;
  private float textSizeTitle;
  private float textSizeSubtitle;
  private final float textSizeSubTitleFactor = 14f / 18f;
  private HashSet<TextView> titles = new HashSet<>();
  private HashSet<TextView> subtitles = new HashSet<>();

  public PageItemsAdapter(Context context, List<PageItem> pageItems, @NonNull Preferences preferences) {
    super(context, 0, pageItems);

    this.preferences = preferences;
    this.textSizeTitle = preferences.textSize().get();
    this.textSizeSubtitle = this.textSizeTitle * textSizeSubTitleFactor;

    this.subscriptions = new HashMap<>();
  }

  @Override @NonNull public View getView(int position, View convertView, @NonNull ViewGroup parent) {
    // Get the data item for this position
    PageItem pageItem = getItem(position);
    List<Subscription> subscriptions;

    // Check if an existing view is being reused, otherwise inflate the view
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_2, parent, false);
      this.subscriptions.put(convertView, new ArrayList<>());
    }

    // Lookup views for data population and touch listening
    TextView textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
    TextView textViewSubtitle = (TextView) convertView.findViewById(R.id.textViewSubtitle);
    HorizontalScrollView scrollView = (HorizontalScrollView) convertView.findViewById(R.id.scrollView);
    scrollView.scrollTo(0, 0);

    // Put TextViews in HashSet so we can update the appearance later
    titles.add(textViewTitle);
    subtitles.add(textViewSubtitle);

    // Set initial appearance
    textViewTitle.setTextSize(textSizeTitle);
    textViewSubtitle.setTextSize(textSizeSubtitle);

    // Unsubscribe in case this view is reused
    subscriptions = this.subscriptions.get(convertView);
    for (Subscription each : subscriptions) {
      if (!each.isUnsubscribed()) {
        each.unsubscribe();
      }
    }
    subscriptions.clear();

    // Subscribe to page items observables
    if (pageItem != null) {
      textViewTitle.setText(pageItem.getTitle());
      textViewSubtitle.setVisibility(pageItem.getSubtitle() == null ? View.GONE : View.VISIBLE);
      textViewSubtitle.setText(pageItem.getSubtitle());
      convertView.setContentDescription(pageItem.getDescription());

      subscriptions.add(pageItem.title()
          .skip(1)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(textViewTitle::setText));
      subscriptions.add(pageItem.subtitle()
          .skip(1)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(text -> {
            textViewSubtitle.setVisibility(text == null ? View.GONE : View.VISIBLE);
            textViewSubtitle.setText(text);
          }));
      subscriptions.add(pageItem.description()
          .skip(1)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(convertView::setContentDescription));
    }

    // Attach touch listeners to perform a list item click and visual indicator on focus, tap
    // If you want to puke, see: https://code.google.com/p/android/issues/detail?id=3414
    final TouchListener touchListener = new TouchListener(parent, position, scrollView);
    textViewTitle.setOnTouchListener(touchListener);
    textViewSubtitle.setOnTouchListener(touchListener);
    scrollView.setOnTouchListener(touchListener);

    // Return the completed view to render on screen
    return convertView;
  }

  @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    this.textSizeTitle = preferences.textSize().get();
    this.textSizeSubtitle = this.textSizeTitle * textSizeSubTitleFactor;
    for (TextView textView : titles) {
      textView.setTextSize(textSizeTitle);
    }
    for (TextView textView : subtitles) {
      textView.setTextSize(textSizeSubtitle);
    }
  }

  private class TouchListener extends OnTouchClickListener {

    private final ViewGroup parent;
    private final int position;
    private View itemView;

    TouchListener(ViewGroup parent, int position, View itemView) {
      this.parent = parent;
      this.position = position;
      this.itemView = itemView;
    }

    @Override public boolean onTouch(View view, MotionEvent motionEvent) {
      final int action = motionEvent.getActionMasked();
      if (action == MotionEvent.ACTION_DOWN) {
        itemView.setBackgroundColor(highlightColor);
      } else if (action == MotionEvent.ACTION_UP) {
        itemView.setBackgroundResource(0);
      } else if (action == MotionEvent.ACTION_CANCEL) {
        itemView.setBackgroundResource(0);
      }

      return super.onTouch(view, motionEvent);
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
