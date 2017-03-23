/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.playlist;

import android.provider.BaseColumns;

public class PlaylistsContract {
    private PlaylistsContract() {
    }

    public static class PlaylistItem implements BaseColumns {
        public static final String TABLE_NAME = "playlist_item";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
        public static final String COLUMN_NAME_PLAYLIST_ID = "playlist_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_PAGE_URI = "page_uri";
        public static final String COLUMN_NAME_MEDIA_URI = "media_uri";
    }
}
