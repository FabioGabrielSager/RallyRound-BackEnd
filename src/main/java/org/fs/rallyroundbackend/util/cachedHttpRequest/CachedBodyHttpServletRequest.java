package org.fs.rallyroundbackend.util.cachedHttpRequest;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A {@link HttpServletRequestWrapper} implementation that caches the request body.
 * This allows the request body to be read multiple times without causing conflicts.
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {

    private byte[] cachedBody;

    /**
     * Constructs a new CachedBodyHttpServletRequest instance.
     *
     * <p>This constructor creates a CachedBodyHttpServletRequest by wrapping
     * an existing HttpServletRequest. It reads the request body from the original
     * request InputStream and caches it in a byte array. The cached request body
     * allows subsequent reads without consuming the original request body,
     * ensuring that it can be read multiple times by different components.
     *
     * @param request the original HttpServletRequest to be wrapped
     * @throws IOException if an I/O error occurs while reading the request body
     */
    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        // Call the superclass constructor to initialize the wrapped request
        super(request);

        // Obtain the InputStream for the request body from the original request
        InputStream requestInputStream = request.getInputStream();

        // Copy the request body data from the InputStream to a byte array
        // This allows subsequent reads of the request body without consuming it
        this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
    }

    /**
     * Retrieves a ServletInputStream that reads from the cached request body.
     * This method allows multiple reads of the request body.
     *
     * <p>This method returns a ServletInputStream instance initialized with
     * a {@link CachedBodyServletInputStream}, which reads data from the cached request body.
     * The cached request body contains the original request body data that was
     * read during the creation of this CachedBodyHttpServletRequest instance.
     * Reading from this input stream does not consume the request body,
     * allowing it to be read multiple times by different components.
     *
     * @return a ServletInputStream instance for reading the cached request body
     * @throws IOException if an I/O error occurs while creating the input stream
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    /**
     * Retrieves a BufferedReader that reads from the cached request body.
     * This method allows multiple reads of the request body.
     *
     * <p>This method returns a BufferedReader instance initialized with
     * an InputStreamReader, which reads data from a ByteArrayInputStream
     * containing the cached request body. The cached request body contains
     * the original request body data that was read during the creation
     * of this CachedBodyHttpServletRequest instance.
     * Reading from this buffered reader does not consume the request body,
     * allowing it to be read multiple times by different components.
     *
     * @return a BufferedReader instance for reading the cached request body
     * @throws IOException if an I/O error occurs while creating the buffered reader
     */
    @Override
    public BufferedReader getReader() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }
}