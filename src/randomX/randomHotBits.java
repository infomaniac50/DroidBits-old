package randomX;

import java.net.*;
import java.io.*;


/**
    Implementation of a <b>randomX</b>-compliant class which obtains
    genuine random data from <a href="http://www.fourmilab.ch/">John
    Walker</a>'s <a href="http://www.fourmilab.ch/hotbits/">HotBits</a>
    radioactive decay random sequence generator.

    <p>
    Designed and implemented in July 1996 by
    <a href="http://www.fourmilab.ch/">John Walker</a>.
*/


public class randomHotBits extends randomX {
	long state;
	int nuflen = 256, buflen = 0;
	byte[] buffer;
	int bufptr = -1;
	
	public randomHotBits()
	{
        buffer = new byte[nuflen];
	}

    /*  Private method to fill buffer from HotBits server.  */
    private void fillBuffer()
    	throws java.io.IOException
	{
        URL u = new URL("http://www.fourmilab.ch/cgi-bin/uncgi/Hotbits?nbytes=128&fmt=bin");
        InputStream s = u.openStream();
        int l;
        buflen = 0;
        while ((l = s.read()) != -1) {
            buffer[buflen++] = (byte) l;
        }
        s.close();
        bufptr = 0;
    }

    /** Get next byte from generator.
	@return the next byte from the generator.
    */
    public byte nextByte()  {
    	synchronized (buffer) {
    		if (bufptr < 0 || bufptr >= buflen) {
            	try {
            		fillBuffer();
                } catch (IOException e) {
                	throw new RuntimeException("Cannot obtain HotBits");
                }
    		}
    		return buffer[++bufptr];
    	}
    }
};
