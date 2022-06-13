package com.tutego.date4u;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.List;

@XmlRootElement
public class Trending {
    public List<Long> unicorns = Arrays.asList(37854L, 8342934L, 673647L);
}