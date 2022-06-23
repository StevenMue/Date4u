package com.tutego.date4u.service;

import com.tutego.date4u.core.FileSystem;
import com.tutego.date4u.core.enities.Photo;
import com.tutego.date4u.core.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.Random;

@Validated
public class PhotoService {
    private FileSystem fs;

    @Autowired
    private PhotoRepository photoRepository;

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
