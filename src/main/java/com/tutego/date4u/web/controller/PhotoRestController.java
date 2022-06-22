package com.tutego.date4u.web.controller;

import com.tutego.date4u.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@RestController
public class PhotoRestController {

    @Autowired
    PhotoService photoService;

    @GetMapping( path = "/api/photo/{name}",
            produces = MediaType.IMAGE_JPEG_VALUE )
    public byte[] specificPhoto(@PathVariable String name) throws IOException {
        Optional<byte[]> picture = photoService.download("./unicorns/img/"+name);
        return picture.get();
    }

    @GetMapping( path     = "/api/photo/random",
            produces = MediaType.IMAGE_JPEG_VALUE )
    public byte[] randomPhoto() throws IOException {
        Resource resource = new UrlResource(
                "https://getwallpapers.com/wallpaper/full/1/1/7/264012.jpg"
        );
        InputStream inputStream = resource.getInputStream();
        return StreamUtils.copyToByteArray( inputStream );
    }
}
