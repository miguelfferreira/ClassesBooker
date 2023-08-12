package com.glofox.classesbooker.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Utils {

    public static Date getDateFromString(String dateString) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        dateFormatter.setLenient(false);
        Date date = null;
        try {
            date = dateFormatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getStringFromDate(Date date) {
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public static Date addNumberOfDaysToDate(Date date, int numberOfDays) {
        return Date.from(date.toInstant().plus(numberOfDays, ChronoUnit.DAYS));
    }

    public static int getNumberOfDaysBetweenTwoDates(Date pStartDate, Date pEndDate) {
        LocalDate startDate = convertToLocalDateViaInstant(pStartDate);
        LocalDate endDate = convertToLocalDateViaInstant(pEndDate);

        Period period = Period.between(startDate, endDate);
        return Math.abs(period.getDays());
    }

    public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static boolean isDateFormatInvalid(String startDateStr, String endDateStr) {
        return (StringUtils.isNotBlank(startDateStr) && Utils.getDateFromString(startDateStr) == null) || (StringUtils.isNotBlank(endDateStr) && Utils.getDateFromString(endDateStr) == null);
    }
}
