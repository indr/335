/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class BaseListActivity<ViewModelType extends ActivityViewModel> extends BaseActivity<ViewModelType> {

    private ListView listView;

    protected ListAdapter getListAdapter() {
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter) adapter).getWrappedAdapter();
        } else {
            return adapter;
        }
    }

    protected void setListAdapter(ListAdapter listAdapter) {
        this.getListView().setAdapter(listAdapter);
    }

    protected ListView getListView() {
        if (this.listView == null) {
            this.initListView();
        }
        return listView;
    }

    protected abstract int getListViewId();

    private void initListView() {
        this.listView = (ListView) findViewById(this.getListViewId());
        if (this.listView == null) {
            throw new RuntimeException("ListView cannot be null. Please set a valid ListViewId");
        }

        this.listView.setOnItemClickListener(new ListOnItemClickListener());
    }

    protected void onListItemClick(ListView listView, View view, int position, long id) {
    }

    private final class ListOnItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            BaseListActivity.this.onListItemClick((ListView) adapterView, view, position, id);
        }
    }
}
