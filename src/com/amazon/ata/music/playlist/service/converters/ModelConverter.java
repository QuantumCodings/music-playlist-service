package com.amazon.ata.music.playlist.service.converters;
import java.util.ArrayList;
import java.util.List;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.models.SongModel;
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
                .withCustomerId(playlist.getCustomerId())
                .withId(playlist.getId())
                .withName(playlist.getName())
                .withSongCount(playlist.getSongCount())
                .withTags(tags)
                .build();
    }
    /**
     * Converts a provided {@link AlbumTrack} into a {@link SongModel} representation.
     * @param albumTrack the albumTrack to convert
     * @return the converted playlist
     */
    public SongModel toSongModel(AlbumTrack albumTrack) {

        return SongModel.builder()
                .withAlbum(albumTrack.getAlbumName())
                .withAsin(albumTrack.getAsin())
                .withTrackNumber(albumTrack.getTrackNumber())
                .withTitle(albumTrack.getSongTitle())
                .build();
    }
}
