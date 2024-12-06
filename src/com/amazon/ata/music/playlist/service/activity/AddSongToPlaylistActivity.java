package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazon.ata.music.playlist.service.models.requests.AddSongToPlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.AddSongToPlaylistResult;
import com.amazon.ata.music.playlist.service.models.SongModel;
import com.amazon.ata.music.playlist.service.dynamodb.AlbumTrackDao;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
import javax.inject.Inject;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
/**
 * Implementation of the AddSongToPlaylistActivity for the MusicPlaylistService's AddSongToPlaylist API.
 *
 * This API allows the customer to add a song to their existing playlist.
 */
public class AddSongToPlaylistActivity implements RequestHandler<AddSongToPlaylistRequest, AddSongToPlaylistResult> {
    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;
    private final AlbumTrackDao albumTrackDao;
    private ModelConverter modelConverter = new ModelConverter();
    /**
     * Instantiates a new AddSongToPlaylistActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlist table.
     * @param albumTrackDao AlbumTrackDao to access the album_track table.
     */
    @Inject
    public AddSongToPlaylistActivity(PlaylistDao playlistDao, AlbumTrackDao albumTrackDao) {
        this.playlistDao = playlistDao;
        this.albumTrackDao = albumTrackDao;
    }

    /**
     * This method handles the incoming request by adding an additional song
     * to a playlist and persisting the updated playlist.
     * <p>
     * It then returns the updated song list of the playlist.
     * <p>
     * If the playlist does not exist, this should throw a PlaylistNotFoundException.
     * <p>
     * If the album track does not exist, this should throw an AlbumTrackNotFoundException.
     *
     * @param addSongToPlaylistRequest request object containing the playlist ID and an asin and track number
     *                                 to retrieve the song data
     * @return addSongToPlaylistResult result object containing the playlist's updated list of
     *                                 API defined {@link SongModel}s
     */
    @Override
    public AddSongToPlaylistResult handleRequest(final AddSongToPlaylistRequest addSongToPlaylistRequest, Context context) {
        log.info("Received AddSongToPlaylistRequest {}", addSongToPlaylistRequest);

        Playlist targetPlaylist;
        try {
            targetPlaylist = playlistDao.getPlaylist(addSongToPlaylistRequest.getId());
        } catch (PlaylistNotFoundException e) {
            throw new PlaylistNotFoundException("Playlist with id " + addSongToPlaylistRequest.getId() + " not found.", e);
        }

        AlbumTrack targetAlbumTrack = albumTrackDao.getAlbumTrack(addSongToPlaylistRequest.getAsin(), addSongToPlaylistRequest.getTrackNumber());

        List<AlbumTrack> targetSongList = targetPlaylist.getSongList();
        LinkedList<AlbumTrack> linkedSongList = (LinkedList<AlbumTrack>) targetSongList;


        if (addSongToPlaylistRequest.isQueueNext()) {
            linkedSongList.addFirst(targetAlbumTrack);
        } else {
            targetSongList.add(targetAlbumTrack);
        }

        playlistDao.savePlaylist(modelConverter.toPlaylistModel(targetPlaylist));

        List<SongModel> songModelList = new ArrayList<>();
        for (AlbumTrack track : targetSongList) {
            songModelList.add(modelConverter.toSongModel(track));
        }

        return AddSongToPlaylistResult.builder()
                .withSongList(songModelList)
                .build();
    }
}
