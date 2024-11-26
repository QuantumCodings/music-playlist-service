package com.amazon.ata.music.playlist.service.dependency;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.music.playlist.service.dynamodb.AlbumTrackDao;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DaoModule {

    @Singleton
    @Provides
    public PlaylistDao providePlaylistDao(DynamoDBMapper dynamoDBMapper) {
        return new PlaylistDao(dynamoDBMapper);
    }

    @Singleton
    @Provides
    public AlbumTrackDao provideAlbumTrackDao(DynamoDBMapper dynamoDBMapper) {
        return new AlbumTrackDao(dynamoDBMapper);
    }

    @Singleton
    @Provides
    public DynamoDBMapper provideDynamoDBMapper() {
        return new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient());
    }
}
