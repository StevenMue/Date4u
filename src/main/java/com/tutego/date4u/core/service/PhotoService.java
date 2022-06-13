package com.tutego.date4u.core.service;

import com.tutego.date4u.core.FileSystem;
import com.tutego.date4u.repository.enities.Photo;
import com.tutego.date4u.core.thumbnail.Thumbnail;
import com.tutego.date4u.core.thumbnail.ThumbnailRendering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.UUID;

@Validated
public class PhotoService {
    private FileSystem fs;

    @ThumbnailRendering( ThumbnailRendering.RenderingQuality.FAST )
    Thumbnail thumbnail;
    public Optional<byte[]> download(String name ) {
        try { return Optional.of( fs.load( name + ".jpg" ) ); }
        catch ( UncheckedIOException e ) { return Optional.empty(); }
    }

    @Autowired
    public void setFs(FileSystem fs){
        this.fs = fs;
    }


    //TODO Check this function if its working and if its needed
    public String upload( byte[] imageBytes ) {
        String imageName = UUID.randomUUID().toString();
        // First: store original image
        fs.store( imageName + ".jpg", imageBytes );
        // Second: store thumbnail
        byte[] thumbnailBytes = thumbnail.thumbnail( imageBytes );
        fs.store( imageName + "-thumb.jpg", thumbnailBytes );
        return imageName;
    }

    /*TODO Download-Method
    needed when the client what to download a picture form the Date-Website or when the HTML need it
    */
    public Optional<byte[]> download( @Valid Photo photo ) {
        return download( photo.name );
    }
}
