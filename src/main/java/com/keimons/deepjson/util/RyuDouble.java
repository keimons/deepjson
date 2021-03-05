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
 * An implementation of Ryu for double.
 */
public final class RyuDouble {

	private static final int DOUBLE_MANTISSA_BITS = 52;
	private static final long DOUBLE_MANTISSA_MASK = (1L << DOUBLE_MANTISSA_BITS) - 1;

	private static final int DOUBLE_EXPONENT_BITS = 11;
	private static final int DOUBLE_EXPONENT_MASK = (1 << DOUBLE_EXPONENT_BITS) - 1;
	private static final int DOUBLE_EXPONENT_BIAS = (1 << (DOUBLE_EXPONENT_BITS - 1)) - 1;

	private static final int POS_TABLE_SIZE = 326;
	private static final int NEG_TABLE_SIZE = 291;

	// Only for debugging.
	private static final BigInteger[] POW5 = new BigInteger[POS_TABLE_SIZE];
	private static final BigInteger[] POW5_INV = new BigInteger[NEG_TABLE_SIZE];

	private static final int POW5_BITCOUNT = 121; // max 3*31 = 124
	private static final int POW5_QUARTER_BITCOUNT = 31;
	private static final int[][] POW5_SPLIT = new int[POS_TABLE_SIZE][4];

	private static final int POW5_INV_BITCOUNT = 122; // max 3*31 = 124
	private static final int POW5_INV_QUARTER_BITCOUNT = 31;
	private static final int[][] POW5_INV_SPLIT = new int[NEG_TABLE_SIZE][4];

	static {
		BigInteger mask = BigInteger.valueOf(1).shiftLeft(POW5_QUARTER_BITCOUNT).subtract(BigInteger.ONE);
		BigInteger invMask = BigInteger.valueOf(1).shiftLeft(POW5_INV_QUARTER_BITCOUNT).subtract(BigInteger.ONE);
		for (int i = 0; i < POW5.length; i++) {
			BigInteger pow = BigInteger.valueOf(5).pow(i);
			int pow5len = pow.bitLength();
			int expectedPow5Bits = pow5bits(i);
			if (expectedPow5Bits != pow5len) {
				throw new IllegalStateException(pow5len + " != " + expectedPow5Bits);
			}
			POW5[i] = pow;
			for (int j = 0; j < 4; j++) {
				POW5_SPLIT[i][j] = pow
						.shiftRight(pow5len - POW5_BITCOUNT + (3 - j) * POW5_QUARTER_BITCOUNT)
						.and(mask)
						.intValueExact();
			}

			if (i < POW5_INV_SPLIT.length) {
				// We want floor(log_2 5^q) here, which is pow5len - 1.
				int j = pow5len - 1 + POW5_INV_BITCOUNT;
				BigInteger inv = BigInteger.ONE.shiftLeft(j).divide(pow).add(BigInteger.ONE);
				POW5_INV[i] = inv;
				for (int k = 0; k < 4; k++) {
					if (k == 0) {
						POW5_INV_SPLIT[i][k] = inv.shiftRight((3 - k) * POW5_INV_QUARTER_BITCOUNT).intValueExact();
					} else {
						POW5_INV_SPLIT[i][k] = inv.shiftRight((3 - k) * POW5_INV_QUARTER_BITCOUNT).and(invMask).intValueExact();
					}
				}
			}
		}
	}

