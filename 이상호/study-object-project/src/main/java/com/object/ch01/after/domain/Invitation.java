package com.object.ch01.after.domain;

import java.time.LocalDateTime;

public class Invitation {

    private LocalDateTime when;

    public Invitation(final LocalDateTime when) {
        this.when = when;
    }

    public static Invitation empty() {

        return null;
    }
}
