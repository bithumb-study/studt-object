package com.object.ch06.theater.step02;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("step02 - 묻지 말고 시켜라")
class TheaterTest {

    @Test
    void 초대장인_경우_티켓으로_교환하고_총잔여금액은_변동없고_티켓은_감소한다() {

        final Bag bag = new Bag(new Invitation(LocalDateTime.now()), 0L);
        final Audience audience = new Audience(bag);

        final TicketOffice ticketOffice = new TicketOffice(10000L, new Ticket(10000L));
        final TicketSeller ticketSeller = new TicketSeller(ticketOffice);
        final Theater theater = new Theater(ticketSeller);

        assertAll(
                () -> assertThat(ticketOffice.remainingAmount()).isEqualTo(10000L),
                () -> assertThat(ticketOffice.remainingTickets()).isEqualTo(1)
        );

        theater.enter(audience);

        assertAll(
                () -> assertThat(ticketOffice.remainingAmount()).isEqualTo(10000L),
                () -> assertThat(ticketOffice.remainingTickets()).isZero()
        );
    }

    @Test
    void 초대장이_아닌경우_금액으로_티켓_구매하고_총잔여금액은_증가하고_티켓은_감소한다() {

        final Bag bag = new Bag(20000L);
        final Audience audience = new Audience(bag);

        final TicketOffice ticketOffice = new TicketOffice(10000L, new Ticket(10000L));
        final TicketSeller ticketSeller = new TicketSeller(ticketOffice);
        final Theater theater = new Theater(ticketSeller);

        assertAll(
                () -> assertThat(ticketOffice.remainingAmount()).isEqualTo(10000L),
                () -> assertThat(ticketOffice.remainingTickets()).isEqualTo(1)
        );

        theater.enter(audience);

        assertAll(
                () -> assertThat(ticketOffice.remainingAmount()).isEqualTo(20000L),
                () -> assertThat(ticketOffice.remainingTickets()).isZero()
        );
    }
}