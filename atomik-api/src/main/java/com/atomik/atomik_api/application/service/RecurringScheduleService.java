package com.atomik.atomik_api.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.domain.model.RecurringFrequency;

@Service
public class RecurringScheduleService {

    public LocalDateTime nextDueDate(LocalDateTime currentDueDate, RecurringFrequency frequency) {
        return switch (frequency) {
            case DAILY -> currentDueDate.plusDays(1);
            case WEEKLY -> currentDueDate.plusWeeks(1);
            case MONTHLY -> currentDueDate.plusMonths(1);
            case YEARLY -> currentDueDate.plusYears(1);
        };
    }
}
