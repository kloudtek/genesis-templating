package com.kloudtek.genesis;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType
public enum TemplateEngineType {
    @XmlEnumValue("none") NONE,
    @XmlEnumValue("simple") SIMPLE,
    @XmlEnumValue("freemarker") FREEMARKER
}