	public static int length(double value) {
		// Step 1: Decode the floating point number, and unify normalized and subnormal cases.
		// First, handle all the trivial cases.
		if (Double.isNaN(value)) {
			return 3;
		}
		if (value == Double.POSITIVE_INFINITY) {
			return 8;
		}
		if (value == Double.NEGATIVE_INFINITY) {
			return 9;
		}
		long bits = Double.doubleToLongBits(value);
		if (bits == 0) {
			return 3;
		}
		if (bits == 0x8000000000000000L) {
			return 4;
		}

		// Otherwise extract the mantissa and exponent bits and run the full algorithm.
		int ieeeExponent = (int) ((bits >>> DOUBLE_MANTISSA_BITS) & DOUBLE_EXPONENT_MASK);
		long ieeeMantissa = bits & DOUBLE_MANTISSA_MASK;
		int e2;
		long m2;
		if (ieeeExponent == 0) {
			// Denormal number - no implicit leading 1, and the exponent is 1, not 0.
			e2 = 1 - DOUBLE_EXPONENT_BIAS - DOUBLE_MANTISSA_BITS;
			m2 = ieeeMantissa;
		} else {
			// Add implicit leading 1.
			e2 = ieeeExponent - DOUBLE_EXPONENT_BIAS - DOUBLE_MANTISSA_BITS;
			m2 = ieeeMantissa | (1L << DOUBLE_MANTISSA_BITS);
		}

		boolean sign = bits < 0;

		// Step 2: Determine the interval of legal decimal representations.
		boolean even = (m2 & 1) == 0;
		final long mv = 4 * m2;
		final long mp = 4 * m2 + 2;
		final int mmShift = ((m2 != (1L << DOUBLE_MANTISSA_BITS)) || (ieeeExponent <= 1)) ? 1 : 0;
		final long mm = 4 * m2 - 1 - mmShift;
		e2 -= 2;

		// Step 3: Convert to a decimal power base using 128-bit arithmetic.
		// -1077 = 1 - 1023 - 53 - 2 <= e_2 - 2 <= 2046 - 1023 - 53 - 2 = 968
		long dp, dm;
		final int e10;
		boolean dmIsTrailingZeros = false, dvIsTrailingZeros = false;
		if (e2 >= 0) {
			final int q = Math.max(0, ((e2 * 78913) >>> 18) - 1);
			// k = constant + floor(log_2(5^q))
			final int k = POW5_INV_BITCOUNT + pow5bits(q) - 1;
			final int i = -e2 + q + k;
			int actualShift = i - 3 * 31 - 21;
			if (actualShift < 0) {
				throw new IllegalArgumentException("" + actualShift);
			}

			final int[] ints = POW5_INV_SPLIT[q];
			long mHigh = mp >>> 31;
			long mLow = mp & 0x7fffffff;
			long bits13 = mHigh * ints[0];
			long bits03 = mLow * ints[0];
			long bits12 = mHigh * ints[1];
			long bits02 = mLow * ints[1];
			long bits11 = mHigh * ints[2];
			long bits01 = mLow * ints[2];
			long bits10 = mHigh * ints[3];
			long bits00 = mLow * ints[3];

			dp = ((((((
					((bits00 >>> 31) + bits01 + bits10) >>> 31)
					+ bits02 + bits11) >>> 31)
					+ bits03 + bits12) >>> 21)
					+ (bits13 << 10)) >>> actualShift;
			mHigh = mm >>> 31;
			mLow = mm & 0x7fffffff;
			bits13 = mHigh * ints[0];
			bits03 = mLow * ints[0];
			bits12 = mHigh * ints[1];
			bits02 = mLow * ints[1];
			bits11 = mHigh * ints[2];
			bits01 = mLow * ints[2];
			bits10 = mHigh * ints[3];
			bits00 = mLow * ints[3];

			dm = ((((((
					((bits00 >>> 31) + bits01 + bits10) >>> 31)
					+ bits02 + bits11) >>> 31)
					+ bits03 + bits12) >>> 21)
					+ (bits13 << 10)) >>> actualShift;
			e10 = q;

			if (q <= 21) {
				if (mv % 5 == 0) {
					int pow5Factor_mv;
					{
						long v = mv;
						if ((v % 25) != 0) {
							pow5Factor_mv = 1;
						} else if ((v % 125) != 0) {
							pow5Factor_mv = 2;
						} else if ((v % 625) != 0) {
							pow5Factor_mv = 3;
						} else {
							pow5Factor_mv = 4;
							v /= 625;
							while (v % 5 == 0) {
								v /= 5;
								pow5Factor_mv++;
							}
						}
					}
					dvIsTrailingZeros = pow5Factor_mv >= q;
				} else if (even) {
					int pow5Factor_mm;
					{
						long v = mm;
						if ((v % 5) != 0) {
							pow5Factor_mm = 0;
						} else if ((v % 25) != 0) {
							pow5Factor_mm = 1;
						} else if ((v % 125) != 0) {
							pow5Factor_mm = 2;
						} else if ((v % 625) != 0) {
							pow5Factor_mm = 3;
						} else {
							pow5Factor_mm = 4;
							v /= 625;
							while (v % 5 == 0) {
								v /= 5;
								pow5Factor_mm++;
							}
						}
					}

					dmIsTrailingZeros = pow5Factor_mm >= q; //
				} else {
					int pow5Factor_mp;
					{
						long v = mp;
						if ((v % 5) != 0) {
							pow5Factor_mp = 0;
						} else if ((v % 25) != 0) {
							pow5Factor_mp = 1;
						} else if ((v % 125) != 0) {
							pow5Factor_mp = 2;
						} else if ((v % 625) != 0) {
							pow5Factor_mp = 3;
						} else {
							pow5Factor_mp = 4;
							v /= 625;
							while (v % 5 == 0) {
								v /= 5;
								pow5Factor_mp++;
							}
						}
					}

					if (pow5Factor_mp >= q) {
						dp--;
					}
				}
			}
		} else {
			final int q = Math.max(0, ((-e2 * 732923) >>> 20) - 1);
			final int i = -e2 - q;
			final int k = pow5bits(i) - POW5_BITCOUNT;
			final int j = q - k;
			int actualShift = j - 3 * 31 - 21;
			if (actualShift < 0) {
				throw new IllegalArgumentException("" + actualShift);
			}
			int[] ints = POW5_SPLIT[i];
			long mHigh = mp >>> 31;
			long mLow = mp & 0x7fffffff;
			long bits13 = mHigh * ints[0]; // 124
			long bits03 = mLow * ints[0];  // 93
			long bits12 = mHigh * ints[1]; // 93
			long bits02 = mLow * ints[1];  // 62
			long bits11 = mHigh * ints[2]; // 62
			long bits01 = mLow * ints[2];  // 31
			long bits10 = mHigh * ints[3]; // 31
			long bits00 = mLow * ints[3];  // 0
			dp = ((((((
					((bits00 >>> 31) + bits01 + bits10) >>> 31)
					+ bits02 + bits11) >>> 31)
					+ bits03 + bits12) >>> 21)
					+ (bits13 << 10)) >>> actualShift;
			mHigh = mm >>> 31;
			mLow = mm & 0x7fffffff;
			bits13 = mHigh * ints[0]; // 124
			bits03 = mLow * ints[0];  // 93
			bits12 = mHigh * ints[1]; // 93
			bits02 = mLow * ints[1];  // 62
			bits11 = mHigh * ints[2]; // 62
			bits01 = mLow * ints[2];  // 31
			bits10 = mHigh * ints[3]; // 31
			bits00 = mLow * ints[3];  // 0
			dm = ((((((
					((bits00 >>> 31) + bits01 + bits10) >>> 31)
					+ bits02 + bits11) >>> 31)
					+ bits03 + bits12) >>> 21)
					+ (bits13 << 10)) >>> actualShift;
			e10 = q + e2;
			if (q <= 1) {
				dvIsTrailingZeros = true;
				if (even) {
					dmIsTrailingZeros = mmShift == 1;
				} else {
					dp--;
				}
			} else if (q < 63) {
				dvIsTrailingZeros = (mv & ((1L << (q - 1)) - 1)) == 0;
			}
		}

		// Step 4: Find the shortest decimal representation in the interval of legal representations.
		//
		// We do some extra work here in order to follow Float/Double.toString semantics. In particular,
		// that requires printing in scientific format if and only if the exponent is between -3 and 7,
		// and it requires printing at least two decimal digits.
		//
		// Above, we moved the decimal dot all the way to the right, so now we need to count digits to
		// figure out the correct exponent for scientific notation.
		final int vplength = decimalLength(dp);
		int exp = e10 + vplength - 1;

		// Double.toString semantics requires using scientific notation if and only if outside this range.
		boolean scientificNotation = !((exp >= -3) && (exp < 7));

		int removed = 0;

		if (dmIsTrailingZeros || dvIsTrailingZeros) {
			while (dp / 10 > dm / 10) {
				if ((dp < 100) && scientificNotation) {
					// Double.toString semantics requires printing at least two digits.
					break;
				}
				dmIsTrailingZeros &= dm % 10 == 0;
				dp /= 10;
				dm /= 10;
				removed++;
			}
			if (dmIsTrailingZeros) {
				while (dm % 10 == 0) {
					if ((dp < 100) && scientificNotation) {
						// Double.toString semantics requires printing at least two digits.
						break;
					}
					dp /= 10;
					dm /= 10;
					removed++;
				}
			}
		} else {
			while (dp / 10 > dm / 10) {
				if ((dp < 100) && scientificNotation) {
					// Double.toString semantics requires printing at least two digits.
					break;
				}
				dp /= 10;
				dm /= 10;
				removed++;
			}
		}
		int olength = vplength - removed;

		// Step 5: Print the decimal representation.
		// We follow Double.toString semantics here.
		int index = 0;
		if (sign) {
			index = 1;
		}

		// Values in the interval [1E-3, 1E7) are special.
		if (scientificNotation) {
			index += olength + 3;
			if (olength == 1) {
				index++;
			}
			// Print 'E', the exponent sign, and the exponent, which has at most three digits.
			if (exp < 0) {
				index++;
				exp = -exp;
			}
			if (exp >= 100) {
				index += 2;
			} else if (exp >= 10) {
				index++;
			}
		} else {
			// Otherwise follow the Java spec for values in the interval [1E-3, 1E7).
			if (exp < 0) {
				// Decimal dot is before any of the digits.
				index += 2;
				for (int i = -1; i > exp; i--) {
					index++;
				}
				for (int i = 0; i < olength; i++) {
					index++;
				}
			} else if (exp + 1 >= olength) {
				index += exp + 3;
			} else {
				index += olength + 1;
			}
		}
		return index;
	}

	private static int pow5bits(int e) {
		return ((e * 1217359) >>> 19) + 1;
	}

	private static int decimalLength(long x) {
		int d = 0;
		x = -x;
		long p = -10;
		for (int i = 1; i < 19; i++) {
			if (x > p)
				return i + d;
			p = 10 * p;
		}
		return 19 + d;
	}
}