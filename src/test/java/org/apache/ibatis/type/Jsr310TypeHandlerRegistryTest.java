
package org.apache.ibatis.type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.chrono.JapaneseDate;

import static org.assertj.core.api.Assertions.assertThat;


class Jsr310TypeHandlerRegistryTest {

  private TypeHandlerRegistry typeHandlerRegistry;

  @BeforeEach
  void setup() {
    typeHandlerRegistry = new TypeHandlerRegistry();
  }

  @Test
  void shouldRegisterJsr310TypeHandlers() {
    assertThat(typeHandlerRegistry.getTypeHandler(Instant.class))
        .isInstanceOf(InstantTypeHandler.class);
    assertThat(typeHandlerRegistry.getTypeHandler(LocalDateTime.class))
        .isInstanceOf(LocalDateTimeTypeHandler.class);
    assertThat(typeHandlerRegistry.getTypeHandler(LocalDate.class))
        .isInstanceOf(LocalDateTypeHandler.class);
    assertThat(typeHandlerRegistry.getTypeHandler(LocalTime.class))
        .isInstanceOf(LocalTimeTypeHandler.class);
    assertThat(typeHandlerRegistry.getTypeHandler(OffsetDateTime.class))
        .isInstanceOf(OffsetDateTimeTypeHandler.class);
    assertThat(typeHandlerRegistry.getTypeHandler(OffsetTime.class))
        .isInstanceOf(OffsetTimeTypeHandler.class);
    assertThat(typeHandlerRegistry.getTypeHandler(ZonedDateTime.class))
        .isInstanceOf(ZonedDateTimeTypeHandler.class);
    assertThat(typeHandlerRegistry.getTypeHandler(Month.class))
        .isInstanceOf(MonthTypeHandler.class);
    assertThat(typeHandlerRegistry.getTypeHandler(Year.class))
        .isInstanceOf(YearTypeHandler.class);
    assertThat(typeHandlerRegistry.getTypeHandler(YearMonth.class))
        .isInstanceOf(YearMonthTypeHandler.class);
    assertThat(typeHandlerRegistry.getTypeHandler(JapaneseDate.class))
        .isInstanceOf(JapaneseDateTypeHandler.class);
  }
}
