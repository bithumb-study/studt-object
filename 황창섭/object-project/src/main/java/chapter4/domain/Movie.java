package chapter4.domain;

import chapter4.domain.enumeration.MovieType;

import java.time.Duration;
import java.util.List;

public class Movie {
    private String title;

    private Duration runningTime;

    @Getter
    @Setter
    private Money fee;

    @Getter
    @Setter
    private List<DiscountCondition> discountConditionList;

    @Getter
    @Setter
    private MovieType movieType;

    @Getter
    @Setter
    private Money discountAmount;

    @Getter
    @Setter
    private double discountPercent;
}
