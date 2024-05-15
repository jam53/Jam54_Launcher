package com.jam54.jam54_launcher.Updating;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class is designed to wrap an existing OutputStream and prevent it from being closed or flushed.
 * It provides a transparent wrapper around the target OutputStream, overriding the close() and flush()
 * methods to be NOOP (no operation), effectively preventing the wrapped OutputStream from being closed or flushed.
 * This can be useful when dealing with libraries or code that incorrectly closes or flushes streams
 * that you want to remain open for further operations.
 */
public class NoCloseOutputStream extends OutputStream
{
    private final OutputStream target;

    /**
     * Constructs a NoCloseOutputStream with the provided target OutputStream.
     *
     * @param target the target OutputStream to wrap
     * @throws NullPointerException if the provided OutputStream is null
     */
    public NoCloseOutputStream(OutputStream target) throws NullPointerException
    {
        if (target == null)
        {
            throw new NullPointerException("Provided output stream is null");
        }
        this.target = target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int b) throws IOException
    {
        target.write(b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        target.write(b, off, len);
    }

    /**
     * Flushes the output stream. This implementation does nothing.
     * Prevents the wrapped OutputStream from being flushed.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void flush() throws IOException
    {
        // NOOP: This method intentionally left blank
    }

    /**
     * Closes the output stream. This implementation does nothing.
     * Prevents the wrapped OutputStream from being closed.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException
    {
        // NOOP: This method intentionally left blank
    }

    /**
     * Manually closes the output stream by calling the superclass's close method.
     * This method is provided for explicit closing of the stream if needed.
     *
     * @throws IOException if an I/O error occurs
     */
    public void manualClose() throws IOException {
        super.close();
    }
}
