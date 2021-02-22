package com.keimons.deepjson.serializer;

import com.keimons.deepjson.UnsafeUtil;
import com.keimons.deepjson.filler.IntegerFiller;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class NormalNode$DeepJson {

	Unsafe unsafe = UnsafeUtil.getUnsafe();

	byte[] bytes1;
	byte[] bytes2;
	byte[] bytes3;
	byte[] bytes4;
	byte[] bytes5;
	byte[] bytes6;
	byte[] bytes7;
	byte[] bytes8;
	byte[] bytes9;
	byte[] bytes10;
	byte[] bytes11;
	byte[] bytes12;
	byte[] bytes13;
	byte[] bytes14;
	byte[] bytes15;
	byte[] bytes16;
	byte[] bytes17;
	byte[] bytes18;
	byte[] bytes19;

	long offset1;
	long offset2;
	long offset3;
	long offset4;
	long offset5;
	long offset6;
	long offset7;
	long offset8;
	long offset9;
	long offset10;
	long offset11;
	long offset12;
	long offset13;
	long offset14;
	long offset15;
	long offset16;
	long offset17;
	long offset18;
	long offset19;

	{
		try {
			Field field1 = NormalNode.class.getDeclaredField("我");
			Field field2 = NormalNode.class.getDeclaredField("node3_2");
			Field field3 = NormalNode.class.getDeclaredField("node3_3");
			Field field4 = NormalNode.class.getDeclaredField("node3_4");
			Field field5 = NormalNode.class.getDeclaredField("node3_5");
			Field field6 = NormalNode.class.getDeclaredField("node3_6");
			Field field7 = NormalNode.class.getDeclaredField("node3_7");
			Field field8 = NormalNode.class.getDeclaredField("node3_8");
			Field field9 = NormalNode.class.getDeclaredField("node3_9");

			Field field10 = NormalNode.class.getDeclaredField("node3_10");
			Field field11 = NormalNode.class.getDeclaredField("node3_11");
			Field field12 = NormalNode.class.getDeclaredField("node3_12");
			Field field13 = NormalNode.class.getDeclaredField("node3_13");
			Field field14 = NormalNode.class.getDeclaredField("node3_14");
			Field field15 = NormalNode.class.getDeclaredField("node3_15");
			Field field16 = NormalNode.class.getDeclaredField("node3_16");
			Field field17 = NormalNode.class.getDeclaredField("node3_17");
			Field field18 = NormalNode.class.getDeclaredField("node3_18");
			Field field19 = NormalNode.class.getDeclaredField("node3_19");

			bytes1  = new IntegerFiller(NormalNode.class, field1 ).getFieldNameByUtf16();
			bytes2  = new IntegerFiller(NormalNode.class, field2 ).getFieldNameByUtf16();
			bytes3  = new IntegerFiller(NormalNode.class, field3 ).getFieldNameByUtf16();
			bytes4  = new IntegerFiller(NormalNode.class, field4 ).getFieldNameByUtf16();
			bytes5  = new IntegerFiller(NormalNode.class, field5 ).getFieldNameByUtf16();
			bytes6  = new IntegerFiller(NormalNode.class, field6 ).getFieldNameByUtf16();
			bytes7  = new IntegerFiller(NormalNode.class, field7 ).getFieldNameByUtf16();
			bytes8  = new IntegerFiller(NormalNode.class, field8 ).getFieldNameByUtf16();
			bytes9  = new IntegerFiller(NormalNode.class, field9 ).getFieldNameByUtf16();
			bytes10 = new IntegerFiller(NormalNode.class, field10).getFieldNameByUtf16();
			bytes11 = new IntegerFiller(NormalNode.class, field11).getFieldNameByUtf16();
			bytes12 = new IntegerFiller(NormalNode.class, field12).getFieldNameByUtf16();
			bytes13 = new IntegerFiller(NormalNode.class, field13).getFieldNameByUtf16();
			bytes14 = new IntegerFiller(NormalNode.class, field14).getFieldNameByUtf16();
			bytes15 = new IntegerFiller(NormalNode.class, field15).getFieldNameByUtf16();
			bytes16 = new IntegerFiller(NormalNode.class, field16).getFieldNameByUtf16();
			bytes17 = new IntegerFiller(NormalNode.class, field17).getFieldNameByUtf16();
			bytes18 = new IntegerFiller(NormalNode.class, field18).getFieldNameByUtf16();
			bytes19 = new IntegerFiller(NormalNode.class, field19).getFieldNameByUtf16();

			offset1 = unsafe.objectFieldOffset(field1);
			offset2 = unsafe.objectFieldOffset(field2);
			offset3 = unsafe.objectFieldOffset(field3);
			offset4 = unsafe.objectFieldOffset(field4);
			offset5 = unsafe.objectFieldOffset(field5);
			offset6 = unsafe.objectFieldOffset(field6);
			offset7 = unsafe.objectFieldOffset(field7);
			offset8 = unsafe.objectFieldOffset(field8);
			offset9 = unsafe.objectFieldOffset(field9);
			offset10 = unsafe.objectFieldOffset(field10);
			offset11 = unsafe.objectFieldOffset(field11);
			offset12 = unsafe.objectFieldOffset(field12);
			offset13 = unsafe.objectFieldOffset(field13);
			offset14 = unsafe.objectFieldOffset(field14);
			offset15 = unsafe.objectFieldOffset(field15);
			offset16 = unsafe.objectFieldOffset(field16);
			offset17 = unsafe.objectFieldOffset(field17);
			offset18 = unsafe.objectFieldOffset(field18);
			offset19 = unsafe.objectFieldOffset(field19);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public int write(NormalNode object, ByteBuf buf) {
		buf.writeStartObject();
		int markIndex = buf.getWriteIndex();
		buf.writeBytes(bytes1 );buf.writeInt(unsafe.getInt(object, offset1) );
		buf.writeBytes(bytes2 );buf.writeInt(unsafe.getInt(object, offset2) );
		buf.writeBytes(bytes3 );buf.writeInt(unsafe.getInt(object, offset3) );
		buf.writeBytes(bytes4 );buf.writeInt(unsafe.getInt(object, offset4) );
		buf.writeBytes(bytes5 );buf.writeInt(unsafe.getInt(object, offset5) );
		buf.writeBytes(bytes6 );buf.writeInt(unsafe.getInt(object, offset6) );
		buf.writeBytes(bytes7 );buf.writeInt(unsafe.getInt(object, offset7) );
		buf.writeBytes(bytes8 );buf.writeInt(unsafe.getInt(object, offset8) );
		buf.writeBytes(bytes9 );buf.writeInt(unsafe.getInt(object, offset9) );
		buf.writeBytes(bytes10);buf.writeInt(unsafe.getInt(object, offset10) );
		buf.writeBytes(bytes11);buf.writeInt(unsafe.getInt(object, offset11) );
		buf.writeBytes(bytes12);buf.writeInt(unsafe.getInt(object, offset12) );
		buf.writeBytes(bytes13);buf.writeInt(unsafe.getInt(object, offset13) );
		buf.writeBytes(bytes14);buf.writeInt(unsafe.getInt(object, offset14) );
		buf.writeBytes(bytes15);buf.writeInt(unsafe.getInt(object, offset15) );
		buf.writeBytes(bytes16);buf.writeInt(unsafe.getInt(object, offset16) );
		buf.writeBytes(bytes17);buf.writeInt(unsafe.getInt(object, offset17) );
		buf.writeBytes(bytes18);buf.writeInt(unsafe.getInt(object, offset18) );
		buf.writeBytes(bytes19);buf.writeInt(unsafe.getInt(object, offset19) );
		buf.writeEndObject(markIndex != buf.getWriteIndex());
		return 0;
	}
}