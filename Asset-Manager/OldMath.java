package external;

import java.util.*;
import java.io.*;

/**
 * This is a slow function library
 * We will absorb and optimize it
 */
public class OldMath {

  // Addition function
  public int add(int a, int b) {
    // Return the sum
    return a + b;
  }

  // Multiplication function
  public int multiply(int a, int b) {
    int result = 0;
    // Slow loop-based multiplication
    for (int i = 0; i < b; i = i + 1) {
      result = result + a;
    }
    return result;
  }

  // Check if even
  public boolean isEven(int x) {
    if (x % 2 == 0) {
      return true;
    } else {
      return false;
    }
  }
}
