/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.impl.scanner.state;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.requests.IRequestLog;

public class CrawlerCallbackWrapper implements ICrawlerResponseProcessor {
	static ICrawlerResponseProcessor createLogging(IRequestLog requestLog, ICrawlerResponseProcessor callback) {
		return new CrawlerCallbackWrapper(requestLog, callback);
	}
	
	static ICrawlerResponseProcessor create(ICrawlerResponseProcessor callback) {
		return new CrawlerCallbackWrapper(null, callback);
	}
	
	private final boolean logRequest;
	private final IRequestLog requestLog;
	private final ICrawlerResponseProcessor wrappedCallback;
	
	private CrawlerCallbackWrapper(IRequestLog requestLog, ICrawlerResponseProcessor callback) {
		this.logRequest = (requestLog != null);
		this.requestLog = requestLog;
		this.wrappedCallback = callback;
		
	}

	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request, IHttpResponse response, Object argument) {
		if(!response.lockResponseEntity()) {
			return;
		}
		wrappedCallback.processResponse(crawler, request, response, argument);
		if(logRequest) {
			requestLog.addRequestResponse(response);
		}
	}
}
