package com.object.ch01.after.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AudienceTest {

    @Test
    void 초대장_존재하여_티켓_교환한다() {

        final Ticket emptyOfTicket = Ticket.empty();
        final Invitation invitation = new Invitation(LocalDateTime.now());
        final Bag bag = new Bag(0L, emptyOfTicket, invitation);

        final Audience audience = new Audience(bag);
        final Ticket ticket = Ticket.createOfInvitation();
        final Long buy = audience.buy(ticket);

        assertThat(buy).isZero();
    }

    @Test
    void 초대장_존재하지_않아서_금액으로_티켓_구매한다() {

        final long balance = 20000L;
        final Bag bag = new Bag(balance, Ticket.empty(), Invitation.empty());

        final Audience audience = new Audience(bag);
        final Long hold = audience.buy(Ticket.create(balance));

        assertThat(hold).isEqualTo(10000L);
    }
}