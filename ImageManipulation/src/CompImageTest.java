import junit.framework.TestCase;

public class CompImageTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
    }

    public void testIntToTwoBytes() throws Exception {
        assertTrue((byte)(1-128) == CompImage.intToTwoBytes(1)[0] && (byte)(0-128) == CompImage.intToTwoBytes(1)[1]);
        assertTrue((byte)(255-128) == CompImage.intToTwoBytes(255)[0] && (byte)(0-128) == CompImage.intToTwoBytes(255)[1]);
        assertTrue((byte)(0-128) == CompImage.intToTwoBytes(256)[0] && (byte)(1-128) == CompImage.intToTwoBytes(256)[1]);
        assertTrue((byte)(255-128) == CompImage.intToTwoBytes(65535)[0] && (byte)(255-128) == CompImage.intToTwoBytes(65535)[1]);
    }

    public void testTwoBytesToInt() throws Exception {
        assertTrue(CompImage.twoBytesToInt(new byte[] {(byte)0x00, (byte)0x00}) == 0x8080);
        assertTrue(CompImage.twoBytesToInt(new byte[] {(byte)0b0111_1111, (byte)0b0111_1111}) == 0xFFFF);

        assertEquals(0, CompImage.twoBytesToInt(CompImage.intToTwoBytes(0)));
        assertEquals(255, CompImage.twoBytesToInt(CompImage.intToTwoBytes(255)));
        assertEquals(127, CompImage.twoBytesToInt(CompImage.intToTwoBytes(127)));
    }

}