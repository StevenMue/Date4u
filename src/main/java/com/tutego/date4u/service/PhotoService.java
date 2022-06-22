package com.tutego.date4u.service;

import com.tutego.date4u.core.FileSystem;
import com.tutego.date4u.core.enities.Photo;
import com.tutego.date4u.core.repository.PhotoRepository;
import com.tutego.date4u.core.repository.ProfileRepository;
import com.tutego.date4u.core.thumbnail.Thumbnail;
import com.tutego.date4u.core.thumbnail.ThumbnailRendering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Validated
public class PhotoService {
    private FileSystem fs;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PhotoRepository photoRepository;
    @ThumbnailRendering(ThumbnailRendering.RenderingQuality.FAST)
    Thumbnail thumbnail;

    public Optional<byte[]> download(String name) {
        try {
            return Optional.of(fs.load(name + ".jpg"));
        } catch (UncheckedIOException e) {
            return Optional.empty();
        }
    }

    @Autowired
    public void setFs(FileSystem fs) {
        this.fs = fs;
    }


    //TODO Check this function if its working and if its needed
    public String upload(byte[] imageBytes) {
        String imageName = UUID.randomUUID().toString();
        // First: store original image
        fs.store(imageName + ".jpg", imageBytes);
        // Second: store thumbnail
        byte[] thumbnailBytes = thumbnail.thumbnail(imageBytes);
        fs.store(imageName + "-thumb.jpg", thumbnailBytes);
        return imageName;
    }

    /*TODO Download-Method
    needed when the client what to download a picture form the Date-Website or when the HTML need it
    */
    public Optional<byte[]> download(@Valid Photo photo) {
        return download(photo.name);
    }

    public Optional<byte[]> getFilePhoto(@Valid Photo photo) {
        return download("./unicorns/img/" + photo.name);
    }

    public Optional<byte[]> getFilePhoto(String photo) {
        return download("./unicorns/img/" + photo);
    }

    public Optional<byte[]> getProfilePictureByNickname(String nickname){
        return photoRepository.findProfilePhotoByNickname(nickname).flatMap(this::getFilePhoto);
    }
    public Optional<byte[]> getPictureByNicknameAndPhotoName(String nickname, String photoName){
        return photoRepository.findPhotoByNicknameAndName(nickname, photoName).flatMap(this::getFilePhoto);
    }

    public Optional<byte[]> getLikeImage(boolean isLiked) {
        if (isLiked) {
            Random r = new Random();
            int index = r.nextInt(1, 5);
            if (index < 10) {
                return download("./webicon/liked_0" + index);
            } else {
                return download("./webicon/liked_" + index);
            }

        } else {
            return download("./webicon/liked_not");
        }
    }
}
