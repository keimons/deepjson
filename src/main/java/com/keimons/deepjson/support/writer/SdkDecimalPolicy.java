/*
 * Copyright (c) 1996, 2016, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.keimons.deepjson.support.writer;

import com.keimons.deepjson.IDecimalStrategy;

import java.util.Arrays;


/**
 * An implementation of sdk for float and double.
 */
public class SdkDecimalPolicy implements IDecimalStrategy {

	//
	// Constants of the implementation;
	// most are IEEE-754 related.
	// (There are more really boring constants at the end.)
	//
	static final int EXP_SHIFT = 52;
	static final long FRACT_HOB = (1L << EXP_SHIFT); // assumed High-Order bit
	static final long EXP_ONE = ((long) 1023) << EXP_SHIFT; // exponent of 1.0
	static final int MAX_SMALL_BIN_EXP = 62;
	static final int MIN_SMALL_BIN_EXP = -(63 / 3);

	static final int SINGLE_FRACT_HOB = 1 << 23;

	static final long[] LONG_5_POW = {
			1L,
			5L,
			5L * 5,
			5L * 5 * 5,
			5L * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
	};
	/**
	 * If insignificant==(1L << ixd)
	 * i = insignificantDigitsNumber[idx] is the same as:
	 * int i;
	 * for ( i = 0; insignificant >= 10L; i++ )
	 * insignificant /= 10L;
	 */
	private static final int[] insignificantDigitsNumber = {
			0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3,
			4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7,
			8, 8, 8, 9, 9, 9, 9, 10, 10, 10, 11, 11, 11,
			12, 12, 12, 12, 13, 13, 13, 14, 14, 14,
			15, 15, 15, 15, 16, 16, 16, 17, 17, 17,
			18, 18, 18, 19
	};
	// approximately ceil( log2( long5pow[i] ) )
	private static final int[] N_5_BITS = {
			0,
			3,
			5,
			7,
			10,
			12,
			14,
			17,
			19,
			21,
			24,
			26,
			28,
			31,
			33,
			35,
			38,
			40,
			42,
			45,
			47,
			49,
			52,
			54,
			56,
			59,
			61,
	};
	private final char[] digits;
	private int decExponent;

	//
	// The fields below provide additional information about the result of
	// the binary to decimal digits conversion done in dtoa() and roundup()
	// methods. They are changed if needed by those two methods.
	//
	private int firstDigitIndex;
	private int nDigits;

	/**
	 * Default constructor; used for non-zero values,
	 * <code>BinaryToASCIIBuffer</code> may be thread-local and reused
	 */
	public SdkDecimalPolicy() {
		this.digits = new char[20];
	}

	/**
	 * Estimate decimal exponent. (If it is small-ish,
	 * we could double-check.)
	 * <p>
	 * First, scale the mantissa bits such that 1 <= d2 < 2.
	 * We are then going to estimate
	 * log10(d2) ~=~  (d2-1.5)/1.5 + log(1.5)
	 * and so we can estimate
	 * log10(d) ~=~ log10(d2) + binExp * log10(2)
	 * take the floor and call it decExp.
	 */
	static int estimateDecExp(long fractBits, int binExp) {
		double d2 = Double.longBitsToDouble(EXP_ONE | (fractBits & 0x000FFFFFFFFFFFFFL));
		double d = (d2 - 1.5D) * 0.289529654D + 0.176091259 + (double) binExp * 0.301029995663981;
		long dBits = Double.doubleToRawLongBits(d);  //can't be NaN here so use raw
		int exponent = (int) ((dBits & 0x7FF0000000000000L) >> EXP_SHIFT) - 1023;
		boolean isNegative = (dBits & 0x8000000000000000L) != 0; // discover sign
		if (exponent >= 0 && exponent < 52) { // hot path
			long mask = 0x000FFFFFFFFFFFFFL >> exponent;
			int r = (int) (((dBits & 0x000FFFFFFFFFFFFFL) | FRACT_HOB) >> (EXP_SHIFT - exponent));
			return isNegative ? (((mask & dBits) == 0L) ? -r : -r - 1) : r;
		} else if (exponent < 0) {
			return (((dBits & ~0x8000000000000000L) == 0) ? 0 :
					((isNegative) ? -1 : 0));
		} else { //if (exponent >= 52)
			return (int) d;
		}
	}

