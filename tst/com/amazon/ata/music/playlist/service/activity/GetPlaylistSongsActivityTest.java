package com.amazon.ata.music.playlist.service.activity;
import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazon.ata.music.playlist.service.models.SongModel;
import com.amazon.ata.music.playlist.service.models.SongOrder;
import com.amazon.ata.music.playlist.service.models.requests.GetPlaylistSongsRequest;
import com.amazon.ata.music.playlist.service.models.results.GetPlaylistSongsResult;
import com.amazon.ata.music.playlist.service.helpers.AlbumTrackTestHelper;
import com.amazon.ata.music.playlist.service.helpers.PlaylistTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.mockito.Mock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GetPlaylistSongsActivityTest {
    @Mock
    private PlaylistDao playlistDao;

    private GetPlaylistSongsActivity getPlaylistSongsActivity;

    private ModelConverter modelConverter;

    @BeforeEach
    public void setup() {
        initMocks(this);
        getPlaylistSongsActivity = new GetPlaylistSongsActivity(playlistDao);
        modelConverter = new ModelConverter();
    }

    @Test
    void handleRequest_playlistExistsWithSongs_returnsSongsInPlaylist() {
        // GIVEN
        Playlist playlist = PlaylistTestHelper.generatePlaylistWithNAlbumTracks(0);
        String playlistId = playlist.getId();
        GetPlaylistSongsRequest request = GetPlaylistSongsRequest.builder()
                .withId(playlistId)
                .build();
        when(playlistDao.getPlaylist(playlistId)).thenReturn(playlist);

        // WHEN
        GetPlaylistSongsResult result = getPlaylistSongsActivity.handleRequest(request, null);

        // THEN
        AlbumTrackTestHelper.assertAlbumTracksEqualSongModels(playlist.getSongList(), result.getSongList());
    }

    @Test
    void handleRequest_playlistExistsWithoutSongs_returnsEmptyList() {
        // GIVEN
        Playlist emptyPlaylist = PlaylistTestHelper.generatePlaylistWithNAlbumTracks(0);
        String playlistId = emptyPlaylist.getId();
        GetPlaylistSongsRequest request = GetPlaylistSongsRequest.builder()
                .withId(playlistId)
                .build();
        when(playlistDao.getPlaylist(playlistId)).thenReturn(emptyPlaylist);

        // WHEN
        GetPlaylistSongsResult result = getPlaylistSongsActivity.handleRequest(request, null);

        // THEN
        assertTrue(result.getSongList().isEmpty(),
                "Expected song list to be empty but was " + result.getSongList());
    }

    @Test
    void handleRequest_withDefaultSongOrder_returnsDefaultOrderedPlaylistSongs() {
        // GIVEN
        Playlist playlist = PlaylistTestHelper.generatePlaylistWithNAlbumTracks(0);
        String playlistId = playlist.getId();

        GetPlaylistSongsRequest request = GetPlaylistSongsRequest.builder()
                .withId(playlistId)
                .withOrder(SongOrder.DEFAULT)
                .build();
        when(playlistDao.getPlaylist(playlistId)).thenReturn(playlist);

        // WHEN
        GetPlaylistSongsResult result = getPlaylistSongsActivity.handleRequest(request, null);

        // THEN
        AlbumTrackTestHelper.assertAlbumTracksEqualSongModels(playlist.getSongList(), result.getSongList());
    }

    @Test
    void handleRequest_withReversedSongOrder_returnsReversedPlaylistSongs() {
        // GIVEN
        Playlist playlist = PlaylistTestHelper.generatePlaylistWithNAlbumTracks(0);
        String playlistId = playlist.getId();
        List<AlbumTrack> reversedAlbumTracks = new LinkedList<>(playlist.getSongList());
        Collections.reverse(reversedAlbumTracks);

        GetPlaylistSongsRequest request = GetPlaylistSongsRequest.builder()
                .withId(playlistId)
                .withOrder(SongOrder.REVERSED)
                .build();
        when(playlistDao.getPlaylist(playlistId)).thenReturn(playlist);

        // WHEN
        GetPlaylistSongsResult result = getPlaylistSongsActivity.handleRequest(request, null);

        // THEN
        AlbumTrackTestHelper.assertAlbumTracksEqualSongModels(reversedAlbumTracks, result.getSongList());
    }

    @Test
    void handleRequest_withShuffledSongOrder_returnsSongsInAnyOrder() {
        Playlist playlist = PlaylistTestHelper.generatePlaylistWithNAlbumTracks(0);
        String playlistId = playlist.getId();

        List<SongModel> songModels = new ArrayList<>();

        for (int i = 0; i < playlist.getSongList().size(); i++) {
            songModels.add(modelConverter.toSongModel(playlist.getSongList().get(i)));
        }

        GetPlaylistSongsRequest request = GetPlaylistSongsRequest.builder()
                .withId(playlistId)
                .withOrder(SongOrder.REVERSED)
                .build();
        when(playlistDao.getPlaylist(playlistId)).thenReturn(playlist);

        // WHEN
        GetPlaylistSongsResult result = getPlaylistSongsActivity.handleRequest(request, null);

        // THEN
        assertEquals(playlist.getSongList().size(),
                result.getSongList().size(),
                String.format("Expected album tracks (%s) and song models (%s) to be the same length",
                        playlist.getSongList(),
                        result.getSongList()));
        assertTrue(
                songModels.containsAll(result.getSongList()),
                String.format("album list (%s) and song models (%s) are the same length, but don't contain the same " +
                                "entries. Expected song models: %s. Returned song models: %s",
                        playlist.getSongList(),
                        result.getSongList(),
                        songModels,
                        result.getSongList()));
    }

    @Test
    public void handleRequest_noMatchingPlaylistId_throwsPlaylistNotFoundException() {
        // GIVEN
        String id = "missingID";
        GetPlaylistSongsRequest request = GetPlaylistSongsRequest.builder()
                .withId(id)
                .build();

        // WHEN
        when(playlistDao.getPlaylist(id)).thenThrow(new PlaylistNotFoundException());

        // WHEN + THEN
        assertThrows(PlaylistNotFoundException.class, () -> getPlaylistSongsActivity.handleRequest(request, null));
    }

}
