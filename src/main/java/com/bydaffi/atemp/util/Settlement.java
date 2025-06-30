package com.bydaffi.atemp.util;

public record Settlement(int x, int y, int z, boolean campCreated) {
    
    // Constructor for backward compatibility
    public Settlement(int x, int y, int z) {
        this(x, y, z, false);
    }
}