	/**
	 * Calculates
	 * <pre>
	 * insignificantDigitsForPow2(v) == insignificantDigits(1L<<v)
	 * </pre>
	 */
	private static int insignificantDigitsForPow2(int p2) {
		if (p2 > 1 && p2 < insignificantDigitsNumber.length) {
			return insignificantDigitsNumber[p2];
		}
		return 0;
	}

	@Override
	public int write(double d, char[] buf, int writeIndex) {
		int markIndex = writeIndex;
		long dBits = Double.doubleToRawLongBits(d);
		boolean isNegative = (dBits & 0x8000000000000000L) != 0; // discover sign
		long fractBits = dBits & 0x000FFFFFFFFFFFFFL;
		int binExp = (int) ((dBits & 0x7FF0000000000000L) >> EXP_SHIFT);
		// Discover obvious special cases of NaN and Infinity.
		if (binExp == (int) (0x7FF0000000000000L >> EXP_SHIFT)) {
			if (fractBits == 0L) {
				if (isNegative) {
					buf[markIndex++] = '-';
				}
				buf[markIndex++] = 'I';
				buf[markIndex++] = 'n';
				buf[markIndex++] = 'f';
				buf[markIndex++] = 'i';
				buf[markIndex++] = 'n';
				buf[markIndex++] = 'i';
				buf[markIndex++] = 't';
				buf[markIndex++] = 'y';
			} else {
				buf[markIndex++] = 'N';
				buf[markIndex++] = 'a';
				buf[markIndex++] = 'N';
			}
			return markIndex - writeIndex;
		}
		// Finish unpacking
		// Normalize denormalized numbers.
		// Insert assumed high-order bit for normalized numbers.
		// Subtract exponent bias.
		int nSignificantBits;
		if (binExp == 0) {
			if (fractBits == 0L) {
				// not a denorm, just a 0!
				buf[markIndex++] = '0';
				return markIndex - writeIndex;
			}
			int leadingZeros = Long.numberOfLeadingZeros(fractBits);
			int shift = leadingZeros - (63 - EXP_SHIFT);
			fractBits <<= shift;
			binExp = 1 - shift;
			nSignificantBits = 64 - leadingZeros; // recall binExp is  - shift count.
		} else {
			fractBits |= FRACT_HOB;
			nSignificantBits = EXP_SHIFT + 1;
		}
		binExp -= 1023;
		// call the routine that actually does all the hard work.
		dtoa(binExp, fractBits, nSignificantBits);

		assert nDigits <= 19 : nDigits; // generous bound on size of nDigits
		if (isNegative) {
			buf[writeIndex++] = '-';
		}
		if (decExponent > 0 && decExponent < 8) {
			// print digits.digits.
			int charLength = Math.min(nDigits, decExponent);
			System.arraycopy(digits, firstDigitIndex, buf, writeIndex, charLength);
			writeIndex += charLength;
			if (charLength < decExponent) {
				charLength = decExponent - charLength;
				Arrays.fill(buf, writeIndex, writeIndex + charLength, '0');
				writeIndex += charLength;
				buf[writeIndex++] = '.';
				buf[writeIndex++] = '0';
			} else {
				buf[writeIndex++] = '.';
				if (charLength < nDigits) {
					int t = nDigits - charLength;
					System.arraycopy(digits, firstDigitIndex + charLength, buf, writeIndex, t);
					writeIndex += t;
				} else {
					buf[writeIndex++] = '0';
				}
			}
		} else if (decExponent <= 0 && decExponent > -3) {
			buf[writeIndex++] = '0';
			buf[writeIndex++] = '.';
			if (decExponent != 0) {
				Arrays.fill(buf, writeIndex, writeIndex - decExponent, '0');
				writeIndex -= decExponent;
			}
			System.arraycopy(digits, firstDigitIndex, buf, writeIndex, nDigits);
			writeIndex += nDigits;
		} else {
			buf[writeIndex++] = digits[firstDigitIndex];
			buf[writeIndex++] = '.';
			if (nDigits > 1) {
				System.arraycopy(digits, firstDigitIndex + 1, buf, writeIndex, nDigits - 1);
				writeIndex += nDigits - 1;
			} else {
				buf[writeIndex++] = '0';
			}
			buf[writeIndex++] = 'E';
			int e;
			if (decExponent <= 0) {
				buf[writeIndex++] = '-';
				e = -decExponent + 1;
			} else {
				e = decExponent - 1;
			}
			// decExponent has 1, 2, or 3, digits
			if (e <= 9) {
				buf[writeIndex++] = (char) (e + '0');
			} else if (e <= 99) {
				buf[writeIndex++] = (char) (e / 10 + '0');
				buf[writeIndex++] = (char) (e % 10 + '0');
			} else {
				buf[writeIndex++] = (char) (e / 100 + '0');
				e %= 100;
				buf[writeIndex++] = (char) (e / 10 + '0');
				buf[writeIndex++] = (char) (e % 10 + '0');
			}
		}
		return writeIndex - markIndex;
	}

