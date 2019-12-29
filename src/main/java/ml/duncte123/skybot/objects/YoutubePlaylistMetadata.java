/*
 * Skybot, a multipurpose discord bot
 *      Copyright (C) 2017 - 2019  Duncan "duncte123" Sterken & Ramid "ramidzkh" Khan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ml.duncte123.skybot.objects;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import javax.annotation.Nullable;
import java.util.List;

public class YoutubePlaylistMetadata {

    private final String id;
    private final String title;
    private final String nextPageKey;
    private final List<AudioTrackInfo> tracks;

    public YoutubePlaylistMetadata(String id, String title, @Nullable String nextPageKey, List<AudioTrackInfo> tracks) {
        this.id = id;
        this.title = title;
        this.nextPageKey = nextPageKey;
        this.tracks = tracks;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Nullable
    public String getNextPageKey() {
        return nextPageKey;
    }

    public List<AudioTrackInfo> getTracks() {
        return tracks;
    }

    @Override
    public String toString() {
        return "YoutubePlaylistMetadata{" +
            "id='" + id + '\'' +
            ", title='" + title + '\'' +
            '}';
    }
}