package com.object.ch01.after.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TicketTest {

    @Test
    void 금액으로_티켓_구매시_금액이_부족하여_구매에_실패한다() {

        final long balance = 9000L;

        assertThatThrownBy(() -> Ticket.create(balance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("티켓 구매 금액이 부족합니다.");
    }
}