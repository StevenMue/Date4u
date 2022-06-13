package com.tutego.date4u.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileSystem {
    private final Path root =
            Paths.get( System.getProperty("user.home") ).resolve( "fs" );

    public FileSystem() {
        try { if ( ! Files.isDirectory(root) ) Files.createDirectory(root); }
        catch ( IOException e ) { throw new UncheckedIOException( e ); }
    }

    /*
    TODO Upload Photo
    When a user try to upload a picture try this or i dont know if its necessary
     */
    public long getFreeDiskSpace() {
        return root.toFile().getFreeSpace();
    }


    /*
    TODO Other invalid symbol handling
    manybe not throw a exception and handle differently
     */
    public byte[] load( String filename ) {
        if (filename.contains("..")){
            throw new IllegalArgumentException("more than one '.' is not valid!");
        }
        try { return Files.readAllBytes( root.resolve( filename ) ); }
        catch ( IOException e ) { throw new UncheckedIOException( e ); }
    }

    public void store( String filename, byte[] bytes ) {
        if (filename.contains("..")){
            throw new IllegalArgumentException("more than one '.' is not valid!");
        }
        try { Files.write( root.resolve( filename ), bytes ); }
        catch ( IOException e ) { throw new UncheckedIOException( e ); }
    }
}
