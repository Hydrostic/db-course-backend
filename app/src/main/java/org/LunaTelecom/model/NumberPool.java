package org.LunaTelecom.model;

import lombok.Data;

@Data
public class NumberPool {
    private long id;
    private String name;
    private String start;
    private String end;
    private long free;
    private long parent;
}
