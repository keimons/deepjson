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
package com.keimons.deepjson.support.decimal;

import com.keimons.deepjson.IDecimalStrategy;

import java.math.BigInteger;

/**
 * An implementation of Ryu for float and double.
 */
public final class RyuDecimalPolicy implements IDecimalStrategy {

	public static final RyuDecimalPolicy instance = new RyuDecimalPolicy();

	private final int[][] F_POW5_SPLIT = new int[47][2];
	private final int[][] F_POW5_INV_SPLIT = new int[31][2];
	private final int[][] D_POW5_SPLIT = new int[326][4];
	private final int[][] D_POW5_INV_SPLIT = new int[291][4];

	{
		BigInteger mask = BigInteger.valueOf(1).shiftLeft(31).subtract(BigInteger.ONE);
		for (int i = 0; i < 47; i++) {
			BigInteger pow = BigInteger.valueOf(5).pow(i);
			int pow5len = pow.bitLength();
			int expectedPow5Bits = i == 0 ? 1 : (int) ((i * 23219280L + 9999999L) / 10000000L);
			if (expectedPow5Bits != pow5len) {
				throw new IllegalStateException(pow5len + " != " + expectedPow5Bits);
			}
			F_POW5_SPLIT[i][0] = pow.shiftRight(pow5len - 61 + 31).intValue();
			F_POW5_SPLIT[i][1] = pow.shiftRight(pow5len - 61).and(mask).intValue();

			if (i < 31) {
				int j = pow5len + 58;
				BigInteger inv = BigInteger.ONE.shiftLeft(j).divide(pow).add(BigInteger.ONE);
				F_POW5_INV_SPLIT[i][0] = inv.shiftRight(31).intValue();
				F_POW5_INV_SPLIT[i][1] = inv.and(mask).intValue();
			}
		}

		for (int i = 0; i < 326; i++) {
			BigInteger pow = BigInteger.valueOf(5).pow(i);
			int pow5len = pow.bitLength();
			int expectedPow5Bits = i == 0 ? 1 : (int) ((i * 23219280L + 9999999L) / 10000000L);
			if (expectedPow5Bits != pow5len) {
				throw new IllegalStateException(pow5len + " != " + expectedPow5Bits);
			}
			for (int j = 0; j < 4; j++) {
				D_POW5_SPLIT[i][j] = pow
						.shiftRight(pow5len - 121 + (3 - j) * 31)
						.and(mask)
						.intValue();
			}

			if (i < D_POW5_INV_SPLIT.length) {
				// We want floor(log_2 5^q) here, which is pow5len - 1.
				int j = pow5len - 1 + 122;
				BigInteger inv = BigInteger.ONE.shiftLeft(j).divide(pow).add(BigInteger.ONE);
				for (int k = 0; k < 4; k++) {
					if (k == 0) {
						D_POW5_INV_SPLIT[i][k] = inv.shiftRight((3 - k) * 31).intValue();
					} else {
						D_POW5_INV_SPLIT[i][k] = inv.shiftRight((3 - k) * 31).and(mask).intValue();
					}
				}
			}
		}
	}

	public static String toString(float value) {
		char[] buf = new char[15];
		int writable = instance.write(value, buf, 0);
		return new String(buf, 0, writable);
	}

	public static String write(double value) {
		char[] result = new char[24];
		int writable = instance.write(value, result, 0);
		return new String(result, 0, writable);
	}

