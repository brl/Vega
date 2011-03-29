package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class FileProcessor implements ICrawlerResponseProcessor {
	private final InjectionChecks injectionChecks = new InjectionChecks();
	private final ParametricCheckHandler parametricChecks = new ParametricCheckHandler();
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request,
			IHttpResponse response, Object argument) {
		final IModuleContext ctx = (IModuleContext) argument;
		
		final IPathState ps = ctx.getPathState();
		ctx.debug("FileProcessor: "+ request.getMethod() + " "+ request.getURI());
		ps.getPath().setVisited(true);

		ps.setResponse(response);
		if(response.isFetchFail()) {
			ctx.error(request, response, "during initial file fetch");
		}
		
		if((ps.get404Parent() == null && response.getResponseCode() == 404) || ps.hasParent404FingerprintMatchingThis()) {
			ps.setPageMissing();
		} else {
			if(response.getResponseCode() > 400) {
				ctx.debug("No access code = "+ response.getResponseCode());
			}
			if(ps.getParentState() == null || !ps.getParentState().matchesPathFingerprint(ps.getPathFingerprint())) {
				ctx.responseChecks(request, response);
				if(ps.get404Parent() != null && !ps.isParametric()) {
					// XXX secondary ext init
				}
			}
			
			if(ps.getPath().getPathType() == PathType.PATH_FILE) {
				// XXX check case
			}
		}
		
		ps.unlockChildren();
		if(ps.isParametric()) {
			parametricChecks.init(ps);
		} else {
			injectionChecks.intitialize(ps);
		}
	}

}
