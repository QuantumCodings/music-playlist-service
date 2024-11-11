package com.amazon.ata.music.playlist.service.converters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;

public class ModelConverter {
    /**
     * Converts a provided {@link Playlist} into a {@link PlaylistModel} representation.
     * @param playlist the playlist to convert
     * @return the converted playlist
     */
    public PlaylistModel toPlaylistModel(Playlist playlist) {
        List<String> tags;
        if (!playlist.getTags().isEmpty()) {
            tags = new ArrayList<>(playlist.getTags());
        } else {
            tags = null;
        }
        return PlaylistModel.builder()
                .withId(playlist.getId())
                .withName(playlist.getName())
                .withCustomerId(playlist.getCustomerId())
                .withSongCount(playlist.getSongCount())
                .withTags(tags)
                .build();
    }
}