	@Override
	public int write(float value, char[] buf, int writeIndex) {
		// Step 1: Decode the floating point number, and unify normalized and subnormal cases.
		// First, handle all the trivial cases.
		int markIndex = writeIndex;
		if (Float.isNaN(value)) {
			buf[writeIndex++] = 'N';
			buf[writeIndex++] = 'a';
			buf[writeIndex++] = 'N';
			return writeIndex - markIndex;
		}

		if (value == Float.POSITIVE_INFINITY) {
			buf[writeIndex++] = 'I';
			buf[writeIndex++] = 'n';
			buf[writeIndex++] = 'f';
			buf[writeIndex++] = 'i';
			buf[writeIndex++] = 'n';
			buf[writeIndex++] = 'i';
			buf[writeIndex++] = 't';
			buf[writeIndex++] = 'y';
			return writeIndex - markIndex;
		}

		if (value == Float.NEGATIVE_INFINITY) {
			buf[writeIndex++] = '-';
			buf[writeIndex++] = 'I';
			buf[writeIndex++] = 'n';
			buf[writeIndex++] = 'f';
			buf[writeIndex++] = 'i';
			buf[writeIndex++] = 'n';
			buf[writeIndex++] = 'i';
			buf[writeIndex++] = 't';
			buf[writeIndex++] = 'y';
			return writeIndex - markIndex;
		}

		int bits = Float.floatToIntBits(value);
		if (bits == 0) {
			buf[writeIndex++] = '0';
			buf[writeIndex++] = '.';
			buf[writeIndex++] = '0';
			return writeIndex - markIndex;
		}
		if (bits == 0x80000000) {
			buf[writeIndex++] = '-';
			buf[writeIndex++] = '0';
			buf[writeIndex++] = '.';
			buf[writeIndex++] = '0';
			return writeIndex - markIndex;
		}

		// Otherwise extract the mantissa and exponent bits and run the full algorithm.
		int ieeeExponent = (bits >> 23) & 0xFF;
		int ieeeMantissa = bits & 0x7FFFFF;
		// By default, the correct mantissa starts with a 1, except for denormal numbers.
		int e2;
		int m2;
		if (ieeeExponent == 0) {
			e2 = -149;
			m2 = ieeeMantissa;
		} else {
			e2 = ieeeExponent - 150;
			m2 = ieeeMantissa | 8388608;
		}

		boolean sign = bits < 0;

		// Step 2: Determine the interval of legal decimal representations.
		boolean even = (m2 & 1) == 0;
		int mv = 4 * m2;
		int mp = 4 * m2 + 2;
		int mm = 4 * m2 - ((m2 != 8388608) || (ieeeExponent <= 1) ? 2 : 1);
		e2 -= 2;

		// Step 3: Convert to a decimal power base using 128-bit arithmetic.
		// -151 = 1 - 127 - 23 - 2 <= e_2 - 2 <= 254 - 127 - 23 - 2 = 102
		int dp, dv, dm;
		int e10;
		boolean dpIsTrailingZeros, dvIsTrailingZeros, dmIsTrailingZeros;
		int lastRemovedDigit = 0;
		if (e2 >= 0) {
			// Compute m * 2^e_2 / 10^q = m * 2^(e_2 - q) / 5^q
			int q = (int) (e2 * 3010299 / 10000000L);
			int k = 58 + (q == 0 ? 1 : (int) ((q * 23219280L + 9999999L) / 10000000L));
			int i = -e2 + q + k;
			long bits0 = F_POW5_INV_SPLIT[q][0];
			long bits1 = F_POW5_INV_SPLIT[q][1];
			dv = (int) ((mv * bits0 + (mv * bits1 >> 31)) >> (i - 31));
			dp = (int) ((mp * bits0 + (mp * bits1 >> 31)) >> (i - 31));
			dm = (int) ((mm * bits0 + (mm * bits1 >> 31)) >> (i - 31));
			if (q != 0 && ((dp - 1) / 10 <= dm / 10)) {
				// We need to know one removed digit even if we are not going to loop below. We could use
				// q = X - 1 above, except that would require 33 bits for the result, and we've found that
				// 32-bit arithmetic is faster even on 64-bit machines.
				int tmp0 = q - 1;
				int l = 58 + (tmp0 == 0 ? 1 : (int) ((tmp0 * 23219280L + 9999999L) / 10000000L));
				int tmp1 = -e2 + q - 1 + l;
				bits0 = F_POW5_INV_SPLIT[tmp0][0];
				bits1 = F_POW5_INV_SPLIT[tmp0][1];
				lastRemovedDigit = (int) (((mv * bits0 + (mv * bits1 >> 31)) >> (tmp1 - 31)) % 10);
			}
			e10 = q;

			int pow5Factor_mp = 0;
			{
				int v = mp;
				while (v > 0) {
					if (v % 5 != 0) {
						break;
					}
					v /= 5;
					pow5Factor_mp++;
				}
			}

			int pow5Factor_mv = 0;
			{
				int v = mv;
				while (v > 0) {
					if (v % 5 != 0) {
						break;
					}
					v /= 5;
					pow5Factor_mv++;
				}
			}

			int pow5Factor_mm = 0;
			{
				int v = mm;
				while (v > 0) {
					if (v % 5 != 0) {
						break;
					}
					v /= 5;
					pow5Factor_mm++;
				}
			}

			dpIsTrailingZeros = pow5Factor_mp >= q;
			dvIsTrailingZeros = pow5Factor_mv >= q;
			dmIsTrailingZeros = pow5Factor_mm >= q;
		} else {
			// Compute m * 5^(-e_2) / 10^q = m * 5^(-e_2 - q) / 2^q
			int q = (int) (-e2 * 6989700L / 10000000L);
			int i = -e2 - q;
			int k = (i == 0 ? 1 : (int) ((i * 23219280L + 9999999L) / 10000000L)) - 61;
			int j = q - k;

			long bits0 = F_POW5_SPLIT[i][0];
			long bits1 = F_POW5_SPLIT[i][1];
			dv = (int) ((mv * bits0 + (mv * bits1 >> 31)) >> (j - 31));
			dp = (int) ((mp * bits0 + (mp * bits1 >> 31)) >> (j - 31));
			dm = (int) ((mm * bits0 + (mm * bits1 >> 31)) >> (j - 31));
			if (q != 0 && ((dp - 1) / 10 <= dm / 10)) {
				int tmp = i + 1;
				j = q - 1 - ((int) ((tmp * 23219280L + 9999999L) / 10000000L) - 61);
				bits0 = F_POW5_SPLIT[tmp][0];
				bits1 = F_POW5_SPLIT[tmp][1];
				lastRemovedDigit = (int) (((mv * bits0 + (mv * bits1 >> 31)) >> (j - 31)) % 10);
			}
			e10 = q + e2; // Note: e2 and e10 are both negative here.

			dpIsTrailingZeros = 1 >= q;
			dvIsTrailingZeros = (q < 23) && (mv & ((1 << (q - 1)) - 1)) == 0;
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
			lastRemovedDigit = dv % 10;
			dv /= 10;
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
				lastRemovedDigit = dv % 10;
				dv /= 10;
				dm /= 10;
				removed++;
			}
		}