	@Override
	public int write(float f, char[] buf, int writeIndex) {
		int markIndex = writeIndex;
		int fBits = Float.floatToRawIntBits(f);
		boolean isNegative = (fBits & 0x80000000) != 0;
		int fractBits = fBits & 0x007FFFFF;
		int binExp = (fBits & 0x7F800000) >> 23;
		// Discover obvious special cases of NaN and Infinity.
		if (binExp == (0x7F800000 >> 23)) {
			if (fractBits == 0L) {
				if (isNegative) {
					buf[writeIndex++] = '-';
				}
				buf[writeIndex++] = 'I';
				buf[writeIndex++] = 'n';
				buf[writeIndex++] = 'f';
				buf[writeIndex++] = 'i';
				buf[writeIndex++] = 'n';
				buf[writeIndex++] = 'i';
				buf[writeIndex++] = 't';
				buf[writeIndex++] = 'y';
			} else {
				buf[writeIndex++] = 'N';
				buf[writeIndex++] = 'a';
				buf[writeIndex++] = 'N';
			}
			return writeIndex - markIndex;
		}
		// Finish unpacking
		// Normalize denormalized numbers.
		// Insert assumed high-order bit for normalized numbers.
		// Subtract exponent bias.
		int nSignificantBits;
		if (binExp == 0) {
			if (fractBits == 0) {
				// not a denorm, just a 0!
				if (isNegative) {
					buf[writeIndex++] = '-';
				}
				buf[writeIndex++] = '0';
				buf[writeIndex++] = '.';
				buf[writeIndex++] = '0';
				return writeIndex - markIndex;
			}
			int leadingZeros = Integer.numberOfLeadingZeros(fractBits);
			int shift = leadingZeros - (31 - 23);
			fractBits <<= shift;
			binExp = 1 - shift;
			nSignificantBits = 32 - leadingZeros; // recall binExp is  - shift count.
		} else {
			fractBits |= SINGLE_FRACT_HOB;
			nSignificantBits = 23 + 1;
		}
		binExp -= 127;
		// call the routine that actually does all the hard work.
		dtoa(binExp, ((long) fractBits) << (EXP_SHIFT - 23), nSignificantBits);

		assert nDigits <= 19 : nDigits; // generous bound on size of nDigits
		if (isNegative) {
			buf[writeIndex++] = '-';
		}
		if (decExponent > 0 && decExponent < 8) {
			// print digits.digits.
			int charLength = Math.min(nDigits, decExponent);
			System.arraycopy(digits, firstDigitIndex, buf, writeIndex, charLength);
			writeIndex += charLength;
			if (charLength < decExponent) {
				charLength = decExponent - charLength;
				Arrays.fill(buf, writeIndex, writeIndex + charLength, '0');
				writeIndex += charLength;
				buf[writeIndex++] = '.';
				buf[writeIndex++] = '0';
			} else {
				buf[writeIndex++] = '.';
				if (charLength < nDigits) {
					int t = nDigits - charLength;
					System.arraycopy(digits, firstDigitIndex + charLength, buf, writeIndex, t);
					writeIndex += t;
				} else {
					buf[writeIndex++] = '0';
				}
			}
		} else if (decExponent <= 0 && decExponent > -3) {
			buf[writeIndex++] = '0';
			buf[writeIndex++] = '.';
			if (decExponent != 0) {
				Arrays.fill(buf, writeIndex, writeIndex - decExponent, '0');
				writeIndex -= decExponent;
			}
			System.arraycopy(digits, firstDigitIndex, buf, writeIndex, nDigits);
			writeIndex += nDigits;
		} else {
			buf[writeIndex++] = digits[firstDigitIndex];
			buf[writeIndex++] = '.';
			if (nDigits > 1) {
				System.arraycopy(digits, firstDigitIndex + 1, buf, writeIndex, nDigits - 1);
				writeIndex += nDigits - 1;
			} else {
				buf[writeIndex++] = '0';
			}
			buf[writeIndex++] = 'E';
			int e;
			if (decExponent <= 0) {
				buf[writeIndex++] = '-';
				e = -decExponent + 1;
			} else {
				e = decExponent - 1;
			}
			// decExponent has 1, 2, or 3, digits
			if (e <= 9) {
				buf[writeIndex++] = (char) (e + '0');
			} else if (e <= 99) {
				buf[writeIndex++] = (char) (e / 10 + '0');
				buf[writeIndex++] = (char) (e % 10 + '0');
			} else {
				buf[writeIndex++] = (char) (e / 100 + '0');
				e %= 100;
				buf[writeIndex++] = (char) (e / 10 + '0');
				buf[writeIndex++] = (char) (e % 10 + '0');
			}
		}
		return writeIndex - markIndex;
	}

