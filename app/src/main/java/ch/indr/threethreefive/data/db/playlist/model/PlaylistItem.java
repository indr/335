/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.playlist.model;

import java.util.UUID;

import ch.indr.threethreefive.libs.MediaItem;

public class PlaylistItem {
    private final String uuid;
    private final String name;
    private final String contentUrl;
    private final String mediaUrl;

    @Deprecated
    public PlaylistItem(MediaItem mediaItem) {
        this.uuid = UUID.randomUUID().toString();
        this.name = mediaItem.getName();
        this.contentUrl = mediaItem.getPageUri();
        this.mediaUrl = mediaItem.getMediaUri();
    }

    public PlaylistItem(long id, String title, String pageUri, String mediaUri) {
        uuid = String.valueOf(id);
        name = title;
        contentUrl = pageUri;
        mediaUrl = mediaUri;
    }

    public String getId() {
        return null;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getPageUrl() {
        return "/playlist/" + uuid;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }
}
