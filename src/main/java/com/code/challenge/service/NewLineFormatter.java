package com.code.challenge.service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete class to format a list of string by joining them with a line separator.
 */
public class NewLineFormatter implements ListFormatter {
    @Override
    public String format(List<String> list) {
        return list.stream().collect(Collectors.joining(System.lineSeparator()));
    }
}
