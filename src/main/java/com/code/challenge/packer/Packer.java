package com.code.challenge.packer;

import com.code.challenge.service.*;
import com.code.challenge.exception.APIException;
import com.code.challenge.model.Problem;
import com.mobiquityinc.service.*;

import java.util.List;
import java.util.stream.Collectors;


public class Packer {


    public static String pack(String filePath) throws APIException {

        Packing service = new CumulativePacking();
        ListFormatter formatter = new NewLineFormatter();

        List<Problem> problems = Parser.getInstance().parse(filePath);

        List<String> solutions = problems.stream()
                .map(service::getOptimalItemIdsInString)
                .collect(Collectors.toList());

        return formatter.format(solutions);
    }

}