		if (dvIsTrailingZeros && (lastRemovedDigit == 5) && (dv % 2 == 0)) {
			// Round down not up if the number ends in X50000 and the number is even.
			lastRemovedDigit = 4;
		}
		int output = dv +
				((dv == dm && !(dmIsTrailingZeros && even)) || (lastRemovedDigit >= 5) ? 1 : 0);
		int olength = dplength - removed;

		// Step 5: Print the decimal representation.
		// We follow Float.toString semantics here.
		if (sign) {
			buf[writeIndex++] = '-';
		}

		if (scientificNotation) {
			// Print in the format x.xxxxxE-yy.
			for (int i = 0; i < olength - 1; i++) {
				int c = output % 10;
				output /= 10;
				buf[writeIndex + olength - i] = (char) ('0' + c);
			}
			buf[writeIndex] = (char) ('0' + output % 10);
			buf[writeIndex + 1] = '.';
			writeIndex += olength + 1;
			if (olength == 1) {
				buf[writeIndex++] = '0';
			}

			// Print 'E', the exponent sign, and the exponent, which has at most two digits.
			buf[writeIndex++] = 'E';
			if (exp < 0) {
				buf[writeIndex++] = '-';
				exp = -exp;
			}
			if (exp >= 10) {
				buf[writeIndex++] = (char) ('0' + exp / 10);
			}
			buf[writeIndex++] = (char) ('0' + exp % 10);
		} else {
			// Otherwise follow the Java spec for values in the interval [1E-3, 1E7).
			if (exp < 0) {
				// Decimal dot is before any of the digits.
				buf[writeIndex++] = '0';
				buf[writeIndex++] = '.';
				for (int i = -1; i > exp; i--) {
					buf[writeIndex++] = '0';
				}
				int current = writeIndex;
				for (int i = 0; i < olength; i++) {
					buf[current + olength - i - 1] = (char) ('0' + output % 10);
					output /= 10;
					writeIndex++;
				}
			} else if (exp + 1 >= olength) {
				// Decimal dot is after any of the digits.
				for (int i = 0; i < olength; i++) {
					buf[writeIndex + olength - i - 1] = (char) ('0' + output % 10);
					output /= 10;
				}
				writeIndex += olength;
				for (int i = olength; i < exp + 1; i++) {
					buf[writeIndex++] = '0';
				}
				buf[writeIndex++] = '.';
				buf[writeIndex++] = '0';
			} else {
				// Decimal dot is somewhere between the digits.
				int current = writeIndex + 1;
				for (int i = 0; i < olength; i++) {
					if (olength - i - 1 == exp) {
						buf[current + olength - i - 1] = '.';
						current--;
					}
					buf[current + olength - i - 1] = (char) ('0' + output % 10);
					output /= 10;
				}
				writeIndex += olength + 1;
			}
		}
		return writeIndex - markIndex;
	}

	public int write(double value, char[] buf, int writeIndex) {
		// Step 1: Decode the floating point number, and unify normalized and subnormal cases.
		// First, handle all the trivial cases.
		int markIndex = writeIndex;
		if (Double.isNaN(value)) {
			buf[markIndex++] = 'N';
			buf[markIndex++] = 'a';
			buf[markIndex++] = 'N';
			return markIndex - writeIndex;
		}

		if (value == Double.POSITIVE_INFINITY) {
			buf[markIndex++] = 'I';
			buf[markIndex++] = 'n';
			buf[markIndex++] = 'f';
			buf[markIndex++] = 'i';
			buf[markIndex++] = 'n';
			buf[markIndex++] = 'i';
			buf[markIndex++] = 't';
			buf[markIndex++] = 'y';
			return markIndex - writeIndex;
		}

		if (value == Double.NEGATIVE_INFINITY) {
			buf[markIndex++] = '-';
			buf[markIndex++] = 'I';
			buf[markIndex++] = 'n';
			buf[markIndex++] = 'f';
			buf[markIndex++] = 'i';
			buf[markIndex++] = 'n';
			buf[markIndex++] = 'i';
			buf[markIndex++] = 't';
			buf[markIndex++] = 'y';
			return markIndex - writeIndex;
		}

		long bits = Double.doubleToLongBits(value);
		if (bits == 0) {
			buf[markIndex++] = '0';
			buf[markIndex++] = '.';
			buf[markIndex++] = '0';
			return markIndex - writeIndex;
		}
		if (bits == 0x8000000000000000L) {
			buf[markIndex++] = '-';
			buf[markIndex++] = '0';
			buf[markIndex++] = '.';
			buf[markIndex++] = '0';
			return markIndex - writeIndex;
		}

		// Otherwise extract the mantissa and exponent bits and run the full algorithm.
		int ieeeExponent = (int) ((bits >>> 52) & 2047);
		long ieeeMantissa = bits & 4503599627370495L;
		int e2;
		long m2;
		if (ieeeExponent == 0) {
			// Denormal number - no implicit leading 1, and the exponent is 1, not 0.
			e2 = -1074;
			m2 = ieeeMantissa;
		} else {
			// Add implicit leading 1.
			e2 = ieeeExponent - 1075;
			m2 = ieeeMantissa | 4503599627370496L;
		}

		boolean sign = bits < 0;

		// Step 2: Determine the interval of legal decimal representations.
		boolean even = (m2 & 1) == 0;
		final long mv = 4 * m2;
		final long mp = 4 * m2 + 2;
		final int mmShift = ((m2 != 4503599627370496L) || (ieeeExponent <= 1)) ? 1 : 0;
		final long mm = 4 * m2 - 1 - mmShift;
		e2 -= 2;

		// Step 3: Convert to a decimal power base using 128-bit arithmetic.
		// -1077 = 1 - 1023 - 53 - 2 <= e_2 - 2 <= 2046 - 1023 - 53 - 2 = 968
		long dv, dp, dm;
		final int e10;
		boolean dmIsTrailingZeros = false, dvIsTrailingZeros = false;
		if (e2 >= 0) {
			final int q = Math.max(0, (int) (e2 * 3010299L / 10000000L) - 1);
			// k = constant + floor(log_2(5^q))
			// q == 0 ? 1 : (int) ((q * 23219280L + 10000000L - 1) / 10000000L)
			final int k = 122 + (q == 0 ? 1 : (int) ((q * 23219280L + 9999999L) / 10000000L)) - 1;
			final int i = -e2 + q + k;

			int actualShift = i - 3 * 31 - 21;
			if (actualShift < 0) {
				throw new IllegalArgumentException("" + actualShift);
			}

			final int[] ints = D_POW5_INV_SPLIT[q];
			{
				long mHigh = mv >>> 31;
				long mLow = mv & 0x7fffffff;
				long bits13 = mHigh * ints[0];
				long bits03 = mLow * ints[0];
				long bits12 = mHigh * ints[1];
				long bits02 = mLow * ints[1];
				long bits11 = mHigh * ints[2];
				long bits01 = mLow * ints[2];
				long bits10 = mHigh * ints[3];
				long bits00 = mLow * ints[3];


				dv = ((((((
						((bits00 >>> 31) + bits01 + bits10) >>> 31)
						+ bits02 + bits11) >>> 31)
						+ bits03 + bits12) >>> 21)
						+ (bits13 << 10)) >>> actualShift;
			}
			{
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
			}
			{
				long mHigh = mm >>> 31;
				long mLow = mm & 0x7fffffff;
				long bits13 = mHigh * ints[0];
				long bits03 = mLow * ints[0];
				long bits12 = mHigh * ints[1];
				long bits02 = mLow * ints[1];
				long bits11 = mHigh * ints[2];
				long bits01 = mLow * ints[2];
				long bits10 = mHigh * ints[3];
				long bits00 = mLow * ints[3];

				dm = ((((((
						((bits00 >>> 31) + bits01 + bits10) >>> 31)
						+ bits02 + bits11) >>> 31)
						+ bits03 + bits12) >>> 21)
						+ (bits13 << 10)) >>> actualShift;
			}

			e10 = q;

			if (q <= 21) {
				if (mv % 5 == 0) {
					int pow5Factor_mv;
					{
						long v = mv;
						if ((v % 5) != 0) {
							pow5Factor_mv = 0;
						} else if ((v % 25) != 0) {
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
			final int q = Math.max(0, (int) (-e2 * 6989700L / 10000000L) - 1);
			final int i = -e2 - q;
			final int k = (i == 0 ? 1 : (int) ((i * 23219280L + 10000000L - 1) / 10000000L)) - 121;
			final int j = q - k;

			int actualShift = j - 3 * 31 - 21;
			if (actualShift < 0) {
				throw new IllegalArgumentException("" + actualShift);
			}
			int[] ints = D_POW5_SPLIT[i];
			{
				long mHigh = mv >>> 31;
				long mLow = mv & 0x7fffffff;
				long bits13 = mHigh * ints[0]; // 124
				long bits03 = mLow * ints[0];  // 93
				long bits12 = mHigh * ints[1]; // 93
				long bits02 = mLow * ints[1];  // 62
				long bits11 = mHigh * ints[2]; // 62
				long bits01 = mLow * ints[2];  // 31
				long bits10 = mHigh * ints[3]; // 31
				long bits00 = mLow * ints[3];  // 0

				dv = ((((((
						((bits00 >>> 31) + bits01 + bits10) >>> 31)
						+ bits02 + bits11) >>> 31)
						+ bits03 + bits12) >>> 21)
						+ (bits13 << 10)) >>> actualShift;
			}
			{
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
			}
			{
				long mHigh = mm >>> 31;
				long mLow = mm & 0x7fffffff;
				long bits13 = mHigh * ints[0]; // 124
				long bits03 = mLow * ints[0];  // 93
				long bits12 = mHigh * ints[1]; // 93
				long bits02 = mLow * ints[1];  // 62
				long bits11 = mHigh * ints[2]; // 62
				long bits01 = mLow * ints[2];  // 31
				long bits10 = mHigh * ints[3]; // 31
				long bits00 = mLow * ints[3];  // 0
				dm = ((((((
						((bits00 >>> 31) + bits01 + bits10) >>> 31)
						+ bits02 + bits11) >>> 31)
						+ bits03 + bits12) >>> 21)
						+ (bits13 << 10)) >>> actualShift;
			}

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
		final int vplength;
		if (dp >= 1000000000000000000L) {
			vplength = 19;
		} else if (dp >= 100000000000000000L) {
			vplength = 18;
		} else if (dp >= 10000000000000000L) {
			vplength = 17;
		} else if (dp >= 1000000000000000L) {
			vplength = 16;
		} else if (dp >= 100000000000000L) {
			vplength = 15;
		} else if (dp >= 10000000000000L) {
			vplength = 14;
		} else if (dp >= 1000000000000L) {
			vplength = 13;
		} else if (dp >= 100000000000L) {
			vplength = 12;
		} else if (dp >= 10000000000L) {
			vplength = 11;
		} else if (dp >= 1000000000L) {
			vplength = 10;
		} else if (dp >= 100000000L) {
			vplength = 9;
		} else if (dp >= 10000000L) {
			vplength = 8;
		} else if (dp >= 1000000L) {
			vplength = 7;
		} else if (dp >= 100000L) {
			vplength = 6;
		} else if (dp >= 10000L) {
			vplength = 5;
		} else if (dp >= 1000L) {
			vplength = 4;
		} else if (dp >= 100L) {
			vplength = 3;
		} else if (dp >= 10L) {
			vplength = 2;
		} else {
			vplength = 1;
		}

		int exp = e10 + vplength - 1;

		// Double.toString semantics requires using scientific notation if and only if outside this range.
		boolean scientificNotation = !((exp >= -3) && (exp < 7));

		int removed = 0;

		int lastRemovedDigit = 0;
		long output;
		if (dmIsTrailingZeros || dvIsTrailingZeros) {
			while (dp / 10 > dm / 10) {
				if ((dp < 100) && scientificNotation) {
					// Double.toString semantics requires printing at least two digits.
					break;
				}
				dmIsTrailingZeros &= dm % 10 == 0;
				dvIsTrailingZeros &= lastRemovedDigit == 0;
				lastRemovedDigit = (int) (dv % 10);
				dp /= 10;
				dv /= 10;
				dm /= 10;
				removed++;
			}
			if (dmIsTrailingZeros && even) {
				while (dm % 10 == 0) {
					if ((dp < 100) && scientificNotation) {
						// Double.toString semantics requires printing at least two digits.
						break;
					}
					dvIsTrailingZeros &= lastRemovedDigit == 0;
					lastRemovedDigit = (int) (dv % 10);
					dp /= 10;
					dv /= 10;
					dm /= 10;
					removed++;
				}
			}
			if (dvIsTrailingZeros && (lastRemovedDigit == 5) && (dv % 2 == 0)) {
				// Round even if the exact numbers is .....50..0.
				lastRemovedDigit = 4;
			}
			output = dv +
					((dv == dm && !(dmIsTrailingZeros && even)) || (lastRemovedDigit >= 5) ? 1 : 0);
		} else {
			while (dp / 10 > dm / 10) {
				if ((dp < 100) && scientificNotation) {
					// Double.toString semantics requires printing at least two digits.
					break;
				}
				lastRemovedDigit = (int) (dv % 10);
				dp /= 10;
				dv /= 10;
				dm /= 10;
				removed++;
			}
			output = dv + ((dv == dm || (lastRemovedDigit >= 5)) ? 1 : 0);
		}
		int olength = vplength - removed;

		// Step 5: Print the decimal representation.
		// We follow Double.toString semantics here.
		if (sign) {
			buf[markIndex++] = '-';
		}

		// Values in the interval [1E-3, 1E7) are special.
		if (scientificNotation) {
			// Print in the format x.xxxxxE-yy.
			for (int i = 0; i < olength - 1; i++) {
				int c = (int) (output % 10);
				output /= 10;
				buf[markIndex + olength - i] = (char) ('0' + c);
			}
			buf[markIndex] = (char) ('0' + output % 10);
			buf[markIndex + 1] = '.';
			markIndex += olength + 1;
			if (olength == 1) {
				buf[markIndex++] = '0';
			}

			// Print 'E', the exponent sign, and the exponent, which has at most three digits.
			buf[markIndex++] = 'E';
			if (exp < 0) {
				buf[markIndex++] = '-';
				exp = -exp;
			}
			if (exp >= 100) {
				buf[markIndex++] = (char) ('0' + exp / 100);
				exp %= 100;
				buf[markIndex++] = (char) ('0' + exp / 10);
			} else if (exp >= 10) {
				buf[markIndex++] = (char) ('0' + exp / 10);
			}
			buf[markIndex++] = (char) ('0' + exp % 10);
			return markIndex - writeIndex;
		} else {
			// Otherwise follow the Java spec for values in the interval [1E-3, 1E7).
			if (exp < 0) {
				// Decimal dot is before any of the digits.
				buf[markIndex++] = '0';
				buf[markIndex++] = '.';
				for (int i = -1; i > exp; i--) {
					buf[markIndex++] = '0';
				}
				int current = markIndex;
				for (int i = 0; i < olength; i++) {
					buf[current + olength - i - 1] = (char) ('0' + output % 10);
					output /= 10;
					markIndex++;
				}
			} else if (exp + 1 >= olength) {
				// Decimal dot is after any of the digits.
				for (int i = 0; i < olength; i++) {
					buf[markIndex + olength - i - 1] = (char) ('0' + output % 10);
					output /= 10;
				}
				markIndex += olength;
				for (int i = olength; i < exp + 1; i++) {
					buf[markIndex++] = '0';
				}
				buf[markIndex++] = '.';
				buf[markIndex++] = '0';
			} else {
				// Decimal dot is somewhere between the digits.
				int current = markIndex + 1;
				for (int i = 0; i < olength; i++) {
					if (olength - i - 1 == exp) {
						buf[current + olength - i - 1] = '.';
						current--;
					}
					buf[current + olength - i - 1] = (char) ('0' + output % 10);
					output /= 10;
				}
				markIndex += olength + 1;
			}
			return markIndex - writeIndex;
		}
	}
}