	/**
	 * This is the easy subcase --
	 * all the significant bits, after scaling, are held in lvalue.
	 * negSign and decExponent tell us what processing and scaling
	 * has already been done. Exceptional cases have already been
	 * stripped out.
	 * In particular:
	 * lvalue is a finite number (not Inf, nor NaN)
	 * lvalue > 0L (not zero, nor negative).
	 * <p>
	 * The only reason that we develop the digits here, rather than
	 * calling on Long.toString() is that we can do it a little faster,
	 * and besides want to treat trailing 0s specially. If Long.toString
	 * changes, we should re-evaluate this strategy!
	 */
	private void developLongDigits(int decExponent, long lvalue, int insignificantDigits) {
		if (insignificantDigits != 0) {
			// Discard non-significant low-order bits, while rounding,
			// up to insignificant value.
			long pow10 = LONG_5_POW[insignificantDigits] << insignificantDigits; // 10^i == 5^i * 2^i;
			long residue = lvalue % pow10;
			lvalue /= pow10;
			decExponent += insignificantDigits;
			if (residue >= (pow10 >> 1)) {
				// round up based on the low-order bits we're discarding
				lvalue++;
			}
		}
		int digitno = digits.length - 1;
		int c;
		if (lvalue <= Integer.MAX_VALUE) {
			assert lvalue > 0L : lvalue; // lvalue <= 0
			// even easier subcase!
			// can do int arithmetic rather than long!
			int ivalue = (int) lvalue;
			c = ivalue % 10;
			ivalue /= 10;
			while (c == 0) {
				decExponent++;
				c = ivalue % 10;
				ivalue /= 10;
			}
			while (ivalue != 0) {
				digits[digitno--] = (char) (c + '0');
				decExponent++;
				c = ivalue % 10;
				ivalue /= 10;
			}
			digits[digitno] = (char) (c + '0');
		} else {
			// same algorithm as above (same bugs, too )
			// but using long arithmetic.
			c = (int) (lvalue % 10L);
			lvalue /= 10L;
			while (c == 0) {
				decExponent++;
				c = (int) (lvalue % 10L);
				lvalue /= 10L;
			}
			while (lvalue != 0L) {
				digits[digitno--] = (char) (c + '0');
				decExponent++;
				c = (int) (lvalue % 10L);
				lvalue /= 10;
			}
			digits[digitno] = (char) (c + '0');
		}
		this.decExponent = decExponent + 1;
		this.firstDigitIndex = digitno;
		this.nDigits = this.digits.length - digitno;
	}

