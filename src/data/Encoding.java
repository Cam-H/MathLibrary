package data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Encoding {

	public static byte[] floatToBytes(float value) {
		return ByteBuffer.allocate(4).putFloat(value).array();
	}

	public static float bytesToFloat(byte[] bytes) {
		return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getFloat();
	}
	
}
