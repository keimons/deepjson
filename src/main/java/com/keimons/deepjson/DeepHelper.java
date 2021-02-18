package com.keimons.deepjson;

public class DeepHelper {

	static final byte[] DigitOnes = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	};

	static final byte[] DigitTens = {
			'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
			'1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
			'2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
			'3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
			'4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
			'5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
			'6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
			'7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
			'8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
			'9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
	};

	/**
	 * 字节编码是否大端序
	 */
	public static final boolean BIG_ENDIAN;

	public static final int LATIN = 0;

	public static final int UTF16 = 1;

	static final int HI_BYTE_SHIFT;
	static final int LO_BYTE_SHIFT;

	static {
		BIG_ENDIAN = !('a' >>> 8 == 0);
		// 虽然绝大部分设备都是大端序，但是，不排除有小端序的可能
		if (BIG_ENDIAN) {
			HI_BYTE_SHIFT = 8;
			LO_BYTE_SHIFT = 0;
		} else {
			HI_BYTE_SHIFT = 0;
			LO_BYTE_SHIFT = 8;
		}
	}

	public static int size(int j) {
		int d = 1;
		if (j >= 0) {
			d = 0;
			j = -j;
		}
		int p = -10;
		for (int i = 1; i < 10; i++) {
			if (j > p)
				return i + d;
			p = 10 * p;
		}
		return 10 + d;
	}

	public static int size(long x) {
		int d = 1;
		if (x >= 0) {
			d = 0;
			x = -x;
		}
		long p = -10;
		for (int i = 1; i < 19; i++) {
			if (x > p)
				return i + d;
			p = 10 * p;
		}
		return 19 + d;
	}

	public static int putLATIN(byte[] buf, int index, int i) {
		int q, r;
		int charPos = index;

		boolean negative = i < 0;
		if (!negative) {
			i = -i;
		}

		// Generate two digits per iteration
		while (i <= -100) {
			q = i / 100;
			r = (q * 100) - i;
			i = q;
			buf[--charPos] = DigitOnes[r];
			buf[--charPos] = DigitTens[r];
		}

		// We know there are at most two digits left at this point.
		q = i / 10;
		r = (q * 10) - i;
		buf[--charPos] = (byte) ('0' + r);

		// Whatever left is the remaining digit.
		if (q < 0) {
			buf[--charPos] = (byte) ('0' - q);
		}

		if (negative) {
			buf[--charPos] = (byte) '-';
		}
		return charPos;
	}

	public static int putUTF16(byte[] buf, int index, int i) {
		int q, r;
		int charPos = index;

		boolean negative = (i < 0);
		if (!negative) {
			i = -i;
		}

		// Get 2 digits/iteration using ints
		while (i <= -100) {
			q = i / 100;
			r = (q * 100) - i;
			i = q;
			putChar2(buf, --charPos, DigitOnes[r]);
			putChar2(buf, --charPos, DigitTens[r]);
		}

		// We know there are at most two digits left at this point.
		q = i / 10;
		r = (q * 10) - i;
		putChar2(buf, --charPos, '0' + r);

		// Whatever left is the remaining digit.
		if (q < 0) {
			putChar2(buf, --charPos, '0' - q);
		}

		if (negative) {
			putChar2(buf, --charPos, '-');
		}
		return charPos;
	}

	public static int putLATIN(byte[] buf, int index, long i) {
		long q;
		int r;
		int charPos = index;

		boolean negative = (i < 0);
		if (!negative) {
			i = -i;
		}

		// Get 2 digits/iteration using longs until quotient fits into an int
		while (i <= Integer.MIN_VALUE) {
			q = i / 100;
			r = (int) ((q * 100) - i);
			i = q;
			buf[--charPos] = DigitOnes[r];
			buf[--charPos] = DigitTens[r];
		}

		// Get 2 digits/iteration using ints
		int q2;
		int i2 = (int) i;
		while (i2 <= -100) {
			q2 = i2 / 100;
			r = (q2 * 100) - i2;
			i2 = q2;
			buf[--charPos] = DigitOnes[r];
			buf[--charPos] = DigitTens[r];
		}

		// We know there are at most two digits left at this point.
		q2 = i2 / 10;
		r = (q2 * 10) - i2;
		buf[--charPos] = (byte) ('0' + r);

		// Whatever left is the remaining digit.
		if (q2 < 0) {
			buf[--charPos] = (byte) ('0' - q2);
		}

		if (negative) {
			buf[--charPos] = (byte) '-';
		}
		return charPos;
	}

	static int putUTF16(byte[] buf, int index, long i) {
		long q;
		int r;
		int charPos = index;

		boolean negative = (i < 0);
		if (!negative) {
			i = -i;
		}

		// Get 2 digits/iteration using longs until quotient fits into an int
		while (i <= Integer.MIN_VALUE) {
			q = i / 100;
			r = (int) ((q * 100) - i);
			i = q;
			putChar2(buf, --charPos, DigitOnes[r]);
			putChar2(buf, --charPos, DigitTens[r]);
		}

		// Get 2 digits/iteration using ints
		int q2;
		int i2 = (int) i;
		while (i2 <= -100) {
			q2 = i2 / 100;
			r = (q2 * 100) - i2;
			i2 = q2;
			putChar2(buf, --charPos, DigitOnes[r]);
			putChar2(buf, --charPos, DigitTens[r]);
		}

		// We know there are at most two digits left at this point.
		q2 = i2 / 10;
		r = (q2 * 10) - i2;
		putChar2(buf, --charPos, '0' + r);

		// Whatever left is the remaining digit.
		if (q2 < 0) {
			putChar2(buf, --charPos, '0' - q2);
		}

		if (negative) {
			putChar2(buf, --charPos, '-');
		}
		return charPos;
	}

	public static void putChar1(byte[] buf, int index, int value) {
		buf[index] = (byte) (value & 0xFF);
	}

	public static void putChar2(byte[] buf, int index, int value) {
		index <<= 1;
		buf[index++] = (byte) (value >> HI_BYTE_SHIFT);
		buf[index] = (byte) (value >> LO_BYTE_SHIFT);
	}
}