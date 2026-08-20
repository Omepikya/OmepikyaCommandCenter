package com.omepikya.commandcenter.memory;
public class MemoryPolicy { public enum Importance { TEMPORARY, USEFUL, IMPORTANT } public boolean shouldPersist(Importance i){return i==Importance.USEFUL||i==Importance.IMPORTANT;} }
