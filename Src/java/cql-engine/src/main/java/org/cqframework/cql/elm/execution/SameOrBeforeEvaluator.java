package org.cqframework.cql.elm.execution;

import org.cqframework.cql.execution.Context;
import org.cqframework.cql.runtime.DateTime;

/*
The same-precision-or before operator compares two date/time values to the specified precision to determine
  whether the first argument is the same or before the second argument.
For DateTime values, precision must be one of: year, month, day, hour, minute, second, or millisecond.
For Time values, precision must be one of: hour, minute, second, or millisecond.
For comparisons involving date/time or time values with imprecision, note that the result of the comparison may be null,
  depending on whether the values involved are specified to the level of precision used for the comparison.
If either or both arguments are null, the result is null.
*/

/**
* Created by Chris Schuler on 6/23/2016
*/
public class SameOrBeforeEvaluator extends SameOrBefore {

  @Override
  public Object evaluate(Context context) {
    Object left = getOperand().get(0).evaluate(context);
    Object right = getOperand().get(1).evaluate(context);

    if (left == null || right == null) { return null; }

    if (left instanceof DateTime && right instanceof DateTime) {
      DateTime leftDT = (DateTime)left;
      DateTime rightDT = (DateTime)right;
      String precision = getPrecision().value();

      int idx = DateTime.getFieldIndex(precision);

      if (idx != -1) {
        // check level of precision
        if (idx + 1 > leftDT.getPartial().size() || idx + 1 > rightDT.getPartial().size()) {
          return null;
        }

        for (int i = 0; i < leftDT.getPartial().size(); ++i) {
          if (leftDT.getPartial().getValue(i) > rightDT.getPartial().getValue(i)) {
            return false;
          }
          else if (leftDT.getPartial().getValue(i) < rightDT.getPartial().getValue(i)) {
            return true;
          }
        }

        return leftDT.getPartial().getValue(idx) <= rightDT.getPartial().getValue(idx);
      }

      // TODO: Implement for Time

      else {
        throw new IllegalArgumentException(String.format("Invalid duration precision: %s", precision));
      }
    }
    throw new IllegalArgumentException(String.format("Cannot SameOrBefore arguments of type '%s' and '%s'.", left.getClass().getName(), right.getClass().getName()));
  }
}