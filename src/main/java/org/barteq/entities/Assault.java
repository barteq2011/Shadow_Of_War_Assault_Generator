package org.barteq.entities;

// Class used to represent and create Assault
public class Assault {
    private final String fortess;
    private final int timeRemaining; /* In seconds */

    private Assault(String fortess, int timeRemaining) {
        this.fortess = fortess;
        this.timeRemaining = timeRemaining;
    }
    public static Assault of(String fortess, int timeRemaining) {
        return new Assault(fortess, timeRemaining);
    }

    public String getFortess() {
        return fortess;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }
}