	private void dtoa(int binExp, long fractBits, int nSignificantBits) {
		assert fractBits > 0; // fractBits here can't be zero or negative
		assert (fractBits & FRACT_HOB) != 0; // Hi-order bit should be set
		// Examine number. Determine if it is an easy case,
		// which we can do pretty trivially using float/long conversion,
		// or whether we must do real work.
		final int tailZeros = Long.numberOfTrailingZeros(fractBits);

		// number of significant bits of fractBits;
		final int nFractBits = EXP_SHIFT + 1 - tailZeros;

		// reset flags to default values as dtoa() does not always set these
		// flags and a prior call to dtoa() might have set them to incorrect
		// values with respect to the current state.
		// True if the dtoa() binary to decimal conversion was exact.
		boolean exactDecimalConversion = false;

		// number of significant bits to the right of the point.
		int nTinyBits = Math.max(0, nFractBits - binExp - 1);
		if (binExp <= MAX_SMALL_BIN_EXP && binExp >= MIN_SMALL_BIN_EXP) {
			// Look more closely at the number to decide if,
			// with scaling by 10^nTinyBits, the result will fit in
			// a long.
			if ((nTinyBits < LONG_5_POW.length) && ((nFractBits + N_5_BITS[nTinyBits]) < 64)) {
				//
				// We can do this:
				// take the fraction bits, which are normalized.
				// (a) nTinyBits == 0: Shift left or right appropriately
				//     to align the binary point at the extreme right, i.e.
				//     where a long int point is expected to be. The integer
				//     result is easily converted to a string.
				// (b) nTinyBits > 0: Shift right by EXP_SHIFT-nFractBits,
				//     which effectively converts to long and scales by
				//     2^nTinyBits. Then multiply by 5^nTinyBits to
				//     complete the scaling. We know this won't overflow
				//     because we just counted the number of bits necessary
				//     in the result. The integer you get from this can
				//     then be converted to a string pretty easily.
				//
				if (nTinyBits == 0) {
					int insignificant;
					if (binExp > nSignificantBits) {
						insignificant = insignificantDigitsForPow2(binExp - nSignificantBits - 1);
					} else {
						insignificant = 0;
					}
					if (binExp >= EXP_SHIFT) {
						fractBits <<= (binExp - EXP_SHIFT);
					} else {
						fractBits >>>= (EXP_SHIFT - binExp);
					}
					developLongDigits(0, fractBits, insignificant);
					return;
				}
				//
				// The following causes excess digits to be printed
				// out in the single-float case. Our manipulation of
				// halfULP here is apparently not correct. If we
				// better understand how this works, perhaps we can
				// use this special case again. But for the time being,
				// we do not.
				// else {
				//     fractBits >>>= EXP_SHIFT+1-nFractBits;
				//     fractBits//= long5pow[ nTinyBits ];
				//     halfULP = long5pow[ nTinyBits ] >> (1+nSignificantBits-nFractBits);
				//     developLongDigits( -nTinyBits, fractBits, insignificantDigits(halfULP) );
				//     return;
				// }
				//
			}
		}
		//
		// This is the hard case. We are going to compute large positive
		// integers B and S and integer decExp, s.t.
		//      d = ( B / S )// 10^decExp
		//      1 <= B / S < 10
		// Obvious choices are:
		//      decExp = floor( log10(d) )
		//      B      = d// 2^nTinyBits// 10^max( 0, -decExp )
		//      S      = 10^max( 0, decExp)// 2^nTinyBits
		// (noting that nTinyBits has already been forced to non-negative)
		// I am also going to compute a large positive integer
		//      M      = (1/2^nSignificantBits)// 2^nTinyBits// 10^max( 0, -decExp )
		// i.e. M is (1/2) of the ULP of d, scaled like B.
		// When we iterate through dividing B/S and picking off the
		// quotient bits, we will know when to stop when the remainder
		// is <= M.
		//
		// We keep track of powers of 2 and powers of 5.
		//
		int decExp = estimateDecExp(fractBits, binExp);
		int B2, B5; // powers of 2 and powers of 5, respectively, in B
		int S2, S5; // powers of 2 and powers of 5, respectively, in S
		int M2, M5; // powers of 2 and powers of 5, respectively, in M

		B5 = Math.max(0, -decExp);
		B2 = B5 + nTinyBits + binExp;

		S5 = Math.max(0, decExp);
		S2 = S5 + nTinyBits;

		M5 = B5;
		M2 = B2 - nSignificantBits;

		//
		// the long integer fractBits contains the (nFractBits) interesting
		// bits from the mantissa of d ( hidden 1 added if necessary) followed
		// by (EXP_SHIFT+1-nFractBits) zeros. In the interest of compactness,
		// I will shift out those zeros before turning fractBits into a
		// FDBigInteger. The resulting whole number will be
		//      d * 2^(nFractBits-1-binExp).
		//
		fractBits >>>= tailZeros;
		B2 -= nFractBits - 1;
		int common2factor = Math.min(B2, S2);
		B2 -= common2factor;
		S2 -= common2factor;
		M2 -= common2factor;

		//
		// HACK!! For exact powers of two, the next smallest number
		// is only half as far away as we think (because the meaning of
		// ULP changes at power-of-two bounds) for this reason, we
		// hack M2. Hope this works.
		//
		if (nFractBits == 1) {
			M2 -= 1;
		}

		if (M2 < 0) {
			// oops.
			// since we cannot scale M down far enough,
			// we must scale the other values up.
			B2 -= M2;
			S2 -= M2;
			M2 = 0;
		}
		//
		// Construct, Scale, iterate.
		// Some day, we'll write a stopping test that takes
		// account of the asymmetry of the spacing of floating-point
		// numbers below perfect powers of 2
		// 26 Sept 96 is not that day.
		// So we use a symmetric test.
		//
		int ndigit = 0;
		boolean low, high;
		long lowDigitDifference;
		int q;

		//
		// Detect the special cases where all the numbers we are about
		// to compute will fit in int or long integers.
		// In these cases, we will avoid doing FDBigInteger arithmetic.
		// We use the same algorithms, except that we "normalize"
		// our FDBigIntegers before iterating. This is to make division easier,
		// as it makes our fist guess (quotient of high-order words)
		// more accurate!
		//
		// Some day, we'll write a stopping test that takes
		// account of the asymmetry of the spacing of floating-point
		// numbers below perfect powers of 2
		// 26 Sept 96 is not that day.
		// So we use a symmetric test.
		//
		// binary digits needed to represent B, approx.
		int Bbits = nFractBits + B2 + ((B5 < N_5_BITS.length) ? N_5_BITS[B5] : (B5 * 3));

		// binary digits needed to represent 10*S, approx.
		int tenSbits = S2 + 1 + (((S5 + 1) < N_5_BITS.length) ? N_5_BITS[(S5 + 1)] : ((S5 + 1) * 3));
		if (Bbits < 64 && tenSbits < 64) {
			if (Bbits < 32 && tenSbits < 32) {
				// wa-hoo! They're all ints!
				int b = ((int) fractBits * FDBigInteger.SMALL_5_POW[B5]) << B2;
				int s = FDBigInteger.SMALL_5_POW[S5] << S2;
				int m = FDBigInteger.SMALL_5_POW[M5] << M2;
				int tens = s * 10;
				//
				// Unroll the first iteration. If our decExp estimate
				// was too high, our first quotient will be zero. In this
				// case, we discard it and decrement decExp.
				//
				ndigit = 0;
				q = b / s;
				b = 10 * (b % s);
				m *= 10;
				low = (b < m);
				high = (b + m > tens);
				assert q < 10 : q; // excessively large digit
				if ((q == 0) && !high) {
					// oops. Usually ignore leading zero.
					decExp--;
				} else {
					digits[ndigit++] = (char) ('0' + q);
				}
				//
				// HACK! Java spec sez that we always have at least
				// one digit after the . in either F- or E-form output.
				// Thus we will need more than one digit if we're using
				// E-form
				//
				if (decExp < -3 || decExp >= 8) {
					high = low = false;
				}
				while (!low && !high) {
					q = b / s;
					b = 10 * (b % s);
					m *= 10;
					assert q < 10 : q; // excessively large digit
					if (m > 0L) {
						low = (b < m);
						high = (b + m > tens);
					} else {
						// hack -- m might overflow!
						// in this case, it is certainly > b,
						// which won't
						// and b+m > tens, too, since that has overflowed
						// either!
						low = true;
						high = true;
					}
					digits[ndigit++] = (char) ('0' + q);
				}
				lowDigitDifference = (b << 1) - tens;
				exactDecimalConversion = (b == 0);
			} else {
				// still good! they're all longs!
				long b = (fractBits * LONG_5_POW[B5]) << B2;
				long s = LONG_5_POW[S5] << S2;
				long m = LONG_5_POW[M5] << M2;
				long tens = s * 10L;
				//
				// Unroll the first iteration. If our decExp estimate
				// was too high, our first quotient will be zero. In this
				// case, we discard it and decrement decExp.
				//
				ndigit = 0;
				q = (int) (b / s);
				b = 10L * (b % s);
				m *= 10L;
				low = (b < m);
				high = (b + m > tens);
				assert q < 10 : q; // excessively large digit
				if ((q == 0) && !high) {
					// oops. Usually ignore leading zero.
					decExp--;
				} else {
					digits[ndigit++] = (char) ('0' + q);
				}
				//
				// HACK! Java spec sez that we always have at least
				// one digit after the . in either F- or E-form output.
				// Thus we will need more than one digit if we're using
				// E-form
				//
				if (decExp < -3 || decExp >= 8) {
					high = low = false;
				}
				while (!low && !high) {
					q = (int) (b / s);
					b = 10 * (b % s);
					m *= 10;
					assert q < 10 : q;  // excessively large digit
					if (m > 0L) {
						low = (b < m);
						high = (b + m > tens);
					} else {
						// hack -- m might overflow!
						// in this case, it is certainly > b,
						// which won't
						// and b+m > tens, too, since that has overflowed
						// either!
						low = true;
						high = true;
					}
					digits[ndigit++] = (char) ('0' + q);
				}
				lowDigitDifference = (b << 1) - tens;
				exactDecimalConversion = (b == 0);
			}
		} else {
			//
			// We really must do FDBigInteger arithmetic.
			// Fist, construct our FDBigInteger initial values.
			//
			FDBigInteger Sval = FDBigInteger.valueOfPow52(S5, S2);
			int shiftBias = Sval.getNormalizationBias();
			Sval = Sval.leftShift(shiftBias); // normalize so that division works better

			FDBigInteger Bval = FDBigInteger.valueOfMulPow52(fractBits, B5, B2 + shiftBias);
			FDBigInteger Mval = FDBigInteger.valueOfPow52(M5 + 1, M2 + shiftBias + 1);

			FDBigInteger tenSval = FDBigInteger.valueOfPow52(S5 + 1, S2 + shiftBias + 1); //Sval.mult( 10 );
			//
			// Unroll the first iteration. If our decExp estimate
			// was too high, our first quotient will be zero. In this
			// case, we discard it and decrement decExp.
			//
			ndigit = 0;
			q = Bval.quoRemIteration(Sval);
			low = (Bval.cmp(Mval) < 0);
			high = tenSval.addAndCmp(Bval, Mval) <= 0;

			assert q < 10 : q; // excessively large digit
			if ((q == 0) && !high) {
				// oops. Usually ignore leading zero.
				decExp--;
			} else {
				digits[ndigit++] = (char) ('0' + q);
			}
			//
			// HACK! Java spec sez that we always have at least
			// one digit after the . in either F- or E-form output.
			// Thus we will need more than one digit if we're using
			// E-form
			//
			if (decExp < -3 || decExp >= 8) {
				high = low = false;
			}
			while (!low && !high) {
				q = Bval.quoRemIteration(Sval);
				assert q < 10 : q;  // excessively large digit
				Mval = Mval.multBy10(); //Mval = Mval.mult( 10 );
				low = (Bval.cmp(Mval) < 0);
				high = tenSval.addAndCmp(Bval, Mval) <= 0;
				digits[ndigit++] = (char) ('0' + q);
			}
			if (high && low) {
				Bval = Bval.leftShift(1);
				lowDigitDifference = Bval.cmp(tenSval);
			} else {
				lowDigitDifference = 0L; // this here only for flow analysis!
			}
			exactDecimalConversion = (Bval.cmp(FDBigInteger.ZERO) == 0);
		}
		this.decExponent = decExp + 1;
		this.firstDigitIndex = 0;
		this.nDigits = ndigit;
		//
		// Last digit gets rounded based on stopping condition.
		//
		if (high) {
			if (low) {
				if (lowDigitDifference == 0L) {
					// it's a tie!
					// choose based on which digits we like.
					if ((digits[firstDigitIndex + nDigits - 1] & 1) != 0) {
						roundup();
					}
				} else if (lowDigitDifference > 0) {
					roundup();
				}
			} else {
				roundup();
			}
		}
	}

	// add one to the least significant digit.
	// in the unlikely event there is a carry out, deal with it.
	// assert that this will only happen where there
	// is only one digit, e.g. (float)1e-44 seems to do it.
	//
	private void roundup() {
		int i = (firstDigitIndex + nDigits - 1);
		int q = digits[i];
		if (q == '9') {
			while (q == '9' && i > firstDigitIndex) {
				digits[i] = '0';
				q = digits[--i];
			}
			if (q == '9') {
				// carryout! High-order 1, rest 0s, larger exp.
				decExponent += 1;
				digits[firstDigitIndex] = '1';
				return;
			}
			// else fall through.
		}
		digits[i] = (char) (q + 1);
	}
}