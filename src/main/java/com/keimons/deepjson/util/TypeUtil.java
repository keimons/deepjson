package com.keimons.deepjson.util;

/**
 * 类型工具
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class TypeUtil {

	public static Boolean caseToBoolean(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		if (value instanceof String) {
			return Boolean.parseBoolean((String) value);
		}
		throw new TypeCaseException("type " + value.getClass() + " can not cast to Boolean, value: " + value);
	}

	public static Character caseToCharacter(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Character) {
			return (Character) value;
		}
		if (value instanceof String) {
			String v = (String) value;
			if (v.length() == 0) {
				return null;
			}
			if (v.length() == 1) {
				return v.charAt(0);
			}
		}
		throw new TypeCaseException("type " + value.getClass() + " can not cast to Character, value: " + value);
	}

	public static Byte caseToByte(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Byte) {
			return (Byte) value;
		}
		if (value instanceof Number) {
			return ((Number) value).byteValue();
		}
		if (value instanceof String) {
			return (byte) Long.parseLong((String) value);
		}
		throw new TypeCaseException("type " + value.getClass() + " can not cast to Byte, value: " + value);
	}

	public static Short caseToShort(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Short) {
			return (Short) value;
		}
		if (value instanceof Number) {
			return ((Number) value).shortValue();
		}
		if (value instanceof String) {
			return (short) Long.parseLong((String) value);
		}
		throw new TypeCaseException("type " + value.getClass() + " can not cast to Short, value: " + value);
	}

	public static Integer caseToInteger(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Integer) {
			return (Integer) value;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		if (value instanceof String) {
			return (int) Long.parseLong((String) value);
		}
		throw new TypeCaseException("type " + value.getClass() + " can not cast to Integer, value: " + value);
	}

	public static Long caseToLong(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Long) {
			return (Long) value;
		}
		if (value instanceof Number) {
			return ((Number) value).longValue();
		}
		if (value instanceof String) {
			return Long.parseLong((String) value);
		}
		throw new TypeCaseException("type " + value.getClass() + " can not cast to Long, value: " + value);
	}

	public static Float caseToFloat(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Float) {
			return (Float) value;
		}
		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}
		if (value instanceof String) {
			return (float) Double.parseDouble((String) value);
		}
		throw new TypeCaseException("type " + value.getClass() + " can not cast to Float, value: " + value);
	}

	public static Double caseToDouble(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Double) {
			return (Double) value;
		}
		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}
		if (value instanceof String) {
			return Double.parseDouble((String) value);
		}
		throw new TypeCaseException("type " + value.getClass() + " can not cast to Double, value: " + value);
	}
}