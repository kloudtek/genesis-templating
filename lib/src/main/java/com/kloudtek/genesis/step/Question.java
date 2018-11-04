package com.kloudtek.genesis.step;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Question {
    @XmlAttribute(required = true)
    @JsonProperty(required = true)
    protected String id;
    @XmlAttribute(required = true)
    @JsonProperty(required = true)
    protected String name;
    @XmlAttribute()
    @JsonProperty()
    protected String description;
    @XmlAttribute(name = "default")
    @JsonProperty("default")
    protected String defaultValue;
    @XmlAttribute
    @JsonProperty
    protected boolean blankAllowed;
    @XmlElement(name = "option")
    @JsonProperty
    protected List<InputOption> options;
    @XmlAttribute
    @JsonProperty
    protected boolean advanced;
    @XmlAttribute
    @JsonProperty
    protected Type type = Type.STRING;

    public Question() {
    }

    public Question(String id, String name, String description, String defaultValue, boolean blankAllowed, List<InputOption> options, boolean advanced, Type type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.blankAllowed = blankAllowed;
        this.options = options;
        this.advanced = advanced;
        this.type = type;
    }

    public Question(Question question) {
        id = question.id;
        name = question.name;
        description = question.description;
        defaultValue = question.defaultValue;
        blankAllowed = question.blankAllowed;
        if( question.getOptions() != null ) {
            options = new ArrayList<>(question.getOptions());
        }
        advanced = question.advanced;
        type = question.type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isBlankAllowed() {
        return blankAllowed;
    }

    public void setBlankAllowed(boolean blankAllowed) {
        this.blankAllowed = blankAllowed;
    }

    public List<InputOption> getOptions() {
        return options;
    }

    public void setOptions(List<InputOption> options) {
        this.options = options;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @XmlType(name = "QuestionType")
    @XmlEnum
    public enum Type {
        STRING, NUMBER, BOOLEAN
    }
}
