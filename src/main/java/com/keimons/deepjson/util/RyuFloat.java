// Copyright 2018 Ulf Adams
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.keimons.deepjson.util;

import java.math.BigInteger;

/**
 * An implementation of Ryu for float.
 */
public final class RyuFloat {

	private static final int FLOAT_MANTISSA_BITS = 23;
	private static final int FLOAT_MANTISSA_MASK = (1 << FLOAT_MANTISSA_BITS) - 1;

	private static final int FLOAT_EXPONENT_BITS = 8;
	private static final int FLOAT_EXPONENT_MASK = (1 << FLOAT_EXPONENT_BITS) - 1;
	private static final int FLOAT_EXPONENT_BIAS = (1 << (FLOAT_EXPONENT_BITS - 1)) - 1;

	private static final long LOG10_2_DENOMINATOR = 10000000L;
	private static final long LOG10_2_NUMERATOR = (long) (LOG10_2_DENOMINATOR * Math.log10(2));

	private static final long LOG10_5_DENOMINATOR = 10000000L;
	private static final long LOG10_5_NUMERATOR = (long) (LOG10_5_DENOMINATOR * Math.log10(5));

	private static final long LOG2_5_DENOMINATOR = 10000000L;
	private static final long LOG2_5_NUMERATOR = (long) (LOG2_5_DENOMINATOR * (Math.log(5) / Math.log(2)));

	private static final int POS_TABLE_SIZE = 47;
	private static final int INV_TABLE_SIZE = 31;

	// Only for debugging.
	private static final BigInteger[] POW5 = new BigInteger[POS_TABLE_SIZE];
	private static final BigInteger[] POW5_INV = new BigInteger[INV_TABLE_SIZE];

	private static final int POW5_BITCOUNT = 61;
	private static final int POW5_HALF_BITCOUNT = 31;
	private static final int[][] POW5_SPLIT = new int[POS_TABLE_SIZE][2];

	private static final int POW5_INV_BITCOUNT = 59;
	private static final int POW5_INV_HALF_BITCOUNT = 31;
	private static final int[][] POW5_INV_SPLIT = new int[INV_TABLE_SIZE][2];

	static {
		BigInteger mask = BigInteger.valueOf(1).shiftLeft(POW5_HALF_BITCOUNT).subtract(BigInteger.ONE);
		BigInteger maskInv = BigInteger.valueOf(1).shiftLeft(POW5_INV_HALF_BITCOUNT).subtract(BigInteger.ONE);
		for (int i = 0; i < POW5.length; i++) {
			BigInteger pow = BigInteger.valueOf(5).pow(i);
			int pow5len = pow.bitLength();
			int expectedPow5Bits = pow5bits(i);
			if (expectedPow5Bits != pow5len) {
				throw new IllegalStateException(pow5len + " != " + expectedPow5Bits);
			}
			POW5[i] = pow;
			POW5_SPLIT[i][0] = pow.shiftRight(pow5len - POW5_BITCOUNT + POW5_HALF_BITCOUNT).intValueExact();
			POW5_SPLIT[i][1] = pow.shiftRight(pow5len - POW5_BITCOUNT).and(mask).intValueExact();

			if (i < POW5_INV.length) {
				int j = pow5len - 1 + POW5_INV_BITCOUNT;
				BigInteger inv = BigInteger.ONE.shiftLeft(j).divide(pow).add(BigInteger.ONE);
				POW5_INV[i] = inv;
				POW5_INV_SPLIT[i][0] = inv.shiftRight(POW5_INV_HALF_BITCOUNT).intValueExact();
				POW5_INV_SPLIT[i][1] = inv.and(maskInv).intValueExact();
			}
		}
	}

