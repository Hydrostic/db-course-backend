package org.LunaTelecom.model;

import lombok.Data;

@Data
public class NumberPool {
    private Long id;
    private String name;
    private String start;
    private String end;
    private Long free;
    private Long parent;
}
