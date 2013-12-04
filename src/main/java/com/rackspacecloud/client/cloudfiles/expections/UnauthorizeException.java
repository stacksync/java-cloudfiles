package com.rackspacecloud.client.cloudfiles.expections;

import org.apache.http.Header;
import org.apache.http.StatusLine;

public class UnauthorizeException extends FilesException {

	private static final long serialVersionUID = -6085928442237316803L;

	/**
	 * @param message
	 * @param httpHeaders
	 * @param httpStatusLine
	 */
	public UnauthorizeException(String message, Header[] httpHeaders,
			StatusLine httpStatusLine) {
		super(message, httpHeaders, httpStatusLine);
	}
}
