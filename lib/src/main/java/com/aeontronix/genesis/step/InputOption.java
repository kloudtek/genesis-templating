package com.aeontronix.genesis.step;

import javax.xml.bind.annotation.XmlAttribute;

public class InputOption {
    private String id;
    private String text;

    @XmlAttribute(required = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(required = true)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
