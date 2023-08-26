package com.object.ch01.after.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TicketSellerTest {

    @Test
    void 초대장인_경우_티켓으로_교환하고_총잔여금액은_변동없고_티켓은_감소한다() {

        final Ticket emptyOfTicket = Ticket.empty();
        final Invitation invitation = new Invitation(LocalDateTime.now());
        final Bag bag = new Bag(0L, emptyOfTicket, invitation);
        final Audience audience = new Audience(bag);

        final TicketOffice ticketOffice = new TicketOffice(10000L, 10);
        final TicketSeller ticketSeller = new TicketSeller(ticketOffice);
        ticketSeller.sellTo(audience);

        assertAll(
                () -> assertThat(ticketOffice.remainingOfTicket()).isEqualTo(9),
                () -> assertThat(ticketOffice.getAmount()).isEqualTo(10000L)
        );
    }

    @Test
    void 초대장이_아닌경우_금액으로_티켓_구매하고_총잔여금액은_증가하고_티켓은_감소한다() {

        final long balance = 20000L;
        final Bag bag = new Bag(balance, Ticket.empty(), Invitation.empty());
        final Audience audience = new Audience(bag);

        final TicketOffice ticketOffice = new TicketOffice(10000L, 10);
        final TicketSeller ticketSeller = new TicketSeller(ticketOffice);
        ticketSeller.sellTo(audience);

        assertAll(
                () -> assertThat(ticketOffice.remainingOfTicket()).isEqualTo(9),
                () -> assertThat(ticketOffice.getAmount()).isEqualTo(20000L)
        );
    }

}