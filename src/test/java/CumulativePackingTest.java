import com.code.challenge.exception.APIException;
import com.code.challenge.model.Problem;
import com.code.challenge.model.Triplet;
import com.code.challenge.service.CumulativePacking;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class CumulativePackingTest {

    @Test
    public void returnItemWithLessWeightWhenTwoItemOfSameCost(){
        Problem problem = new Problem(10);
        Triplet optimalTriplet = new Triplet(2, 9.99f, 100);
        problem.getTriplets().add(new Triplet(1,10.0f,100));
        problem.getTriplets().add(optimalTriplet);

        CumulativePacking serviceUnderTest = new CumulativePacking();
        List<Triplet> triplets = serviceUnderTest.getOptimalTriplets(problem);

        assertNotNull(triplets.size());
        assertEquals(optimalTriplet,triplets.get(0));
    }

    @Test
    public void returnItemWithHighCostWhenTwoItemOfSameWeight(){
        Problem problem = new Problem(10);
        Triplet optimalTriplet = new Triplet(2, 10.0f, 100);
        problem.getTriplets().add(new Triplet(1,10.0f,99));
        problem.getTriplets().add(optimalTriplet);

        CumulativePacking serviceUnderTest = new CumulativePacking();
        List<Triplet> triplets = serviceUnderTest.getOptimalTriplets(problem);

        assertNotNull(triplets.size());
        assertEquals(optimalTriplet,triplets.get(0));
    }

    @Test
    public void returnEmptyListWhenItemsNotFittingPackageCapacity(){
        Problem problem = new Problem(10);
        problem.getTriplets().add(new Triplet(1,88.0f,99));
        problem.getTriplets().add(new Triplet(2, 99.0f, 100));

        CumulativePacking serviceUnderTest = new CumulativePacking();
        List<Triplet> triplets = serviceUnderTest.getOptimalTriplets(problem);

        assertEquals(triplets.size(),0);
    }

    @Test
    public void ThrowExceptionWhenInvalidWeightValue(){
        String expectedExceptionMessage =String.format("Invalid problem : Max item weight possible is %d",
                CumulativePacking.MAX_TRIPLET_WEIGHT);
        Problem problem = new Problem(10);
        problem.getTriplets().add(new Triplet(1,110.0f,99));

        CumulativePacking serviceUnderTest = new CumulativePacking();
        APIException exception = assertThrows(APIException.class, () -> serviceUnderTest.getOptimalTriplets(problem));

        assertEquals(expectedExceptionMessage,exception.getMessage());

    }

    @Test
    public void ThrowExceptionWhenInvalidCostValue(){
        String expectedExceptionMessage =String.format("Invalid problem : Max item cost possible is %d",
                CumulativePacking.MAX_TRIPLET_COST);
        Problem problem = new Problem(10);
        problem.getTriplets().add(new Triplet(1,80.0f,110));

        CumulativePacking serviceUnderTest = new CumulativePacking();
        APIException exception = assertThrows(APIException.class, () -> serviceUnderTest.getOptimalTriplets(problem));

        assertEquals(expectedExceptionMessage,exception.getMessage());

    }

    @Test
    public void ThrowExceptionWhenInvalidNumberOfItemInProblem(){
        String expectedExceptionMessage =String.format("Invalid problem : Max items possible is %d",
                CumulativePacking.MAX_TRIPLETS_SIZE_IN_PROBLEM);
        List<Triplet> triplets = IntStream.range(0,CumulativePacking.MAX_TRIPLETS_SIZE_IN_PROBLEM + 1)
                .mapToObj(i-> new Triplet(i,80.0f,90))
                .collect(Collectors.toList());
        Problem problem = new Problem(10,triplets);

        CumulativePacking serviceUnderTest = new CumulativePacking();
        APIException exception = assertThrows(APIException.class, () -> serviceUnderTest.getOptimalTriplets(problem));

        assertEquals(expectedExceptionMessage,exception.getMessage());

    }


}
