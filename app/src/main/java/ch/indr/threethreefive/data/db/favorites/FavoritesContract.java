/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.favorites;

import android.provider.BaseColumns;

/**
 * https://developer.android.com/training/basics/data-storage/databases.html
 */
public class FavoritesContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FavoritesContract() {
    }

    /* Inner class that defines the table contents */
    public static class FavoritesEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_PAGE_URI = "page_uri";
        public static final String COLUMN_NAME_MEDIA_URI = "media_uri";
    }
}