	public static int length(float value) {
		// Step 1: Decode the floating point number, and unify normalized and subnormal cases.
		// First, handle all the trivial cases.
		if (Float.isNaN(value)) {
			return 3;
		}
		if (value == Float.POSITIVE_INFINITY) {
			return 8;
		}
		if (value == Float.NEGATIVE_INFINITY) {
			return 9;
		}
		int bits = Float.floatToIntBits(value);
		if (bits == 0) {
			return 3;
		}
		if (bits == 0x80000000) {
			return 4;
		}

		// Otherwise extract the mantissa and exponent bits and run the full algorithm.
		int ieeeExponent = (bits >> FLOAT_MANTISSA_BITS) & FLOAT_EXPONENT_MASK;
		int ieeeMantissa = bits & FLOAT_MANTISSA_MASK;
		// By default, the correct mantissa starts with a 1, except for denormal numbers.
		int e2;
		int m2;
		if (ieeeExponent == 0) {
			e2 = 1 - FLOAT_EXPONENT_BIAS - FLOAT_MANTISSA_BITS;
			m2 = ieeeMantissa;
		} else {
			e2 = ieeeExponent - FLOAT_EXPONENT_BIAS - FLOAT_MANTISSA_BITS;
			m2 = ieeeMantissa | (1 << FLOAT_MANTISSA_BITS);
		}

		boolean sign = bits < 0;

		// Step 2: Determine the interval of legal decimal representations.
		boolean even = (m2 & 1) == 0;
		int mp = 4 * m2 + 2;
		int mm = 4 * m2 - ((m2 != (1L << FLOAT_MANTISSA_BITS)) || (ieeeExponent <= 1) ? 2 : 1);
		e2 -= 2;

		// Step 3: Convert to a decimal power base using 128-bit arithmetic.
		// -151 = 1 - 127 - 23 - 2 <= e_2 - 2 <= 254 - 127 - 23 - 2 = 102
		int dp, dm;
		int e10;
		boolean dpIsTrailingZeros, dmIsTrailingZeros;
		if (e2 >= 0) {
			// Compute m * 2^e_2 / 10^q = m * 2^(e_2 - q) / 5^q
			int q = (int) (e2 * LOG10_2_NUMERATOR / LOG10_2_DENOMINATOR);
			int k = POW5_INV_BITCOUNT + pow5bits(q) - 1;
			int i = -e2 + q + k;

			long pis0 = POW5_INV_SPLIT[q][0];
			long pis1 = POW5_INV_SPLIT[q][1];
			dp = (int) ((mp * pis0 + ((mp * pis1) >> 31)) >> (i - 31));
			dm = (int) ((mm * pis0 + ((mm * pis1) >> 31)) >> (i - 31));
			e10 = q;
			int pow5Factor_mp = 0;
			int v = mp;
			while (v % 5 == 0) {
				v /= 5;
				pow5Factor_mp++;
			}

			int pow5Factor_mm = 0;
			v = mm;
			while (v % 5 == 0) {
				v /= 5;
				pow5Factor_mm++;
			}

			dpIsTrailingZeros = pow5Factor_mp >= q;
			dmIsTrailingZeros = pow5Factor_mm >= q;
		} else {
			int q = (int) (-e2 * LOG10_5_NUMERATOR / LOG10_5_DENOMINATOR);
			int i = -e2 - q;
			int k = pow5bits(i) - POW5_BITCOUNT;
			int j = q - k;

			long ps0 = POW5_SPLIT[i][0];
			long ps1 = POW5_SPLIT[i][1];
			int j31 = j - 31;
			dp = (int) ((mp * ps0 + ((mp * ps1) >> 31)) >> j31);
			dm = (int) ((mm * ps0 + ((mm * ps1) >> 31)) >> j31);

			e10 = q + e2; // Note: e2 and e10 are both negative here.

			dpIsTrailingZeros = 1 >= q;
			dmIsTrailingZeros = (mm % 2 == 1 ? 0 : 1) >= q;
		}

		// Step 4: Find the shortest decimal representation in the interval of legal representations.
		//
		// We do some extra work here in order to follow Float/Double.toString semantics. In particular,
		// that requires printing in scientific format if and only if the exponent is between -3 and 7,
		// and it requires printing at least two decimal digits.
		//
		// Above, we moved the decimal dot all the way to the right, so now we need to count digits to
		// figure out the correct exponent for scientific notation.

		int dplength = 10;
		int factor = 1000000000;
		for (; dplength > 0; dplength--) {
			if (dp >= factor) {
				break;
			}
			factor /= 10;
		}
		int exp = e10 + dplength - 1;

		// Float.toString semantics requires using scientific notation if and only if outside this range.
		boolean scientificNotation = !((exp >= -3) && (exp < 7));

		int removed = 0;
		if (dpIsTrailingZeros && !even) {
			dp--;
		}

		while (dp / 10 > dm / 10) {
			if ((dp < 100) && scientificNotation) {
				// We print at least two digits, so we might as well stop now.
				break;
			}
			dmIsTrailingZeros &= dm % 10 == 0;
			dp /= 10;
			dm /= 10;
			removed++;
		}
		if (dmIsTrailingZeros && even) {
			while (dm % 10 == 0) {
				if ((dp < 100) && scientificNotation) {
					// We print at least two digits, so we might as well stop now.
					break;
				}
				dp /= 10;
				dm /= 10;
				removed++;
			}
		}

		// Round down not up if the number ends in X50000 and the number is even.
		int olength = dplength - removed;

		int index = 0;
		if (sign) {
			index = 1;
		}

		if (scientificNotation) {
			index += olength + 3;
			if (olength == 1) {
				index++;
			}
			if (exp < 0) {
				index++;
				exp = -exp;
			}
			if (exp >= 10) {
				index++;
			}
		} else {
			// Otherwise follow the Java spec for values in the interval [1E-3, 1E7).
			if (exp < 0) {
				// Decimal dot is before any of the digits.
				index += olength + 2;
				for (int i = -1; i > exp; i--) {
					index++;
				}
			} else if (exp + 1 >= olength) {
				index += olength + 2;
				for (int i = olength; i < exp + 1; i++) {
					index++;
				}
			} else {
				index += olength + 1;
			}
		}
		return index;
	}

	private static int pow5bits(int e) {
		return e == 0 ? 1 : (int) ((e * LOG2_5_NUMERATOR + LOG2_5_DENOMINATOR - 1) / LOG2_5_DENOMINATOR);
	}
}