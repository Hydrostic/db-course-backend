package org.LunaTelecom.dto.common;

public class PagerResponse<T> {
    public static class Pager {
        public int totalPages;
        public int size;
    }
    public Pager pager;
    public T data;
    public PagerResponse(T d, int totalPages, int size) {
        this.data = d;
        this.pager = new Pager();
        this.pager.totalPages = totalPages;
        this.pager.size = size;
    }
}
