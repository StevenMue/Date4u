package com.tutego.date4u.core.thumbnail;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.FIELD, ElementType.METHOD, ElementType.TYPE,
        ElementType.PARAMETER, ElementType.ANNOTATION_TYPE } )
@Retention( RetentionPolicy.RUNTIME )
@Qualifier
public @interface ThumbnailRendering {
    enum RenderingQuality { FAST, QUALITY }
    RenderingQuality value();
}
