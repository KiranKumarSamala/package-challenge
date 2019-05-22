package com.code.challenge.service;

import java.util.List;

/**
 * Define Formatter Protocol to be implemented by Concrete classes.
 */
public interface ListFormatter {
    String format(List<String> list);
}
