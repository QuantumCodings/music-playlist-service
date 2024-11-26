package com.amazon.ata.music.playlist.service.dynamodb;

import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Accesses data for a playlist using {@link Playlist} to represent the model in DynamoDB.
 */
public class PlaylistDao {
    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates a PlaylistDao object.
     *
     * @param dynamoDbMapper the {@link DynamoDBMapper} used to interact with the playlists table
     */
    @Inject
    public PlaylistDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Returns the {@link Playlist} corresponding to the specified id.
     *
     * @param id the Playlist ID
     * @return the stored Playlist, or null if none was found.
     */
    public Playlist getPlaylist(String id) {
        Playlist playlist = this.dynamoDbMapper.load(Playlist.class, id);

        if (playlist == null) {
            throw new PlaylistNotFoundException("Could not find playlist with id " + id);
        }

        return playlist;
    }

    public Playlist savePlaylist(PlaylistModel playlistModel) {


        Playlist updatedPlaylist;

        Set<String> tagsSet = new HashSet<>(playlistModel.getTags());

        if (getPlaylist(playlistModel.getId()) == null) {
            updatedPlaylist = new Playlist(playlistModel.getId(), playlistModel.getName(), playlistModel.getCustomerId(), playlistModel.getSongCount(), tagsSet, new ArrayList<>());
        } else {
            Playlist existingPlaylist = getPlaylist(playlistModel.getId());
            updatedPlaylist = new Playlist(playlistModel.getId(), playlistModel.getName(), playlistModel.getCustomerId(), playlistModel.getSongCount(), tagsSet, existingPlaylist.getSongList());
        }



        dynamoDbMapper.save(updatedPlaylist);

        return updatedPlaylist;
    }
}
