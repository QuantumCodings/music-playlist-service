package com.amazon.ata.music.playlist.service.dynamodb;

import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import javax.inject.Inject;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazon.ata.music.playlist.service.exceptions.AlbumTrackNotFoundException;
/**
 * Accesses data for an album using {@link AlbumTrack} to represent the model in DynamoDB.
 */
public class AlbumTrackDao {
    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates an AlbumTrackDao object.
     *
     * @param dynamoDbMapper the {@link DynamoDBMapper} used to interact with the album_track table
     */
    @Inject
    public AlbumTrackDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }
    public AlbumTrack getAlbumTrack(String asin, Integer trackNumber) {

        AlbumTrack resultTrack = dynamoDbMapper.load(AlbumTrack.class, asin, trackNumber);

        if (resultTrack == null) {
            throw new AlbumTrackNotFoundException("Requested album track not found");
        }
        return resultTrack;
    }
}

