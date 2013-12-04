/**
 * 
 */
package com.rackspacecloud.client.cloudfiles.expections;

import org.apache.http.Header;
import org.apache.http.StatusLine;


public class OverQuotaException extends FilesException {

	private static final long serialVersionUID = -5007912697704455371L;

	/**
	 * @param message
	 * @param httpHeaders
	 * @param httpStatusLine
	 */
	public OverQuotaException(String message, Header[] httpHeaders,
			StatusLine httpStatusLine) {
		super(message, httpHeaders, httpStatusLine);
	}

}
